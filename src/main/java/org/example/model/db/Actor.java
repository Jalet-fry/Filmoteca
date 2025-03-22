package org.example.model.db;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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
@Table(name = "actor")
public class Actor {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;
    @Column(name = "last_name")
    private String lastName;

    @ManyToMany(mappedBy = "actors")//(cascade = CascadeType.ALL)
    private List<Film> films;


    public void updateForPatch(Actor newActor) {
        if (!newActor.getFirstName().isEmpty()) {
            this.setFirstName(newActor.getFirstName());
        }
        if (!newActor.getSecondName().isEmpty()) {
            this.setSecondName(newActor.getSecondName());
        }
        if (!newActor.getLastName().isEmpty()) {
            this.setLastName(newActor.getLastName());
        }
    }

    public void updateForPut(Actor newActor) {
        this.setFirstName(newActor.getFirstName());
        this.setSecondName(newActor.getSecondName());
        this.setLastName(newActor.getLastName());
    }
}