package tn.exemple.medicare.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Entity
@Data
@Table(name = "Medications")
@AllArgsConstructor
@NoArgsConstructor

public class Medication implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String denomination ; //nom
    String forme_pharmaceutique; // type comprime, serum
    String libelle ; //Description de medic + quantité

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getForme_pharmaceutique() {
        return forme_pharmaceutique;
    }

    public void setForme_pharmaceutique(String forme_pharmaceutique) {
        this.forme_pharmaceutique = forme_pharmaceutique;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
