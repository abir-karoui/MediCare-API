package tn.exemple.medicare.services;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.TypeRole;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.repositories.IUserRepository;

@AllArgsConstructor
@Service


public class UserServices implements IUserSevices {

@Autowired
    private IUserRepository iUserRepository;


    @Override
    public User addUser(User user) {
        /*if (user instanceof Doctor) {
            Doctor doctor = (Doctor) user;
            return iUserRepository.save(doctor);

        } else if (user instanceof Patient) {
            Patient patient = (Patient) user;
            if (user.getRole() == TypeRole.PATIENT) {
                return iUserRepository.save(patient);
            }
        }
        return null;*/
        return  iUserRepository.save(user);
    }
}
