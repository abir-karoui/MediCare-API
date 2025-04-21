package tn.exemple.medicare.entities.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalTime;
import java.util.List;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionDto {

    private int durationDays;
    private String stockActuel;
    private MedicationDto medication;
    private List<DoseDto> doses;
// Utilise l'héritage pour partager des comportements ou des attributs communs entre plusieurs classes. Utile quand les entités ont des caractéristiques communes mais ont aussi des comportements spécifiques qui doivent être personnalisés dans les sous-classes.
    @Data
    public static class MedicationDto {

        private String denomination;
    }
    @Data
    public static class DoseDto {
        @JsonFormat(pattern = "HH:mm")
        private LocalTime timeToTake;
        private String quantity;
    }
}
