package tn.exemple.medicare.services;

import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.entities.dto.PrescriptionDto;

import java.util.List;

public interface IPrescriptionServices {

    Prescription createPrescription(PrescriptionDto prescriptionDto);
   List<Prescription> getPrescriptions();
     List<Prescription> getInactivePrescriptions();

    Prescription getPrescriptionById(Long prescriptionId );
    void deletePrescription(Long prescriptionId);

    Prescription updatePrescriptionPartial(Long prescriptionId, PrescriptionDto prescriptionDto) ;


    }
