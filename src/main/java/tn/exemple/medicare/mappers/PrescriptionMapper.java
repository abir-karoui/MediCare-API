package tn.exemple.medicare.mappers;

import org.springframework.stereotype.Component;
import tn.exemple.medicare.entities.dto.PrescriptionDto;
import tn.exemple.medicare.entities.prescription.Dose;
import tn.exemple.medicare.entities.prescription.Medication;
import tn.exemple.medicare.entities.prescription.Prescription;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PrescriptionMapper {

    public PrescriptionDto toDto(Prescription prescription) {
        PrescriptionDto dto = new PrescriptionDto();


        dto.setDurationDays(prescription.getDurationDays());
        dto.setStockActuel(prescription.getStockActuel());


        PrescriptionDto.MedicationDto medDto = new PrescriptionDto.MedicationDto();

        medDto.setDenomination(prescription.getMedication().getDenomination());
        dto.setMedication(medDto);

        if (prescription.getDoses() != null && !prescription.getDoses().isEmpty()) {
            List<PrescriptionDto.DoseDto> doses = prescription.getDoses()
                    .stream()
                    .map(dose -> {
                        PrescriptionDto.DoseDto doseDto = new PrescriptionDto.DoseDto();

                        doseDto.setTimeToTake(dose.getTimeToTake());
                        doseDto.setQuantity(dose.getQuantity());
                        return doseDto;
                    })
                    .collect(Collectors.toList());
            dto.setDoses(doses);
        }

        return dto;
    }
        public Prescription toEntity(PrescriptionDto prescriptionDto) {
            Prescription prescription = new Prescription();

            // Convertir le DTO en entité
            prescription.setDurationDays(prescriptionDto.getDurationDays());
            prescription.setStockActuel(prescriptionDto.getStockActuel());

            // Mapper Medication
            Medication medication = new Medication();
            medication.setDenomination(prescriptionDto.getMedication().getDenomination());
            prescription.setMedication(medication);

            // Mapper les Doses
            if (prescriptionDto.getDoses() != null) {
                List<Dose> doses = prescriptionDto.getDoses().stream()
                        .map(doseDto -> {
                            Dose dose = new Dose();
                            dose.setTimeToTake(doseDto.getTimeToTake());
                            dose.setQuantity(doseDto.getQuantity());
                            dose.setPrescription(prescription); // 🔥 LIEN essentiel
                            return dose;
                        })
                        .collect(Collectors.toList());
                prescription.setDoses(doses);
            }

            return prescription;
        }




}
