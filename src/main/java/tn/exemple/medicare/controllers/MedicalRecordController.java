package tn.exemple.medicare.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.entities.dto.MedicalRecordDTO;
import tn.exemple.medicare.entities.dto.MedicalRecordResponse;
import tn.exemple.medicare.services.IMedicalRecordServies;

@RestController
@RequestMapping("/medicalRecord")
@RequiredArgsConstructor
public class MedicalRecordController {

    @Autowired
    private IMedicalRecordServies iMedicalRecordServies;
    @PostMapping("/add")
    public ResponseEntity<MedicalRecordDTO> createMedicalRecord(@RequestBody MedicalRecordDTO dto) {
        MedicalRecordDTO createdRecord = iMedicalRecordServies.createMedicalRecord(dto);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }
    @GetMapping("/get")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecord() {
        MedicalRecordResponse response = iMedicalRecordServies.getMedicalRecord();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecordOfPatient(@PathVariable Long patientId) {
        MedicalRecordResponse response = iMedicalRecordServies.getMedicalRecordPatient(patientId);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMedicalRecord() {
        iMedicalRecordServies.deleteMedicalRecord();
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/update")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecordPartially(@RequestBody MedicalRecordDTO dto) {
        MedicalRecordDTO updated = iMedicalRecordServies.updateMedicalRecordPartial(dto);
        return ResponseEntity.ok(updated);
    }

}
