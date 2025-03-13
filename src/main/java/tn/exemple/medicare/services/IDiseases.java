package tn.exemple.medicare.services;

import org.springframework.data.domain.Page;
import tn.exemple.medicare.entities.Diseases;
import tn.exemple.medicare.entities.Medication;

import java.util.List;

public interface IDiseases {
    Diseases addDiseases(Diseases diseases) ;
    Page<Diseases> getDiseases (int pageNo, int pageSize) ;
    Diseases getDiseasesByName(String name) ;
    //List<Diseases> retrieveAllDiseases();

    Diseases Update(Long id , Diseases diseases) ;
    void deleteAllDiseases();
    void  deleteDiseasesById(Long id);


}
