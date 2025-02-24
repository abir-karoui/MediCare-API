package tn.exemple.medicare.services;


import tn.exemple.medicare.entities.Medication;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IMedicationServices  {
    Medication addMedications(Medication medications);
    List<Medication> retrieveAllMedications();
    List<Medication> getMedicationsByDenomination(String denomination);
    Medication partialUpdate(Long id , Medication medications) ;
    void  deleteMedicationsById(Long id);
    List<Medication> deleteMedicationsByDenomination(String denomination);
    void deleteAllMedications();
    Page<Medication> getMedications (int pageNo, int pageSize) ;

}
