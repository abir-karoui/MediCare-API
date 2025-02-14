package tn.exemple.medicare.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.repositories.IDoctorRepository;

@AllArgsConstructor
@Service
public class DoctorServices implements  IDoctor {
    @Autowired
    private IDoctorRepository iDoctorRepository;
    @Override
    public Doctor addDoctor(Doctor doctor) {
        return iDoctorRepository.save(doctor);
    }
}
