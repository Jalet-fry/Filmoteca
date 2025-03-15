package org.example.model;

import org.example.model.db.Actor;
import org.example.model.db.Director;
import org.example.model.db.Film;

public class Convert {
    public static Film toEntity(FilmDto from) {
        return from == null ? null : Film.builder()
                .id(from.getId())
                .link(from.getLink())
                .year(from.getYear())
                .title(from.getTitle())
                .director(Director.builder().name(from.getDirector()).build())
                .actors(from.getActors() == null ? null
                        : from.getActors().stream()
                        .map(name -> Actor.builder().name(name).build()).toList())
                .build();
    }

    public static Director toEntity(DirectorDto from) {
        return from == null ? null : Director.builder()
                .id(from.getId())
                .name(from.getName())
                .films(from.getFilms() == null ? null : from.getFilms()
                        .stream().map(Convert::toEntity).toList())
                .build();
    }

    public static Actor toEntity(ActorDto from) {
        return from == null ? null : Actor.builder()
                .id(from.getId())
                .name(from.getName())
                .films(from.getFilms() == null ? null : from.getFilms()
                    .stream().map(Convert::toEntity).toList())
                .build();
    }

    public static FilmDto toDto(Film from) {
        return from == null ? null : FilmDto.builder()
                .id(from.getId())
                .link(from.getLink())
                .year(from.getYear())
                .title(from.getTitle())
                .director(from.getDirector().getName())
                .actors(from.getActors() == null ? null
                        : from.getActors().stream()
                        .map(Actor::getName).toList())
                .build();
    }


    public static ActorDto toDto(Actor from) {
        return from == null ? null : ActorDto.builder()
                .id(from.getId())
                .name(from.getName())
                .films(from.getFilms() == null ? null
                        : from.getFilms().stream()
                        .map(Convert::toDto).toList())
                .build();
    }


    public static DirectorDto toDto(Director from) {
        return from == null ? null : DirectorDto.builder()
                .id(from.getId())
                .name(from.getName())
                .films(from.getFilms() == null ? null
                        : from.getFilms().stream()
                        .map(Convert::toDto).toList())
                .build();
    }
}
