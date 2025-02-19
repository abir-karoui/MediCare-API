package tn.exemple.medicare.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Diseases;
import tn.exemple.medicare.repositories.IDiseasesRepository;
import tn.exemple.medicare.services.IDiseases;

@AllArgsConstructor
@Service
public class DiseasesServices implements IDiseases {
    @Autowired
    private IDiseasesRepository iDiseasesRepository ;
    @Override
    public Diseases addDiseases(Diseases diseases) {
        return iDiseasesRepository.save(diseases);
    }
}
