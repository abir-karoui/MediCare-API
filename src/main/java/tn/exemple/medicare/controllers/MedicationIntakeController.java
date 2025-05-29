package tn.exemple.medicare.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.entities.prescription.MedicationIntake;
import tn.exemple.medicare.services.IMedicationIntakeServices;

@RestController
@RequestMapping("/intake")
@RequiredArgsConstructor
public class MedicationIntakeController {
    private final IMedicationIntakeServices medicationIntakeService;

    @PutMapping("/{id}")
    public ResponseEntity<String> markMedicationAsTaken(
            @PathVariable Long id,
            @RequestParam boolean taken
    ) {
        MedicationIntake updated = medicationIntakeService.markIntakeAsTaken(id, taken);

        String status = taken ? "taken" : "not taken";
        return ResponseEntity.ok("Medication with ID " + id + " marked as " + status);
    }
}
