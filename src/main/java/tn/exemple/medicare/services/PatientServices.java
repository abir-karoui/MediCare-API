package tn.exemple.medicare.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.repositories.IPatientRepository;

@AllArgsConstructor
@Service
public class PatientServices implements  IPatient{
    @Autowired
    private IPatientRepository iPatientRepository ;
    @Override
    public Patient addPatient(Patient patient) {
        return iPatientRepository.save(patient);
    }
}
