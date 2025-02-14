package tn.exemple.medicare.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.exemple.medicare.entities.Diseases;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.services.IDiseases;
import tn.exemple.medicare.services.IDoctor;

@RestController
@RequestMapping("/diseases")
@RequiredArgsConstructor

public class DiseasesControllers {
    private IDiseases iDiseases;
    @PostMapping("/addDiseases")
    Diseases addDiseases(@RequestBody Diseases d ) {
        return iDiseases.addDiseases(d) ;
    }

}
