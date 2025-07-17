package tn.exemple.medicare.services;


import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;
import tn.exemple.medicare.entities.prescription.Medication;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IMedicationServices  {
    Mono<List<Medication>> searchMedicationsByName(String name);
    List<Medication> mapToMedications(JsonNode response) ;
    Mono<List<Medication>> getAllMedications(int page, int size);

    Medication addMedications(Medication medications);
    List<Medication> retrieveAllMedications();
    List<Medication> getMedicationsByDenomination(String denomination);
    Medication partialUpdate(Long id , Medication medications) ;
    void  deleteMedicationsById(Long id);
    List<Medication> deleteMedicationsByDenomination(String denomination);

    Page<Medication> getMedications (int pageNo, int pageSize) ;

}
