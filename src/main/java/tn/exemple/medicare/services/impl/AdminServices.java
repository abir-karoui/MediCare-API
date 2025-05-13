package tn.exemple.medicare.services.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.exceptions.BusinessErrorCode;
import tn.exemple.medicare.exceptions.BusinessException;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.services.IAdminServices;

@Service
@RequiredArgsConstructor
public class AdminServices implements IAdminServices {
    private final IUserRepository iUserRepository;

    private final EmailService emailService ;
    /*@Override
    public void validateDoctorMedicalCard(Long doctorId) {
        Doctor doctor = (Doctor) iUserRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND));
        if (doctor.isMedicalCardVerified()) {
            throw new BusinessException(BusinessErrorCode.MEDICAL_CARD_ALREADY_VERIFIED);
        }

        doctor.setMedicalCardVerified(true);
        iUserRepository.save(doctor);
    }*/
    @Override
    public void validateDoctorMedicalCard(Long doctorId) {
        Doctor doctor = (Doctor) iUserRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND));
        if (doctor.isMedicalCardVerified()) {
            throw new BusinessException(BusinessErrorCode.MEDICAL_CARD_ALREADY_VERIFIED);
        }

        doctor.setMedicalCardVerified(true);
        iUserRepository.save(doctor);

        // ✅ Envoi de l'email simple
        try {
            emailService.sendSimpleMessage(
                    doctor.getEmail(),
                    doctor.getFirstname(),
                    "Medical Card Validation",
                    "Your medical card has been successfully validated. You can now log in to your account."
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
