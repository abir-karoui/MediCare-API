package tn.exemple.medicare.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;


@Entity
@Data
@Table(name = "Medications")

public class Medications implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String denomination ;
    String forme_pharmaceutique;
    String libelle ;




}
