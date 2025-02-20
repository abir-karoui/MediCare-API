package tn.exemple.medicare.services.impl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.exceptions.UserException;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.services.IUserSevices;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices implements IUserSevices {
    private final IUserRepository iUserRepository;
    public UserServices(IUserRepository iUserRepository) {
        this.iUserRepository = iUserRepository;
    }
    @Override
    public User addUser(User user) {
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
    public User getUserById(Long id)  throws Exception {

        return iUserRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with ID: " + id));
        //return iUserRepository.findById(id);
    }


    @Override
    public List<User> getUsersByRole(TypeRole role) {
        List<User> users = iUserRepository.findAllByRole(role);
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No users found for the role :" + role);
        }
        return users;
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
