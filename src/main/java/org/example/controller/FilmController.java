package org.example.controller;

import static org.example.model.Convert.toDto;
import static org.example.model.Convert.toDtoList;
import static org.example.model.Convert.toEntity;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.model.Convert;
import org.example.model.FilmDto;
import org.example.model.db.Film;
import org.example.service.FilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @SneakyThrows
    @PostMapping
    public ResponseEntity<String> create(@RequestBody FilmDto filmDto) {
        Film film = toEntity(filmDto);
        film.setId(0);
        filmService.create(film);
        return ResponseEntity.status(201).body("Film created successfully");
    }

    @PutMapping
    public ResponseEntity<String> put(@RequestBody FilmDto filmDto) {
        filmService.put(toEntity(filmDto));
        return ResponseEntity.status(200).body("Film changed successfully");
    }

    @PatchMapping
    public ResponseEntity<String> patch(@RequestBody FilmDto filmDto) {
        filmService.patch(toEntity(filmDto));
        return ResponseEntity.status(200).body("Film changed successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        filmService.delete(id);
        return ResponseEntity.status(200).body("Film deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<FilmDto>> getByTitle(@RequestParam String title) {
        List<Film> films = filmService.getByTitle(title);
        if (films != null && !films.isEmpty()) {
            return ResponseEntity.ok(toDtoList(films));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Film> film = Optional.ofNullable(filmService.get(id));
        return film.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<FilmDto>> getAllFilms(
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String actor
    ) {
        List<Film> films;
        if (actor != null && director != null) {
            films = filmService.findByActorAndDirector(actor, director);
        } else if (actor != null) {
            films = filmService.getByActor(actor);
        } else  if (director != null) {
            films = filmService.getByDirector(director);
        } else {
            films = filmService.getAll();
        }
        return ResponseEntity.ok(films.stream().map(Convert::toDto).toList());
    }
}
