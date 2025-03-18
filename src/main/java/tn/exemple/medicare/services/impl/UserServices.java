package tn.exemple.medicare.services.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.configs.JwtService;
import tn.exemple.medicare.controllers.authcontrollers.AuthenticationRequest;
import tn.exemple.medicare.controllers.authcontrollers.AuthenticationResponse;
import tn.exemple.medicare.controllers.authcontrollers.ChangePasswordRequest;
import tn.exemple.medicare.entities.*;
import tn.exemple.medicare.entities.auth.*;
import tn.exemple.medicare.enums.TypeCode;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.repositories.*;
import tn.exemple.medicare.services.IUserSevices;

import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender mailSender;
    private final IDiseasesRepository iDiseasesRepository ;



    @Override

    public AuthenticationResponse  singUp(User user) throws MessagingException {
        Optional<User> existingUser = iUserRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        User savedUser;
        if (user.getRole() == TypeRole.DOCTOR) {
            Doctor doctor = (Doctor) user;
            savedUser = iUserRepository.save(doctor);
        } else if (user.getRole() == TypeRole.PATIENT) {
            Patient patient = (Patient) user;

            savedUser = iUserRepository.save(patient);
        } else {
            throw new IllegalArgumentException("Invalid user role");
        }

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

        Codes savedCode = codeRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Invalid Code"));
        if (savedCode.getTypecode() != TypeCode.ACTIVATION) {
            throw new RuntimeException("Invalid activation code");
        }
        if (LocalDateTime.now().isAfter(savedCode.getExpiredAt())){
            sendValidationEmail(savedCode.getUser());
            throw  new RuntimeException("Activation code has expired , Anew code has been send ");
        }
        var user = iUserRepository.findById(savedCode.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        iUserRepository.save(user);
        savedCode.setValidateAt(LocalDateTime.now());
        codeRepository.save(savedCode);
    }


    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
       try { var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var claims = new HashMap<String, Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("fullName" , user.fullName());
        var jwtToken = jwtService.generateToken(claims , user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    } catch(BadCredentialsException e){
           throw new RuntimeException("Email or password incorrect", e);
       } catch (Exception e) {
           throw new RuntimeException("Une erreur est survenue lors de l'authentification.");
       }
    }


    @Override
    public void changePassword(ChangePasswordRequest request , Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (! passwordEncoder.matches(request.getCurrentPassword() , user.getPassword())){
            throw new IllegalStateException("Wrong password");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())){
            throw   new IllegalStateException("Password and confirmation do not match");
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
    public List<User> getUsersByRole(TypeRole role) {
        return  iUserRepository.findAllByRole(role);
    }

    @Override
    public User UpdateUser(Long id, User user) {
        if (!iUserRepository.existsById(id)) {
            throw new EntityNotFoundException("User with Id: '" +id + "' not found");
        }
        user.setId(id);
        return iUserRepository.save(user);
    }
    @Transactional
    @Override
    public void deleteUserById(Long id) {
        if (!iUserRepository.existsById(id)) {
            throw new EntityNotFoundException("User with Id: '" +id + "' not found");
        }
        iUserRepository.deleteById(id);
    }

    @Override
    public void deleteAllUser() {
        iUserRepository.deleteAll();
    }

    public void requestPasswordReset(String email) throws MessagingException { //hedhi bch nlawej user b mail w nabaath token ctt
        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not found"));
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
                .orElseThrow(() -> new RuntimeException("User Not found"));

        Codes codes = codeRepository.findByCodeAndUser(code, user)
                .orElseThrow(() -> new RuntimeException("Invalid or expired code"));

        if (codes.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("The token has expired");
        }

        return true;
    }
    public void resetPassword(String email, String code, String newPassword) {
        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not found"));

        Codes codes = codeRepository.findByCodeAndUser(code, user)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (codes.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("The token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        iUserRepository.save(user);
        codeRepository.delete(codes);
    }


    /*public void resetPassword(String code, String newPassword) {
        Codes codes = codeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));
        if (codes.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("The token has expired");
        }
        User user = codes.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        iUserRepository.save(user);
        codeRepository.delete(codes);
    }*/

    @Transactional
    public void logout(User user) {
        refreshTokenRepositroty.deleteByUser(user);
    }

}
