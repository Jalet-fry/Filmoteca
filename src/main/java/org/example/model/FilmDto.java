package org.example.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class FilmDto {
    private long id;
    private String director;
    @Builder.Default
    private List<String> actors = new ArrayList<>();
    private String title;
    private String link;
    private int year;
}