package tn.exemple.medicare.mappers;


import org.springframework.stereotype.Component;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.dto.MedicalRecordDTO;
import tn.exemple.medicare.entities.dto.MedicalRecordResponse;
import tn.exemple.medicare.entities.dto.OperationDTO;
import tn.exemple.medicare.entities.medicalRecord.MedicalRecord;
import tn.exemple.medicare.entities.medicalRecord.Operation;
import tn.exemple.medicare.entities.prescription.Medication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MedicalRecordMapper {
    public MedicalRecord toEntity(MedicalRecordDTO dto, Patient patient) {
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setBloodType(dto.getBloodType());
        record.setHeight(dto.getHeight());
        record.setWeight(dto.getWeight());
        record.setAllergies(dto.getAllergies());
        record.setChronicDiseases(dto.getChronicDiseases());

        List<Operation> operations = new ArrayList<>();
        for (OperationDTO opDto : dto.getOperations()) {
            Operation op = new Operation();
            op.setName(opDto.getName());
            op.setDateOperation(opDto.getDateOperation());
            op.setSurgeon(opDto.getSurgeon());
            op.setHospital(opDto.getHospital());
            op.setDescription(opDto.getDescription());
            op.setMedicalRecord(record);
            operations.add(op);
        }
        record.setOperations(operations);

        return record;
    }
    public MedicalRecordDTO toDto(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setBloodType(record.getBloodType());
        dto.setHeight(record.getHeight());
        dto.setWeight(record.getWeight());
        dto.setAllergies(record.getAllergies());
        dto.setChronicDiseases(record.getChronicDiseases());

        List<OperationDTO> operationDTOs = new ArrayList<>();
        for (Operation op : record.getOperations()) {
            OperationDTO opDto = new OperationDTO();
            opDto.setName(op.getName());
            opDto.setDateOperation(op.getDateOperation());
            opDto.setSurgeon(op.getSurgeon());
            opDto.setHospital(op.getHospital());
            opDto.setDescription(op.getDescription());
            operationDTOs.add(opDto);
        }
        dto.setOperations(operationDTOs);

        return dto;
    }
    public MedicalRecordResponse toResponse(MedicalRecord record, Patient patient) {
        List<String> medicationNames = getMedicationNames(patient);
        List<OperationDTO> operationDTOs = mapOperations(record);

        MedicalRecordResponse Recordresponse = new MedicalRecordResponse();
        Recordresponse.setBloodType(record.getBloodType());
        Recordresponse.setHeight(record.getHeight());
        Recordresponse.setWeight(record.getWeight());
        Recordresponse.setAllergies(record.getAllergies());
        Recordresponse.setChronicDiseases(record.getChronicDiseases());
        Recordresponse.setOperations(operationDTOs);
        Recordresponse.setMedicationNames(medicationNames);

        return Recordresponse;
    }

    private List<String> getMedicationNames(Patient patient) {
        return patient.getPrescriptions().stream()
                .map(prescription -> {
                    Medication med = prescription.getMedication();
                    return (med != null) ? med.getDenomination() : "Unknown";
                })
                .collect(Collectors.toList());
    }

    private List<OperationDTO> mapOperations(MedicalRecord record) {
        return record.getOperations().stream()
                .map(operation -> {
                    OperationDTO dto = new OperationDTO();
                    dto.setName(operation.getName());
                    dto.setDateOperation(operation.getDateOperation());
                    dto.setSurgeon(operation.getSurgeon());
                    dto.setHospital(operation.getHospital());
                    dto.setDescription(operation.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
