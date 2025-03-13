package tn.exemple.medicare.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.entities.Diseases;
import tn.exemple.medicare.entities.Medication;
import tn.exemple.medicare.services.IDiseases;

import java.util.List;

@RestController
@RequestMapping("/diseases")
@RequiredArgsConstructor

public class DiseasesControllers {
    @Autowired
    private IDiseases iDiseases;
    @PostMapping("/addDiseases")
    Diseases addDiseases(@RequestBody Diseases d ) {
        return iDiseases.addDiseases(d) ;
    }

   /* @GetMapping("/all")
    List<Diseases> retrieveAllDiseases() {
        return iDiseases.retrieveAllDiseases();
    }*/
    @GetMapping("/name/{name}")
    Diseases getUsersByName(@PathVariable("name") String name) {
        return iDiseases.getDiseasesByName(name);
    }
    @GetMapping("/getDiseases")
    public ResponseEntity<Page<Diseases>> getDiseases(
            @RequestParam(defaultValue = "0" ) int pageNo,
            @RequestParam(defaultValue = "5" ) int pageSize) {
        Page<Diseases> diseases = iDiseases.getDiseases(pageNo, pageSize);
        return ResponseEntity.ok(diseases);
    }
    @PutMapping("{id}")
    Diseases  UpdateDisease(@PathVariable Long id, @RequestBody Diseases diseases) {
        return iDiseases.Update(id, diseases);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteDiseasesById(@PathVariable Long id) {

        try{
            iDiseases.deleteDiseasesById(id);
            return new ResponseEntity<>("Disease deleted successfully", HttpStatus.OK);

        }   catch (EntityNotFoundException e) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/")
    public void deleteAllDisease(){ iDiseases.deleteAllDiseases();}

}
