package tn.exemple.medicare.services.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Medication;
import tn.exemple.medicare.repositories.IMedicationRepository;
import tn.exemple.medicare.services.IMedicationServices;

import java.util.List;
import java.util.Optional;

@Service

public class MedicationServices implements IMedicationServices {
    private final IMedicationRepository iMedicationRepository ;

    public MedicationServices(IMedicationRepository iMedicationRepository) {
        this.iMedicationRepository = iMedicationRepository;
    }

    @Override
    public Medication addMedications(Medication medications) {
        return iMedicationRepository.save(medications);
    }

    @Override
    public List<Medication> retrieveAllMedications() {
        return iMedicationRepository.findAll();
    }
    @Override
    public List<Medication> getMedicationsByDenomination(String denomination) {
        return iMedicationRepository.findAllByDenomination(denomination);
    }
    @Override
    public Medication partialUpdate(Long id, Medication medications) {
        medications.setId(id);
        return iMedicationRepository.findById(id).map(existingMedication -> {
            Optional.ofNullable(medications.getDenomination()).ifPresent(existingMedication::setDenomination);
            Optional.ofNullable(medications.getForme_pharmaceutique()).ifPresent(existingMedication::setForme_pharmaceutique);
            Optional.ofNullable(medications.getLibelle()).ifPresent(existingMedication::setLibelle);
            return iMedicationRepository.save(existingMedication);
        }).orElseThrow(() -> new EntityNotFoundException("Medication does not exist"));
    }

    @Override
    public void deleteMedicationsById(Long id) {

        if (!iMedicationRepository.existsById(id)) {
            throw new EntityNotFoundException("Medication with Id: '" +id + " ' not found");
        }
        iMedicationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public  List<Medication> deleteMedicationsByDenomination(String denomination) {
       return iMedicationRepository.deleteMedicationsByDenomination(denomination);
    }

    @Override
    public void deleteAllMedications() {
        iMedicationRepository.deleteAll();
    }





}
