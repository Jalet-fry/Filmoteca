package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class FilmDTO {
    private int id;
    private Integer directorId;
    //TODO:private DirectorDTO director;
    private String title;
    private String link;
    private int year;
}