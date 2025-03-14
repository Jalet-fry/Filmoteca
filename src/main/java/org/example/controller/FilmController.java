package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Convert;
import org.example.model.FilmDto;
import org.example.model.db.Film;
import org.example.service.FilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.example.model.Convert.toDto;
import static org.example.model.Convert.toEntity;


@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody FilmDto filmDto) {
        filmService.create(toEntity(filmDto));
        return ResponseEntity.status(201).body("Film created successfully");
    }
    @PutMapping
    public ResponseEntity<String> change(@RequestBody FilmDto filmDto) {
        filmService.update(toEntity(filmDto));
        return ResponseEntity.status(201).body("Film created successfully");
    }

    @GetMapping
    public ResponseEntity<FilmDto> getByTitle(@RequestParam String title) {
        Optional<Film> film = Optional.ofNullable(filmService.getByTitle(title));
        return film.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable int id) {
        Optional<Film> film = Optional.ofNullable(filmService.get(id));
        return film.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Новый метод для получения всех фильмов
    @GetMapping("/all")
    public ResponseEntity<List<FilmDto>> getAllFilms() {
        List<FilmDto> filmDtos = filmService.getAll().stream().map(Convert::toDto).toList();
        return ResponseEntity.ok(filmDtos);
    }



}
