package tn.exemple.medicare.services;

import jakarta.mail.MessagingException;

public interface IAdminServices {

    //void validateDoctorMedicalCard(Long doctorId);
     void processDoctorMedicalCard(Long doctorId, boolean accept) throws MessagingException;
}
