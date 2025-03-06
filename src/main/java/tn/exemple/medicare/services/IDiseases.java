package tn.exemple.medicare.services;

import tn.exemple.medicare.entities.Diseases;

import java.util.List;

public interface IDiseases {
    Diseases addDiseases(Diseases diseases) ;
    List<Diseases> retrieveAllDiseases();
    Diseases getUsersByName(String name);
    Diseases Update(Long id , Diseases diseases) ;
    void deleteAllDiseases();
    void  deleteDiseasesById(Long id);


}
