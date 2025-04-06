package org.example.model.db;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "film")
public class Film {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private Integer year;

    @Column
    private String link;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "director_id")
    private Director director;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Builder.Default
    private List<Actor> actors = new ArrayList<>();


    public void updateForPatch(Film film) {
        if (film.getTitle() != null) {
            this.setTitle(film.getTitle());
        }
        if (film.getYear() != null) {
            this.setYear(film.getYear());
        }
        if (film.getLink() != null) {
            this.setLink(film.getLink());
        }
    }

    public void updateForPut(Film film) {
        this.setTitle(film.getTitle());
        this.setYear(film.getYear());
        this.setLink(film.getLink());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Film[");

        if (title != null) {
            sb.append("title='").append(title).append("'");
        }

        if (year != null) {
            if (sb.length() > 6) {
                sb.append(", ");
            }
            sb.append("year=").append(year);
        }

        if (director != null && director.getFullName() != null) {
            if (sb.length() > 6) {
                sb.append(", ");
            }
            sb.append("director='").append(director.getFullName()).append("'");
        }

        return sb.append("]").toString();
    }

}