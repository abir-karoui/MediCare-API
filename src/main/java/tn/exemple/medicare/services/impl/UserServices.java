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
import tn.exemple.medicare.services.IUserSevices;

import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
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
    private  final IDoctorRepository iDoctorRepository;

    public AuthenticationResponse register(Map<String, Object> userMap, MultipartFile photo, MultipartFile medicalCard) throws Exception
    {

        String roleStr = (String) userMap.get("role");
        TypeRole role = TypeRole.valueOf(roleStr);

        ObjectMapper objectMapper = new ObjectMapper();
        User user;
        if (role == TypeRole.DOCTOR) {
            user = objectMapper.convertValue(userMap, Doctor.class);
        } else if (role == TypeRole.PATIENT) {
            user = objectMapper.convertValue(userMap, Patient.class);
        } else {
            user = objectMapper.convertValue(userMap, User.class);
        }
        if (iUserRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException(BusinessErrorCode.EMAIL_ALREADY_EXISTS);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountLocked(false);
        user.setEnabled(false);
        user.setPhoto(photo != null && !photo.isEmpty() ? fileUpload.uploadImage(photo) : null);

        if (user instanceof Doctor doctor) {
            doctor.setMedicalCard(medicalCard != null && !medicalCard.isEmpty() ? fileUpload.uploadImage(medicalCard) : null);
            doctor.setMedicalCardVerified(false);
        }

        var savedUser = iUserRepository.save(user);
        var jwtToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        saveRefreshToken(savedUser, refreshToken);
        sendValidationEmail(savedUser);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
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
    public void activateAccount(String code) throws MessagingException {
        Codes savedCode = codeRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CODE_INCORRECT));

        if (savedCode.getTypecode() != TypeCode.ACTIVATION) {
            throw new BusinessException(BusinessErrorCode.CODE_Expired);
        }

        if (LocalDateTime.now().isAfter(savedCode.getExpiredAt())) {
            sendValidationEmail(savedCode.getUser());
            throw new BusinessException(BusinessErrorCode.CODE_INCORRECT);
        }

        var user = iUserRepository.findById(savedCode.getUser().getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND));
        user.setEnabled(true);
        iUserRepository.save(user);
        savedCode.setValidateAt(LocalDateTime.now());
        codeRepository.save(savedCode);
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

    public void requestPasswordReset(String email) throws MessagingException { //hedhi bch nlawej user b mail w nabaath token ctt
        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND));
        String generateCode = generateCode(6);
        Codes codes = Codes.builder()
                .code(generateCode)
                .typecode(TypeCode.RESET)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        codeRepository.save(codes);
        sendResetEmail(user , generateCode);
    }
    public void sendResetEmail(User user , String code) throws MessagingException {
        var newToken = generateAndSaveActivationCode(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getUsername(),
                newToken,
                "Password Reset" ,
                TypeCode.RESET);

    }

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



}

