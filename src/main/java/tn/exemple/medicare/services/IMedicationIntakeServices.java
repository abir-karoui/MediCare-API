package tn.exemple.medicare.services;

import tn.exemple.medicare.entities.prescription.MedicationIntake;
import tn.exemple.medicare.entities.prescription.MedicationIntakeSimpleDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IMedicationIntakeServices {

    MedicationIntake markIntakeAsTaken(Long intakeId, boolean taken);
    Map<LocalDate, List<MedicationIntakeSimpleDTO>> getUntakenGroupedByDate();
}
