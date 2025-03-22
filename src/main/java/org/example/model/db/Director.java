package org.example.model.db;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "director")
public class Director {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName = "";
    @Column(name = "second_name")
    private String secondName = "";
    @Column(name = "last_name")
    private String lastName = "";
    //@OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "director_id")
    @Builder.Default
    private List<Film> films = new ArrayList<>();

    public void updateForPatch(Director newDirector) {
        if (!newDirector.getFirstName().isEmpty()) {
            this.setFirstName(newDirector.getFirstName());
        }
        if (!newDirector.getSecondName().isEmpty()) {
            this.setSecondName(newDirector.getSecondName());
        }
        if (!newDirector.getLastName().isEmpty()) {
            this.setLastName(newDirector.getLastName());
        }
    }

    public void updateForPut(Director newDirector) {
        this.setFirstName(newDirector.getFirstName());
        this.setSecondName(newDirector.getSecondName());
        this.setLastName(newDirector.getLastName());
    }
}