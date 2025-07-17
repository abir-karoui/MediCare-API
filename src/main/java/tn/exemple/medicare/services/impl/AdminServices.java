package tn.exemple.medicare.services.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.exceptions.BusinessErrorCode;
import tn.exemple.medicare.exceptions.BusinessException;
import tn.exemple.medicare.repositories.IAdminRepository;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.services.IAdminServices;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServices implements IAdminServices {
    private final IUserRepository iUserRepository;
    private final IAdminRepository iAdminRepository;

    private final EmailService emailService ;

   @Override
   public void processDoctorMedicalCard(Long doctorId, boolean accept) throws MessagingException {
       Doctor doctor = (Doctor) iUserRepository.findById(doctorId)
               .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND));

       if (accept) {
           doctor.setMedicalCardVerified(true);
           doctor.setAccountLocked(false);

           iUserRepository.save(doctor);

           emailService.sendSimpleMessage(
                   doctor.getEmail(),
                   doctor.getFirstname(),
                   "Medical Card Validation",
                   "Your medical card has been successfully validated. You can now log in to your account."
           );

       } else {
          /* doctor.setMedicalCardVerified(false);
           doctor.setAccountLocked(true);
           iUserRepository.save(doctor);*/


           emailService.sendSimpleMessage(
                   doctor.getEmail(),
                   doctor.getFirstname(),
                   "Medical Card Validation Rejected",
                   "Unfortunately, after verification of your medical card, your account has been rejected. Please check your information carefully."
           );
           iUserRepository.delete(doctor);

       }
   }
    @Override
    public List<Doctor> getAllDoctors() {
        return iAdminRepository.findAllDoctors();
    }

    @Override
    public List<Patient> getAllPatient() {
        return iAdminRepository.findAllPatients();
    }




}
