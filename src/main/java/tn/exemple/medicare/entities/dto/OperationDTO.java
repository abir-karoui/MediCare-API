package tn.exemple.medicare.entities.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OperationDTO {
    private Long id;
    private String name;
    private LocalDate dateOperation;
    private String surgeon;
    private String hospital;
    private String description;

}
