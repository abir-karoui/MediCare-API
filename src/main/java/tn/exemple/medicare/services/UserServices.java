package tn.exemple.medicare.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.TypeRole;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.repositories.IDoctorRepository;
import tn.exemple.medicare.repositories.IPatientRepository;
import tn.exemple.medicare.repositories.IUserRepository;

@AllArgsConstructor
@Service

public class UserServices implements IUserSevices {
    @Autowired
    private IUserRepository iUserRepository ;
    @Autowired
    IDoctorRepository iDoctorRepository ;
    IPatientRepository iPatientRepository ;

    @Override
    public User addUser(User user) {


        /*if (user.getRole() == TypeRole.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setAddress(user.getAddress());
            doctor.setEmail(user.getEmail());
            doctor.setFirstname(user.getFirstname());
            doctor.setLastname(user.getLastname());
            doctor.setMdp(user.getMdp());
            doctor.setPhone(user.getPhone());
            doctor.setPhoto(user.getPhoto());
            doctor.setGender(user.getGender());
            doctor.setSpecialite(((Doctor) user).getSpecialite());
            return iDoctorRepository.save(doctor);
        } else if (user.getRole() == TypeRole.PATIENT) {
            Patient patient = new Patient();
            patient.setAddress(user.getAddress());
            patient.setEmail(user.getEmail());
            patient.setFirstname(user.getFirstname());
            patient.setLastname(user.getLastname());
            patient.setMdp(user.getMdp());
            patient.setPhone(user.getPhone());
            patient.setPhoto(user.getPhoto());
            patient.setGender(user.getGender());
            patient.setAge(((Patient) user).getAge());
            return iPatientRepository.save(patient);
        }*/
        return iUserRepository.save(user);

    }

}
