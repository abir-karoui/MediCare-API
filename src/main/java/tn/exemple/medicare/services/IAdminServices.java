package tn.exemple.medicare.services;

import jakarta.mail.MessagingException;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;

import java.util.List;

public interface IAdminServices {

     void processDoctorMedicalCard(Long doctorId, boolean accept) throws MessagingException;
      List<Doctor> getAllDoctors();
    List<Patient> getAllPatient();

}
