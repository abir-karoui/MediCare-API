package tn.exemple.medicare.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.Diseases;
import tn.exemple.medicare.exceptions.BusinessErrorCode;
import tn.exemple.medicare.exceptions.BusinessException;
import tn.exemple.medicare.repositories.IDiseasesRepository;
import tn.exemple.medicare.services.IDiseases;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DiseasesServices implements IDiseases {

    private final IDiseasesRepository iDiseasesRepository ;
    @PostConstruct
    public void loadDiseasesFromJson() {
        if (iDiseasesRepository.count() == 0) {
            try {
                InputStream inputStream = new ClassPathResource("diseases.json").getInputStream();
                ObjectMapper objectMapper = new ObjectMapper();
                List<Diseases> diseasesList = objectMapper.readValue(inputStream, new TypeReference<List<Diseases>>() {});

                iDiseasesRepository.saveAll(diseasesList);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load diseases.json", e);
            }
        }
    }
    @Override
    public Diseases addDiseases(Diseases diseases) {

        if (iDiseasesRepository.findDiseasesByName(diseases.getName()) != null) {
            throw new BusinessException(BusinessErrorCode.DISEASE_NAME_ALREADY_EXISTS );
        }
        return iDiseasesRepository.save(diseases);
    }
    @Override
    public Page<Diseases> getDiseases(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("name"));
        return iDiseasesRepository.findAll(pageable);
    }

    @Override
    public Diseases getDiseasesByName(String name) {
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
    @Override
    public long countDiseases() {
        return iDiseasesRepository.count();
    }


}
