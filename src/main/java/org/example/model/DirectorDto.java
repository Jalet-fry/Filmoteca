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
public class DirectorDto {
    private long id;
    private String name;
    @Builder.Default
    private List<FilmDto> films = new ArrayList<>();
}