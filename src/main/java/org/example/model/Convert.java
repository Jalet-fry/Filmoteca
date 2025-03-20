/*
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
*/

//package org.example.model;
//
//import java.util.List;
//import java.util.stream.Collectors;
//import org.example.model.db.Actor;
//import org.example.model.db.Director;
//import org.example.model.db.Film;
//
//public class Convert {
//
//    // Private constructor to prevent instantiation
//    private Convert() {
//        throw new UnsupportedOperationException("Utility class");
//    }
//
//    public static Film toEntity(FilmDto from) {
//        if (from == null) {
//            return null;
//        }
//
//        // Extracting the nested ternary operation into an independent statement
//        var actors = from.getActors() == null ? null
//                : from.getActors().stream()
//                .map(name -> Actor.builder().name(name).build())
//                .toList();
//
//        return Film.builder()
//                .id(from.getId())
//                .link(from.getLink())
//                .year(from.getYear())
//                .title(from.getTitle())
//                .director(Director.builder().name(from.getDirector()).build())
//                .actors(actors)
//                .build();
//    }
//
//    public static Director toEntity(DirectorDto from) {
//        if (from == null) {
//            return null;
//        }
//
//        var films = from.getFilms() == null ? null
//                : from.getFilms().stream()
//                .map(Convert::toEntity)
//                .toList();
//
//        return Director.builder()
//                .id(from.getId())
//                .name(from.getName())
//                .films(films)
//                .build();
//    }
//
//    public static Actor toEntity(ActorDto from) {
//        if (from == null) {
//            return null;
//        }
//
//        var films = from.getFilms() == null ? null
//                : from.getFilms().stream()
//                .map(Convert::toEntity)
//                .toList();
//
//        return Actor.builder()
//                .id(from.getId())
//                .name(from.getName())
//                .films(films)
//                .build();
//    }
//
//
//    public static ActorDto toDto(Actor from) {
//        if (from == null) {
//            return null;
//        }
//
//        var films = from.getFilms() == null ? null
//                : from.getFilms().stream()
//                .map(Convert::toDto)
//                .toList();
//
//        return ActorDto.builder()
//                .id(from.getId())
//                .name(from.getName())
//                .films(films)
//                .build();
//    }
//
//    public static DirectorDto toDto(Director from) {
//        if (from == null) {
//            return null;
//        }
//
//        var films = from.getFilms() == null ? null
//                : from.getFilms().stream()
//                .map(Convert::toDto)
//                .toList();
//
//        return DirectorDto.builder()
//                .id(from.getId())
//                .name(from.getName())
//                .films(films)
//                .build();
//    }
//
//    public static FilmDto toDto(Film from) {
//        if (from == null) {
//            return null;
//        }
//
//        var actors = from.getActors() == null ? null
//                : from.getActors().stream()
//                .map(Actor::getName)
//                .toList();
//
//        return FilmDto.builder()
//                .id(from.getId())
//                .link(from.getLink())
//                .year(from.getYear())
//                .title(from.getTitle())
//                .director(from.getDirector().getName())
//                .actors(actors)
//                .build();
//    }
//
//    public static List<FilmDto> toDtoList(List<Film> films) {
//        if (films == null) {
//            return null;
//        }
//        return films.stream()
//                .map(Convert::toDto)
//                .collect(Collectors.toList());
//    }
//
//
//    public static List<ActorDto> toDtoListActors(List<Actor> actors) {
//        if (actors == null) {
//            return null;
//        }
//        return actors.stream()
//                .map(Convert::toDto)
//                .collect(Collectors.toList());
//    }
//
//
//    public static List<DirectorDto> toDtoListDirectors(List<Director> directors) {
//        if (directors == null) {
//            return null;
//        }
//        return directors.stream()
//                .map(Convert::toDto)
//                .collect(Collectors.toList());
//    }
//}

package org.example.model;

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
        return toDto(from, 2);
    }

    private static ActorDto toDto(Actor from, int depth) {
        if (from == null) {
            return null;
        }
        ActorDto dto = ActorDto.builder()
                .id(from.getId())
                .firstName(from.getLastName().isEmpty() ? from.getLastName() : null)
                .secondName(from.getLastName().isEmpty() ? from.getLastName() : null)
                .lastName(from.getLastName().isEmpty() ? from.getLastName() : null)
                .build();
        if (depth > 0 && from.getFilms() != null) {
            dto.setFilms(from.getFilms().stream()
                    .map(entity -> toDto(entity, depth - 1))
                    .toList());
        }
        return dto;
    }

    public static DirectorDto toDto(Director from) {
        return toDto(from, 2);
    }

    public static DirectorDto toDto(Director from, int depth) {
        if (from == null) {
            return null;
        }

        DirectorDto dto =  DirectorDto.builder()
                .id(from.getId())
                .firstName(from.getFirstName())
                .secondName(from.getSecondName())
                .lastName(from.getLastName())
                .build();
        if (depth > 0 && from.getFilms() != null) {
            dto.setFilms(from.getFilms().stream()
                    .map(entity -> toDto(entity, depth - 1))
                    .toList());
        }
        return dto;
    }

    public static FilmDto toDto(Film from) {
        return toDto(from, 2);
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
            return null;
        }
        return films.stream()
                .map(Convert::toDto)
                .collect(Collectors.toList());
    }

    public static List<ActorDto> toDtoListActors(List<Actor> actors) {
        if (actors == null) {
            return null;
        }
        return actors.stream()
                .map(Convert::toDto)
                .collect(Collectors.toList());
    }

    public static List<DirectorDto> toDtoListDirectors(List<Director> directors) {
        if (directors == null) {
            return null;
        }
        return directors.stream()
                .map(Convert::toDto)
                .collect(Collectors.toList());
    }
}