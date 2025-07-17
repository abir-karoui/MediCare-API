package tn.exemple.medicare.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.configs.LoggableAction;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.dto.PrescriptionDto;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.services.IDoctor;

import java.util.List;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final IDoctor iDoctor;
    @LoggableAction(
            title = "Prescription created",
            description = "add new prescption"
    )
    @PostMapping("/createPrescriptionForPatient/{patientId}")
    public Prescription createPrescriptionForPatient(@PathVariable Long patientId, @RequestBody PrescriptionDto prescriptionDto) {
        return iDoctor.createPrescriptionForPatient(patientId, prescriptionDto);
    }
    @GetMapping("/getprescription/{patientId}")
    public ResponseEntity<List<Prescription>> getPrescriptions(@PathVariable Long patientId) {
        List<Prescription> prescriptions = iDoctor.getPrescriptions(patientId);
        return ResponseEntity.ok(prescriptions);
    }
    @GetMapping("/getNotprescription/{patientId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsNotCreatedByDoctor(@PathVariable Long patientId) {
        List<Prescription> prescriptions = iDoctor.getPrescriptionsNotCreatedByDoctor(patientId);
        return ResponseEntity.ok(prescriptions);
    }
    @GetMapping("/gemini/{patientId}")
    public ResponseEntity<List<Prescription>> getAllPrescriptionsForPatient(@PathVariable Long patientId) {
        List<Prescription> prescriptions = iDoctor.getAllPrescriptionsForPatient(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @DeleteMapping("/prescription/{id}")
    public void deletePrescription(@PathVariable Long id) {
        iDoctor.deletePrescription(id);
    }


}
