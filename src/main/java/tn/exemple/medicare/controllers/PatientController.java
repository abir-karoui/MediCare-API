package tn.exemple.medicare.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.services.IPatient;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {
    private IPatient iPatient;
    /*@PostMapping("/addpatient")
    Patient addPatient(@RequestBody Patient p){ return iPatient.addPatient(p);}*/
}
