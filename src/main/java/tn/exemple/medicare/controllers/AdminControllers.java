package tn.exemple.medicare.controllers;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.configs.LoggableAction;
import tn.exemple.medicare.entities.ActivityLog;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.exceptions.BusinessException;
import tn.exemple.medicare.services.IActivityLogService;
import tn.exemple.medicare.services.IAdminServices;
import tn.exemple.medicare.services.IDiseases;
import tn.exemple.medicare.services.IPrescriptionServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor

public class AdminControllers {
    @Autowired
    private IAdminServices iAdminServices;

    @Autowired
    private IPrescriptionServices iPrescriptionServices;
    @Autowired
    private IDiseases iDiseases;
    @Autowired
    private IActivityLogService iActivityLogService;
    @LoggableAction(
            title = "Doctor Verification",
            description = "The medical card has been verified by the administrator"
    )

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

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = iAdminServices.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPtients() {
        List<Patient> patients = iAdminServices.getAllPatient();
        return ResponseEntity.ok(patients);
    }


    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("usersCount", (long) (iAdminServices.getAllPatient().size() + iAdminServices.getAllDoctors().size()));
        stats.put("prescriptionsCount", (long) iPrescriptionServices.getPrescriptions().size());
        stats.put("diseasesCount", iDiseases.countDiseases());

        return ResponseEntity.ok(stats);
    }


    @GetMapping("/activities")
    public ResponseEntity<Page<ActivityLog>> getLogs(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(iActivityLogService.getActivities(pageNo, pageSize));
    }


}
