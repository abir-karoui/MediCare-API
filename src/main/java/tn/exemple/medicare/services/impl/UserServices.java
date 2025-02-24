package tn.exemple.medicare.services.impl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.services.IUserSevices;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices implements IUserSevices {
    private final IUserRepository iUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserServices(IUserRepository iUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.iUserRepository = iUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public User addUser(User user) {
        if (iUserRepository.findByEmail(user.getEmail()) != null){
             throw new EntityNotFoundException("Email already existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

       if (user.getRole() == TypeRole.DOCTOR) {
            Doctor doctor = (Doctor) user;
            return iUserRepository.save(doctor);
       } else if (user.getRole() == TypeRole.PATIENT) {
            Patient patient = (Patient) user;
                return iUserRepository.save(patient);
       }
        return null;
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
