package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.example.model.db.Actor;
import org.example.model.db.Director;
import org.example.model.db.Film;

public class Convert {

    private Convert() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Film toEntity(FilmDto from) {
        if (from == null) {
            return null;
        }

        var actors = from.getActors() == null ? null
                : from.getActors().stream()
                .map(Convert::toEntity)
                .toList();

        var director = from.getDirector() == null ? null
                : toEntity(from.getDirector());

        return Film.builder()
                .id(from.getId())
                .link(from.getLink())
                .year(from.getYear())
                .title(from.getTitle())
                .director(director)
                .actors(actors)
                .build();
    }

    public static Director toEntity(DirectorDto from) {
        if (from == null) {
            return null;
        }

        var films = from.getFilms() == null ? null
                : from.getFilms().stream()
                .map(Convert::toEntity)
                .toList();

        return Director.builder()
                .id(from.getId())
                .firstName(from.getFirstName() == null ? "" : from.getFirstName())
                .secondName(from.getSecondName() == null ? "" : from.getSecondName())
                .lastName(Optional.ofNullable(from.getLastName()).orElse(""))
                .films(films)
                .build();
    }

    public static Actor toEntity(ActorDto from) {
        if (from == null) {
            return null;
        }

        var films = from.getFilms() == null ? null
                : from.getFilms().stream()
                .map(Convert::toEntity)
                .toList();

        return Actor.builder()
                .id(from.getId())
                .firstName(from.getFirstName() == null ? "" : from.getFirstName())
                .secondName(from.getSecondName() == null ? "" : from.getSecondName())
                .lastName(Optional.ofNullable(from.getLastName()).orElse(""))
                .films(films)
                .build();
    }

    public static ActorDto toDto(Actor from) {
        return toDto(from, 1);
    }

    private static ActorDto toDto(Actor from, int depth) {
        if (from == null) {
            return null;
        }
        ActorDto dto = ActorDto.builder()
                .id(from.getId())
                .firstName(from.getFirstName().isEmpty() ? null : from.getFirstName())
                .secondName(from.getSecondName().isEmpty() ? null : from.getSecondName())
                .lastName(from.getLastName().isEmpty() ? null : from.getLastName())
                .build();
        if (depth > 0 && from.getFilms() != null) {
            dto.setFilms(from.getFilms().stream()
                    .map(entity -> toDto(entity, depth - 1))
                    .toList());
        }
        return dto;
    }

    public static DirectorDto toDto(Director from) {
        return toDto(from, 1);
    }

    public static DirectorDto toDto(Director from, int depth) {
        if (from == null) {
            return null;
        }

        DirectorDto dto =  DirectorDto.builder()
                .id(from.getId())
                .firstName(from.getFirstName().isEmpty() ? null : from.getFirstName())
                .secondName(from.getSecondName().isEmpty() ? null : from.getSecondName())
                .lastName(from.getLastName().isEmpty() ? null : from.getLastName())
                .build();
        if (depth > 0 && from.getFilms() != null) {
            dto.setFilms(from.getFilms().stream()
                    .map(entity -> toDto(entity, depth - 1))
                    .toList());
        }
        return dto;
    }

    public static FilmDto toDto(Film from) {
        return toDto(from, 1);
    }

    public static FilmDto toDto(Film from, int depth) {
        if (from == null) {
            return null;
        }

        FilmDto dto = FilmDto.builder()
                .id(from.getId())
                .link(from.getLink())
                .year(from.getYear())
                .title(from.getTitle())
                .build();
        if (depth > 0 && from.getDirector() != null) {
            dto.setDirector(toDto(from.getDirector(), depth - 1));
        }
        if (depth > 0 && from.getActors() != null) {
            dto.setActors(from.getActors().stream()
                    .map(entity -> toDto(entity, depth - 1))
                    .toList());
        }
        return dto;
    }

    public static List<FilmDto> toDtoList(List<Film> films) {
        if (films == null) {
            return new ArrayList<>();
        }
        return films.stream()
                .map(Convert::toDto)
                .toList();
    }

    public static List<ActorDto> toDtoListActors(List<Actor> actors) {
        if (actors == null) {
            return new ArrayList<>();
        }
        return actors.stream()
                .map(Convert::toDto)
                .toList();
    }

    public static List<DirectorDto> toDtoListDirectors(List<Director> directors) {
        if (directors == null) {
            return new ArrayList<>();
        }
        return directors.stream()
                .map(Convert::toDto)
                .toList();
    }

    public static List<Film> toEntityListFilms(List<FilmDto> filmDtos) {
        if (filmDtos == null) {
            return new ArrayList<>();
        }
        return filmDtos.stream()
                .map(Convert::toEntity)
                .toList();
    }

    public static List<Director> toEntityListDirectors(List<DirectorDto> directorDtos) {
        if (directorDtos == null) {
            return new ArrayList<>();
        }
        return directorDtos.stream()
                .map(Convert::toEntity)
                .toList();
    }

    public static List<Actor> toEntityListActors(List<ActorDto> actorDtos) {
        if (actorDtos == null) {
            return new ArrayList<>();
        }
        return actorDtos.stream()
                .map(Convert::toEntity)
                .toList();
    }
}