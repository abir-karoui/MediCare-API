package tn.exemple.medicare.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Diseases;
import tn.exemple.medicare.repositories.IDiseasesRepository;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.services.IDiseases;

import java.util.List;


@Service
public class DiseasesServices implements IDiseases {

    private final IDiseasesRepository iDiseasesRepository ;

    public DiseasesServices(IDiseasesRepository iDiseasesRepository) {
        this.iDiseasesRepository = iDiseasesRepository;
    }

    @Override
    public Diseases addDiseases(Diseases diseases) {
        return iDiseasesRepository.save(diseases);
    }

    @Override
    public List<Diseases> retrieveAllDiseases() {
        return iDiseasesRepository.findAll();
    }

    @Override
    public Diseases getUsersByName(String name) {
        Diseases disease = iDiseasesRepository.findDiseasesByName(name);
        if (disease == null) {
            throw new EntityNotFoundException("Disease name not found: " + name);
        }
        return disease;
    }

    @Override
    public Diseases Update(Long id, Diseases diseases) {
        if (!iDiseasesRepository.existsById(id)) {
            throw new EntityNotFoundException("Disease with Id: '" +id + "' not found");
        }
        diseases.setId(id);
        return iDiseasesRepository.save(diseases);
    }
    @Override
    public void deleteAllDiseases() {
        iDiseasesRepository.deleteAll();
    }
    @Override
    public void deleteDiseasesById(Long id) {
        if (!iDiseasesRepository.existsById(id)) {
            throw new EntityNotFoundException("Disease with Id: '" +id + "' not found");
        }
        iDiseasesRepository.deleteById(id);

    }


}
