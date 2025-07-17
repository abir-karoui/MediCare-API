package tn.exemple.medicare.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Data
@Table(name = "Diseases")
@Getter
@Setter
public class Diseases implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name ;

    @JsonIgnore
    @ManyToMany(mappedBy = "diseases")
    Set<Patient> patients;

}
