package tn.exemple.medicare.services.impl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.configs.JwtService;
import tn.exemple.medicare.controllers.AuthenticationRequest;
import tn.exemple.medicare.controllers.AuthenticationResponse;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.Token;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.repositories.TokenRepository;
import tn.exemple.medicare.services.IUserSevices;

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

    public UserServices(IUserRepository iUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, TokenRepository tokenRepository) {
        this.iUserRepository = iUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
    }
   /* @Override

    public User addUser(User user) {
        Optional<User> existingUser = iUserRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists99");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == TypeRole.DOCTOR) {
            Doctor doctor = (Doctor) user;
            return iUserRepository.save(doctor);
        } else if (user.getRole() == TypeRole.PATIENT) {
            Patient patient = (Patient) user;
            return iUserRepository.save(patient);
        } else {
            throw new IllegalArgumentException("Invalid user role");
        }
       // sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) {
        var newToken = generateAndSaveActivationToken(user);
        //send email


    }

    private String generateAndSaveActivationToken(User user) {
        //generate Token
        String generateToken = generateActivationCode(6);
        var token = Token.builder()
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
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
    */
   @Override

    public User addUser(User user) {
       Optional<User> existingUser = iUserRepository.findByEmail(user.getEmail());
       if (existingUser.isPresent()) {
           throw new IllegalArgumentException("Email already exists");
       }

       user.setPassword(passwordEncoder.encode(user.getPassword()));

       if (user.getRole() == TypeRole.DOCTOR) {
           Doctor doctor = (Doctor) user;
           return iUserRepository.save(doctor);
       } else if (user.getRole() == TypeRole.PATIENT) {
           Patient patient = (Patient) user;
           return iUserRepository.save(patient);
       } else {
           throw new IllegalArgumentException("Invalid user role");
       }
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

}
