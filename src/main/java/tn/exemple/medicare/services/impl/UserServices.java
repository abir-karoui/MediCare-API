package tn.exemple.medicare.services.impl;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.configs.JwtService;
import tn.exemple.medicare.controllers.AuthenticationRequest;
import tn.exemple.medicare.controllers.AuthenticationResponse;
import tn.exemple.medicare.controllers.ChangePasswordRequest;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.Token;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.repositories.TokenRepository;
import tn.exemple.medicare.services.IUserSevices;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices implements IUserSevices {
    private final IUserRepository iUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private  final JwtService jwtService ;
    private  final TokenRepository tokenRepository;
    private final EmailService emailService ;

    @Value("${activation.url}")
    private String activationUrl;
    public UserServices(IUserRepository iUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, TokenRepository tokenRepository, EmailService emailService) {
        this.iUserRepository = iUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Override

    public User addUser(User user) throws MessagingException {
        Optional<User> existingUser = iUserRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
        sendValidationEmail(savedUser);
        return savedUser;
    }
    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
            emailService.sendEmail(
                    user.getEmail(),
                    user.getUsername(),
                    activationUrl,
                    newToken,
                    "Account activation" );

    }

   /* private void sendValidationEmail(User user) {
        var newToken = generateAndSaveActivationToken(user);
        //send email


    }*/

    /*private String generateAndSaveActivationToken(User user) {
        //generate Token
        String generateToken = generateActivationCode(6);
        var token = Token.builder()
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
      tokenRepository.save(token);
      return generateToken;
    }*/
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

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var claims = new HashMap<String, Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("fullName" , user.fullName());
        var jwtToken = jwtService.generateToken(claims , user);

        return new AuthenticationResponse(jwtToken);
        //return AuthenticationResponse.builder().token(jwtToken).build();
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


}
