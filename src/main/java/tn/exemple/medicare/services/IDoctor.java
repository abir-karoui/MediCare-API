package tn.exemple.medicare.services;

import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.dto.PrescriptionDto;
import tn.exemple.medicare.entities.prescription.Prescription;

import java.util.List;

public interface IDoctor {

    Prescription createPrescriptionForPatient(Long patientId, PrescriptionDto prescriptionDto);
    List<Prescription> getPrescriptions(Long patientId);
    List<Prescription> getPrescriptionsNotCreatedByDoctor(Long patientId);
    void deletePrescription(Long prescriptionId);
    List<Prescription> getAllPrescriptionsForPatient(Long patientId);
}
