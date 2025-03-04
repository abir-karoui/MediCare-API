package tn.exemple.medicare.services.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.configs.JwtService;
import tn.exemple.medicare.controllers.AuthenticationRequest;
import tn.exemple.medicare.controllers.AuthenticationResponse;
import tn.exemple.medicare.controllers.ChangePasswordRequest;
import tn.exemple.medicare.entities.*;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.repositories.RefreshTokenRepositroty;
import tn.exemple.medicare.repositories.TokenRepository;
import tn.exemple.medicare.services.IUserSevices;

import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor

public class UserServices implements IUserSevices {
    private final IUserRepository iUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private  final JwtService jwtService ;
    private  final TokenRepository tokenRepository;
    private final EmailService emailService ;
    private final RefreshTokenRepositroty refreshTokenRepositroty;


    @Value("${activation.url}")
    private String activationUrl;
   /* public UserServices(IUserRepository iUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, TokenRepository tokenRepository, EmailService emailService) {
        this.iUserRepository = iUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }*/

    @Override

    public AuthenticationResponse  addUser(User user) throws MessagingException {
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
        sendValidationEmail(savedUser);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }
    public void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
            emailService.sendEmail(
                    user.getEmail(),
                    user.getUsername(),
                    activationUrl,
                    newToken,
                    "Account activation" );

    }
    private String generateAndSaveActivationToken(User user) {
        String generateToken = generateActivationCode(6);
        Token token = new Token();
        token.setToken(generateToken);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiredAt(LocalDateTime.now().plusMinutes(15));
        token.setUser(user);
        tokenRepository.save(token);
        return generateToken;
    }

    private String generateActivationCode(int length) {
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
    public AuthenticationResponse login(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var claims = new HashMap<String, Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("fullName" , user.fullName());
        var jwtToken = jwtService.generateToken(claims , user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveRefreshToken(user, refreshToken);
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }

    @Override
    public void changePassword(ChangePasswordRequest request , Principal connectedUser) {
         var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
    if (! passwordEncoder.matches(request.getCurrentPassword() , user.getPassword())){
         throw new IllegalStateException("Wrong password");
    }
    if (!request.getNewPassword().equals(request.getConfirmationPassword())){
        throw   new IllegalStateException("Password are not the same");
    }
    user.setPassword((passwordEncoder.encode(request.getNewPassword()))); //update
    iUserRepository.save(user);
    }

   /* @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String refreshToken ;
        final  String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7); //7 c'est Bearer avec espace en fin
        userEmail  = jwtService.extractUsername(refreshToken);
        if(userEmail != null) {
            var  userDetails =this.iUserRepository.findByEmail(userEmail).orElseThrow();
            if(jwtService.isTokenValid(refreshToken, userDetails)){
               var accessToken = jwtService.generateToken(userDetails);
               var authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
               new ObjectMapper().writeValue(response.getOutputStream(), authResponse);


            }
        }
    }*/
   @Override
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
               throw new RuntimeException("Refresh token invalide ou expiré");
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
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid Token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())){
            sendValidationEmail(savedToken.getUser());
            throw  new RuntimeException("Activation token has expired , Anew token has been send ");
        }
        var user = iUserRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        iUserRepository.save(user);
        savedToken.setValidateAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
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


}




   /* private void revokeAllUserToken(User user){
        var validUserTokens =tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return; ;
            validUserTokens.forEach(token -> {
                token.setValidateAt();
                token.setExpiredAt();

            });
            tokenRepository.saveAll(validUserTokens);
    }*/