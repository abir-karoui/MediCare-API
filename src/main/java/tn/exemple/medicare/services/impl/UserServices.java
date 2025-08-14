package tn.exemple.medicare.services.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.configs.JwtService;
import tn.exemple.medicare.controllers.authcontrollers.AuthenticationRequest;
import tn.exemple.medicare.controllers.authcontrollers.AuthenticationResponse;
import tn.exemple.medicare.controllers.authcontrollers.ChangePasswordRequest;
import tn.exemple.medicare.entities.*;
import tn.exemple.medicare.entities.auth.*;
import tn.exemple.medicare.entities.dto.UserDto;
import tn.exemple.medicare.entities.invitation.Invitation;
import tn.exemple.medicare.enums.TypeCode;
import tn.exemple.medicare.enums.TypeGender;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.exceptions.BusinessErrorCode;
import tn.exemple.medicare.exceptions.BusinessException;
import tn.exemple.medicare.fileServer.FileUploadImpl;
import tn.exemple.medicare.mappers.UserMapper;
import tn.exemple.medicare.repositories.*;
import tn.exemple.medicare.services.IActivityLogService;
import tn.exemple.medicare.services.INotificationServices;
import tn.exemple.medicare.services.IUserSevices;

import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor

public class UserServices implements IUserSevices {

    private final IUserRepository iUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private  final JwtService jwtService ;
    private final CodeRepository codeRepository;
    private final EmailService emailService ;
    private final RefreshTokenRepositroty refreshTokenRepositroty;
    private final FileUploadImpl  fileUpload;
    private  final AuthService authService;
    private  final INotificationServices notificationServices;

    private final IActivityLogService activityLogService;

    private final Map<String, Map<String, Object>> tempUserCache = new ConcurrentHashMap<>();

    @Override
    public void register(Map<String, Object> userMap, MultipartFile photo, MultipartFile medicalCard) throws Exception {
        String email = (String) userMap.get("email");
        String roleStr = (String) userMap.get("role");

        if (iUserRepository.existsByEmail(email)) {
            throw new BusinessException(BusinessErrorCode.EMAIL_ALREADY_EXISTS);
        }


        if (photo != null && !photo.isEmpty()) {
            userMap.put("photo", fileUpload.uploadImage(photo)); // anciennement "photoUrl"
        }
        if (medicalCard != null && !medicalCard.isEmpty()) {
            userMap.put("medicalCard", fileUpload.uploadImage(medicalCard)); // anciennement "medicalCardUrl"
        }


        // Chiffrer le mot de passe avant de le stocker temporairement
        String rawPassword = (String) userMap.get("password");
        userMap.put("password", passwordEncoder.encode(rawPassword));

        // Stocker en cache temporaire (clé = email)
        tempUserCache.put(email, userMap);

        // Générer le code
        String code = generateAndSaveCode(email);

        // Envoyer le code par email
        emailService.sendEmail(
                email,
                (String) userMap.get("firstname"),
                code,
                "Account Activation",
                TypeCode.ACTIVATION
        );
    }


    public void sendValidationEmail(User user ) throws MessagingException {
        var newToken = generateAndSaveActivationCode(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getUsername(),
                newToken,
                "Account activation" ,
                TypeCode.ACTIVATION);

    }
    private String generateAndSaveCode(String email) {
        String generateCode = generateCode(6);
        Codes codes = Codes.builder()
                .code(generateCode)
                .email(email) // <-- très important
                .typecode(TypeCode.ACTIVATION)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();
        codeRepository.save(codes);
        return generateCode;
    }

    private String generateAndSaveActivationCode(User user ) {
        String generateCode = generateCode(6);
        Codes codes = Codes.builder()
                .code(generateCode)
                .typecode(TypeCode.ACTIVATION)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        codeRepository.save(codes);
        return generateCode;
    }

    private String generateCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i<length; i++){
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return  codeBuilder.toString();
    }

   @Override
   public AuthenticationResponse activateAccount(String code) throws MessagingException {
       // Vérifie si le code existe
       Codes savedCode = codeRepository.findByCode(code)
               .orElseThrow(() -> new BusinessException(BusinessErrorCode.CODE_INCORRECT));

       // Vérifie la date d'expiration du code
       if (savedCode.getExpiredAt().isBefore(LocalDateTime.now())) {
           throw new BusinessException(BusinessErrorCode.CODE_Expired);
       }

       // Récupère l'utilisateur temporaire en cache à partir de l'email
       String email = savedCode.getEmail();
       if (!tempUserCache.containsKey(email)) {
           throw new BusinessException(BusinessErrorCode.NOT_FOUND);
       }

       Map<String, Object> userMap = tempUserCache.remove(email);
       String roleStr = (String) userMap.get("role");
       TypeRole role = TypeRole.valueOf(roleStr);

       ObjectMapper objectMapper = new ObjectMapper();
       User user;

       if (role == TypeRole.DOCTOR) {
           // Création d'un doctor
           user = objectMapper.convertValue(userMap, Doctor.class);
           ((Doctor) user).setMedicalCard((String) userMap.get("medicalCard"));
           ((Doctor) user).setMedicalCardVerified(false); // En attente de vérification

       } else if (role == TypeRole.PATIENT) {
           user = objectMapper.convertValue(userMap, Patient.class);
       } else {
           user = objectMapper.convertValue(userMap, User.class);
       }

       // Initialisation des champs communs
       user.setEnabled(true);
       user.setAccountLocked(false);
       user.setPhoto((String) userMap.get("photo"));

       // Sauvegarde du nouvel utilisateur
       var savedUser = iUserRepository.save(user);
       String fullName = savedUser.getFirstname() + " " + savedUser.getLastname();


       activityLogService.logActivity(
               "New registration",
               fullName,
               fullName + " has registered as " + role
       );



       // Sauvegarde des infos du code
       savedCode.setUser(savedUser);
       savedCode.setValidateAt(LocalDateTime.now());
       codeRepository.save(savedCode);

       if (role == TypeRole.DOCTOR) {
           notificationServices.notifyAdminNewDoctor((Doctor) savedUser);
       }
       // Génération des tokens
       var jwtToken = jwtService.generateToken(savedUser);
       var refreshToken = jwtService.generateRefreshToken(savedUser);
       saveRefreshToken(savedUser, refreshToken);

       return AuthenticationResponse.builder()
               .accessToken(jwtToken)
               .refreshToken(refreshToken)
               .build();
   }
    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            var user = ((User)auth.getPrincipal());

            if (user instanceof Doctor) {
                Doctor doctor = (Doctor) user;
                if (!doctor.isMedicalCardVerified()) {
                    throw new BusinessException(BusinessErrorCode.MEDICAL_CARD_NOT_VERIFIED);
                }
            }
            var claims = new HashMap<String, Object>();
            claims.put("fullName" , user.fullName());
            claims.put("userId", user.getId());
            var jwtToken = jwtService.generateToken(claims , user);
            var refreshToken = jwtService.generateRefreshToken(user);
            Long extractedUserId = jwtService.extractUserId(jwtToken);
            System.out.println("Extracted User ID from JWT: " + extractedUserId);
            saveRefreshToken(user, refreshToken);
            activityLogService.logActivity(
                    "Login",
                    user.fullName(),
                    " Successfully logged into the platform "
            );
            return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
        } catch (DisabledException e) {
            throw new BusinessException(BusinessErrorCode.ACCOUNT_DISABLED);
        }catch (BadCredentialsException e) {
            throw e;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Une erreur est survenue lors de l'authentification.", e);
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest request , Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (! passwordEncoder.matches(request.getCurrentPassword() , user.getPassword())){
            throw new   BusinessException(BusinessErrorCode.INCORRECT_CURRENT_PASSWORD);
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())){
            throw  new BusinessException(BusinessErrorCode.NEW_PASSWORD_DOSES_NOT_MATCH);
        }
        user.setPassword((passwordEncoder.encode(request.getNewPassword()))); //update
        iUserRepository.save(user);
    }
    @Override
    //Renouveler access token avec Refresh
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7); // "Bearer " a une longueur de 7
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var userDetails = this.iUserRepository.findByEmail(userEmail).orElseThrow();
            var storedRefreshToken = refreshTokenRepositroty.findByRefreshToken(refreshToken) //bch kifh user yabaath refreshToken bch yaamil access token jdid yet2aed mawjouf f database
                    .orElseThrow(() -> new RuntimeException("Refresh token NOT FOUND"));

            if (jwtService.isTokenValid(refreshToken, userDetails) && !storedRefreshToken.getExpiredAt().isBefore(Instant.now())) {
                HashMap<String, Object> extraClaims = new HashMap<>();
                extraClaims.put("userId", userDetails.getId());
                var accessToken = jwtService.generateToken(userDetails);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            } else {
                throw new RuntimeException("Invalid or expired refresh token");
            }
        }
    }
    private void saveRefreshToken(User user, String refreshToken) {
        var token = RefreshToken.builder()
                .user(user)
                .createdAt(Instant.now())
                .refreshToken(refreshToken)
                .expiredAt((Instant.now().plusMillis(jwtService.getRefreshTokenExpiration()))).build();
        refreshTokenRepositroty.save(token);
    }



    @Override
    public List<User> retrieveAllUsers() {
        return iUserRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return iUserRepository.findById(id);
    }
    @Override
    public List<UserDto> getUsersByRole() {
        TypeRole role = authService.getAuthenticatedUserRole();

        List<User> users;

        if (role == TypeRole.DOCTOR) {
            users = iUserRepository.findAllByRole(TypeRole.PATIENT);
        } else if (role == TypeRole.PATIENT) {
            users = iUserRepository.findAllByRole(TypeRole.DOCTOR);
        } else {
            return List.of();
        }

        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public void deleteUserById(Long id) {
        if (!iUserRepository.existsById(id)) {
            throw new EntityNotFoundException("User with Id: '" +id + "' not found");
        }
        iUserRepository.deleteById(id);
    }
    @Transactional
    @Override
    public void deleteMe(String password) {
        Long userId = authService.getAuthenticatedUserId();
        User user = iUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with Id: '" + userId + "' not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(BusinessErrorCode.INCORRECT_PASSWORD);
        }
        iUserRepository.deleteById(userId);
    }


    @Override
    public void deleteAllUser() {
        iUserRepository.deleteAll();
    }
    @Override
    public void requestPasswordReset(String email) throws MessagingException {
        // Récupération de l'utilisateur
        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND));

        // Génération du code de réinitialisation
        String generateCode = generateCode(6);

        // Création et sauvegarde de l'entité Codes
        Codes codes = Codes.builder()
                .code(generateCode)
                .typecode(TypeCode.RESET)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .email(user.getEmail()) // ⚠️ Assure que email n'est pas null
                .build();
        codeRepository.save(codes);

        // Envoi de l'email avec le même code
        emailService.sendEmail(
                user.getEmail(),
                user.getUsername(),
                generateCode,
                "Password Reset",
                TypeCode.RESET
        );
    }

    @Override
    public boolean verifyResetCode(String email, String code) {
        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND));

        Codes codes = codeRepository.findByCodeAndUser(code, user)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CODE_INCORRECT));

        if (codes.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(BusinessErrorCode.CODE_Expired);
        }
        return true;
    }
    @Override
    public void resetPassword(String email, String code, String newPassword) {
        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND));

        Codes codes = codeRepository.findByCodeAndUser(code, user)
                .orElseThrow(() -> new  BusinessException(BusinessErrorCode.CODE_INCORRECT));

        if (codes.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(BusinessErrorCode.CODE_Expired);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        iUserRepository.save(user);
        codeRepository.delete(codes);
    }
    @Transactional
    public void logout(User user) {
        refreshTokenRepositroty.deleteByUser(user);
    }

    @Override
    public UserDto getMe() {
        Long userId = authService.getAuthenticatedUserId();
        User user = iUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
        return UserMapper.toDto(user); }
    @Override
    public AuthenticationResponse updateProfile(Map<String, Object> userParams, MultipartFile file) throws Exception {

        // Récupérer l'ID de l'utilisateur authentifié
        Long userId = authService.getAuthenticatedUserId();
        // Rechercher l'utilisateur dans la base de données
        User user = iUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with Id: '" + userId + "' not found"));

        // Mise à jour du prénom si présent et non vide
        if (userParams.containsKey("firstname")) {
            String firstname = (String) userParams.get("firstname");
            if (firstname != null && !firstname.trim().isEmpty()) {
                user.setFirstname(firstname);
            }
        }

        // Mise à jour du nom si présent et non vide
        if (userParams.containsKey("lastname")) {
            String lastname = (String) userParams.get("lastname");
            if (lastname != null && !lastname.trim().isEmpty()) {
                user.setLastname(lastname);
            }
        }

        // Mise à jour de l'email si présent et non vide, et vérification si email existe déjà
        if (userParams.containsKey("email")) {
            String newEmail = (String) userParams.get("email");
            if (newEmail != null && !newEmail.trim().isEmpty() && !newEmail.equals(user.getEmail())) {
                Optional<User> existingUserWithEmail = iUserRepository.findByEmail(newEmail);
                if (existingUserWithEmail.isPresent() && existingUserWithEmail.get().getId() != user.getId()) {
                    throw new BusinessException(BusinessErrorCode.EMAIL_ALREADY_EXISTS);
                }
                user.setEmail(newEmail);
            }
        }

        // Mise à jour du téléphone si présent et non vide
        if (userParams.containsKey("phone")) {
            String phone = (String) userParams.get("phone");
            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone);
            }
        }

        // Mise à jour de l'adresse si présente et non vide
        if (userParams.containsKey("address")) {
            String address = (String) userParams.get("address");
            if (address != null && !address.trim().isEmpty()) {
                user.setAddress(address);
            }
        }

        // Mise à jour du genre si présent et non vide
        if (userParams.containsKey("gender")) {
            String genderStr = (String) userParams.get("gender");
            if (genderStr != null && !genderStr.trim().isEmpty()) {
                try {
                    TypeGender gender = TypeGender.valueOf(genderStr.toUpperCase());
                    user.setGender(gender);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid gender value: " + genderStr);
                }
            }
        }

        // Mise à jour de la spécialité pour les docteurs
        if (user.getRole() == TypeRole.DOCTOR && user instanceof Doctor doctor) {
            if (userParams.containsKey("speciality")) {
                String speciality = (String) userParams.get("speciality");
                if (speciality != null && !speciality.trim().isEmpty()) {
                    doctor.setSpeciality(speciality);
                }
            }
        }

        // Mise à jour de l'âge pour les patients
        if (user.getRole() == TypeRole.PATIENT && user instanceof Patient patient) {
            if (userParams.containsKey("age")) {
                String age = (String) userParams.get("age");
                if (age != null && !age.trim().isEmpty()) {
                    patient.setAge(age);
                }
            }
        }

        // Traitement du fichier photo si présent
        if (file != null && !file.isEmpty()) {
            String pictureUrl = fileUpload.uploadImage(file);
            user.setPhoto(pictureUrl);
        }

        // Sauvegarde de l'utilisateur mis à jour
        User updatedUser = iUserRepository.save(user);

        // Création des claims pour le JWT
        var claims = new HashMap<String, Object>();
        claims.put("fullName", updatedUser.fullName());
        claims.put("userId", updatedUser.getId());

        // Génération du token d'accès et du refresh token
        var jwtToken = jwtService.generateToken(claims, updatedUser);
        var refreshToken = jwtService.generateRefreshToken(updatedUser);

        // Sauvegarde du refresh token
        saveRefreshToken(updatedUser, refreshToken);

        // Retourner la réponse avec les tokens
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
    @Override
    @Transactional
    public void resendActivationCode(String email) throws Exception {
        if (!tempUserCache.containsKey(email)) {
            throw new BusinessException(BusinessErrorCode.NOT_FOUND);
        }

        Map<String, Object> userMap = tempUserCache.get(email);
        String firstname = (String) userMap.get("firstname");

        // Supprimer anciens codes (facultatif)
        codeRepository.deleteAllByEmailAndTypecode(email, TypeCode.ACTIVATION);

        // Générer un nouveau code
        String newCode = generateCode(6);

        Codes code = Codes.builder()
                .email(email)
                .code(newCode)
                .typecode(TypeCode.ACTIVATION)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();

        codeRepository.save(code);

        // Renvoyer l'email
        emailService.sendEmail(email, firstname, newCode, "Account Activation", TypeCode.ACTIVATION);
    }




}
