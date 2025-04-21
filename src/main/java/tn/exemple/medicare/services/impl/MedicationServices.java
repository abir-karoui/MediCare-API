package tn.exemple.medicare.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tn.exemple.medicare.entities.prescription.Medication;
import tn.exemple.medicare.repositories.IMedicationRepository;
import tn.exemple.medicare.services.IMedicationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;



    @Service
    @RequiredArgsConstructor
    public class MedicationServices implements IMedicationServices {
        @Autowired
        private  IMedicationRepository iMedicationRepository ;
        private final WebClient webClient; //WebClient c’est un outil de Spring pour envoyer des requêtes HTTP(comme postman mais en java)
        @Autowired
        public MedicationServices(WebClient.Builder webClientBuilder) {
            this.webClient = webClientBuilder.baseUrl("https://api-bdpm-graphql.axel-op.fr/graphql").build();
        } //WebClient.Builder pour configurer l'URL de base de l'API GraphQL

        @Override
        //mono traja 0 ou 1 , ici trajaa 1 liste contient ts medicament avec name
        public Mono<List<Medication>> searchMedicationsByName(String name) {
            String graphqlQuery = """
            query {
              medicaments(denomination: { contains_one_of: ["%s"] }) {
                denomination
            
              }
            }
            """.formatted(name);

            return webClient.post()//preparer requette ili bch nabaatha l API
                    .bodyValue(Map.of("query", graphqlQuery)) //huni bch nabaath requette ili snaatha f body
                    .retrieve()// Exécute la requête
                    .bodyToMono(JsonNode.class) // Convertit la réponse en JSON
                    .map(this::mapToMedications); //Transforme le JSON en liste de médicaments
        }
        @Override
        public Mono<List<Medication>> getAllMedications(int page, int size) {
            String graphqlQuery = """
        query {
          medicaments(limit: %d, from: %d) {
            denomination
          }
        }
    """.formatted(size, page);

            return webClient.post()
                    .bodyValue(Map.of("query", graphqlQuery))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(this::mapToMedications);
        }

        //logique comment Transforme le JSON en liste de médicaments
        public List<Medication> mapToMedications(JsonNode response) {
            List<Medication> medications = new ArrayList<>();
            JsonNode medicaments = response.path("data").path("medicaments");

            for (JsonNode medicament : medicaments) {
                Medication medication = new Medication();
                medication.setDenomination(medicament.path("denomination").asText());
                medications.add(medication);
            }

            return medications;
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

            List<Medication> medications = iMedicationRepository.findAllByDenomination(denomination);
            if (medications.isEmpty()) {
                throw new EntityNotFoundException("No medication found with denomination: " + denomination);
            }
            return iMedicationRepository.findAllByDenomination(denomination);
        }
        @Override
        public Medication partialUpdate(Long id, Medication medications) {
            medications.setId(id);
            return iMedicationRepository.findById(id).map(existingMedication -> {
                Optional.ofNullable(medications.getDenomination()).ifPresent(existingMedication::setDenomination);
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
            List<Medication> medications = iMedicationRepository.findAllByDenomination(denomination);

            if (medications.isEmpty()) {
                throw new EntityNotFoundException("No medications found with denomination: " + denomination);
            }

            return iMedicationRepository.deleteMedicationsByDenomination(denomination);
        }

        @Override
        public void deleteAllMedications() {
            iMedicationRepository.deleteAll();
        }

        @Override
        public Page<Medication> getMedications(int pageNo, int pageSize) {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            return iMedicationRepository.findAll(pageable);
        }




}
