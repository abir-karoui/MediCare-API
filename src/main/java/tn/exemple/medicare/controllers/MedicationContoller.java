package tn.exemple.medicare.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tn.exemple.medicare.entities.prescription.Medication;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.entities.dto.PrescriptionDto;
import tn.exemple.medicare.services.IMedicationServices;
import tn.exemple.medicare.services.IPrescriptionServices;
import tn.exemple.medicare.services.impl.MedicationServices;

import java.util.List;

@RestController
@RequestMapping("/medication")
@RequiredArgsConstructor

public class MedicationContoller {

    @Autowired
    private IMedicationServices iMedicationServices;

    public record DeleteMedicationResponse(String message, List<Medication> medications) {}
    //public record GetAllMedications( List<Medication> data , int count) {}
    @GetMapping("/search")
    public Mono<List<Medication>> searchMedications(@RequestParam String name) {
        return iMedicationServices.searchMedicationsByName(name);
    }
    @GetMapping("/pagination")
    public Mono<ResponseEntity<List<Medication>>> getExternalMedications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return iMedicationServices.getAllMedications(page, size)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
    @PostMapping("/addmedication")
    Medication addMedication(@RequestBody Medication m) {
        return iMedicationServices.addMedications(m);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> partialUpdate(
            @PathVariable("id") long id,
            @RequestBody Medication medication
    ){
            Medication medication1 = iMedicationServices.partialUpdate(id, medication);
            return ResponseEntity.ok(medication1);
    }
    @DeleteMapping("/{denomination}")
    public ResponseEntity<Object> deleteMedicationByDenomination(@PathVariable String denomination) {
        List<Medication> medications = iMedicationServices.deleteMedicationsByDenomination(denomination);
        DeleteMedicationResponse response = new DeleteMedicationResponse("Denomination deleted successfully", medications);
        return ResponseEntity.ok(response);
    }
    /*@DeleteMapping("/id/{id}")
    public ResponseEntity<Object> deleteMedicationsById(@PathVariable Long id) {
            iMedicationServices.deleteMedicationsById(id);
            return new ResponseEntity<>("Medication with id : " +id+ " deleted successfully", HttpStatus.OK);
    }
    @GetMapping("/pagination")
    public ResponseEntity<Page<Medication>> getMedications(
            @RequestParam(defaultValue = "0" ) int pageNo,
            @RequestParam(defaultValue = "5" ) int pageSize) {
        Page<Medication> medications = iMedicationServices.getMedications(pageNo, pageSize);
        return ResponseEntity.ok(medications);
    }
    */
        /*@GetMapping("/all")
    public ResponseEntity<GetAllMedications> retrieveAllMedications() {
        List<Medication> medications = iMedicationServices.retrieveAllMedications();
        GetAllMedications response = new GetAllMedications(medications , medications.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/denomination/{denomination}")
    public ResponseEntity<List<Medication>> getMedicationByDenomation(@PathVariable("denomination") String denomination) {
        List<Medication> medications = iMedicationServices.getMedicationsByDenomination(denomination);
        return ResponseEntity.ok(medications);
    }*/
}
