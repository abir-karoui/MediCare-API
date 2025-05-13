package tn.exemple.medicare.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.exemple.medicare.services.IAdminServices;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor

public class AdminControllers {
    @Autowired
    private IAdminServices iAdminServices;
    @PostMapping("/validate/{doctorId}")
    public ResponseEntity<String> validateDoctorMedicalCard(@PathVariable Long doctorId) {
        iAdminServices.validateDoctorMedicalCard(doctorId);
        return ResponseEntity.ok("Doctor's medical card has been successfully validated.");
    }
}
