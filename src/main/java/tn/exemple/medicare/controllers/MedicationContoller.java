package tn.exemple.medicare.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.entities.Medication;
import tn.exemple.medicare.services.IMedicationServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medication")
@RequiredArgsConstructor

public class MedicationContoller {
    @Autowired
    private IMedicationServices iMedicationServices;
    @PostMapping("/addmedication")
    Medication addMedication(@RequestBody Medication m) {
        return iMedicationServices.addMedications(m);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Medication>> retrieveAllMedications() {
        List<Medication> medications = iMedicationServices.retrieveAllMedications();
        if (medications.isEmpty()) {
            throw new EntityNotFoundException("No Medications found in the database.");
        }
        return ResponseEntity.ok(medications);
    }
    @GetMapping("/denomination/{denomination}")
    public ResponseEntity<List<Medication>> getMedicationByDenomation(@PathVariable("denomination") String denomination) {
        List<Medication> medications = iMedicationServices.getMedicationsByDenomination(denomination);
        if (medications.isEmpty()) {
            throw new EntityNotFoundException("No medication found with denomination: " + denomination);
        }
        return ResponseEntity.ok(medications);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> partialUpdate(
            @PathVariable("id") long id,
            @RequestBody Medication medication
    ){
        try {
            Medication medication1 = iMedicationServices.partialUpdate(id, medication);
            return ResponseEntity.ok(medication1);
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/{denomination}")
    public ResponseEntity<Object> deleteMedicationByDenomination(@PathVariable String denomination) {
        List<Medication> medications = iMedicationServices.deleteMedicationsByDenomination(denomination);
        if (medications.isEmpty()) {
            throw new EntityNotFoundException("No medication found with denomination: " + denomination);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("Denomination deleted successfully:", medications);
        return ResponseEntity.ok(response);
       // return ResponseEntity.ok( "denomination deleted successfully");

    }
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Object> deleteMedicationsById(@PathVariable Long id) {
        try {
            iMedicationServices.deleteMedicationsById(id);
            return new ResponseEntity<>("Medication with id : " +id+ " deleted successfully", HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }




}
