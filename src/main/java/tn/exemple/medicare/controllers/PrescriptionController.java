package tn.exemple.medicare.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.entities.dto.PrescriptionDto;
import tn.exemple.medicare.services.IPrescriptionServices;

import tn.exemple.medicare.entities.prescription.Prescription;

import java.util.List;

@RestController
@RequestMapping("/prescription")
@RequiredArgsConstructor
public class PrescriptionController {
    private final IPrescriptionServices prescriptionServices;
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Prescription addPrescription(@RequestBody PrescriptionDto prescriptionDto) {
        return prescriptionServices.createPrescription(prescriptionDto);
    }
    @GetMapping("/prescriptions")
    public ResponseEntity<List<Prescription>> getPrescriptions() {
        List<Prescription> prescriptions = prescriptionServices.getPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }
    @GetMapping("/{prescriptionId}")
    public ResponseEntity<Prescription> getPrescriptionsByUserId(@PathVariable Long  prescriptionId) {
        Prescription prescriptions = prescriptionServices.getPrescriptionById(prescriptionId);
        return ResponseEntity.ok(prescriptions);
    }
    @DeleteMapping("/{id}")
    public void deletePrescription(@PathVariable Long id) {
        prescriptionServices.deletePrescription(id);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Prescription> updatePrescriptionPartial(
            @PathVariable Long id,
            @RequestBody PrescriptionDto prescriptionDto) {
        Prescription updatedPrescription = prescriptionServices.updatePrescriptionPartial(id, prescriptionDto);
        return ResponseEntity.ok(updatedPrescription);
    }

}
