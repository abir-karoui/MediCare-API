package tn.exemple.medicare.entities.dto;

import lombok.Data;
import tn.exemple.medicare.enums.BloodGroup;

import java.util.List;

@Data
public class MedicalRecordResponse {
    private BloodGroup bloodType;
    private Double height;
    private Double weight;
    private List<String> allergies;
    private List<String> chronicDiseases;
    private List<OperationDTO> operations;
    private List<String> medicationNames;

}
