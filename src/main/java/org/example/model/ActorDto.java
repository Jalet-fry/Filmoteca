package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class ActorDto {
    private int id;
    private String name;
    //@Builder.Default
    private List<FilmDto> films = new ArrayList<>();
}