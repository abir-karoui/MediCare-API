package tn.exemple.medicare.services;


import tn.exemple.medicare.entities.Medication;

import java.util.List;

public interface IMedicationServices  {
    Medication addMedications(Medication medications);
    List<Medication> retrieveAllMedications();
    List<Medication> getMedicationsByDenomination(String denomination);
    Medication partialUpdate(Long id , Medication medications) ;
    void  deleteMedicationsById(Long id);
    List<Medication> deleteMedicationsByDenomination(String denomination);
    void deleteAllMedications();
}
