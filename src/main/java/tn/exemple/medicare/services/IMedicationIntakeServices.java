package tn.exemple.medicare.services;

import tn.exemple.medicare.entities.prescription.MedicationIntake;

public interface IMedicationIntakeServices {

    MedicationIntake markIntakeAsTaken(Long intakeId, boolean taken);
}
