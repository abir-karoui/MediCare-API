package tn.exemple.medicare.controllers;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.exceptions.BusinessException;
import tn.exemple.medicare.services.IAdminServices;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor

public class AdminControllers {
    @Autowired
    private IAdminServices iAdminServices;
    @PostMapping("/validate/{doctorId}")
    public ResponseEntity<String> validateDoctorMedicalCard(
            @PathVariable Long doctorId,
            @RequestParam boolean accept) {
        try {
            iAdminServices.processDoctorMedicalCard(doctorId, accept);
            String message = accept ?
                    "Doctor medical card validated successfully." :
                    "Doctor medical card validation rejected.";
            return ResponseEntity.ok(message);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send email notification.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }
}
