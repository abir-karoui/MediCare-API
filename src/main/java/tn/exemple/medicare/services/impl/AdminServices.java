package tn.exemple.medicare.services.impl;

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
    @Override
    public void validateDoctorMedicalCard(Long doctorId) {
        Doctor doctor = (Doctor) iUserRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND));
        if (doctor.isMedicalCardVerified()) {
            throw new BusinessException(BusinessErrorCode.MEDICAL_CARD_ALREADY_VERIFIED);
        }

        doctor.setMedicalCardVerified(true);
        iUserRepository.save(doctor);
    }
}
