package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.FilmDTO;
import org.example.model.db.Director;
import org.example.model.db.Film;
import org.example.service.FilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody FilmDTO filmDTO) {
        filmService.create(toEntity(filmDTO));
        return ResponseEntity.status(201).body("Film created successfully");
    }

    @GetMapping
    public ResponseEntity<FilmDTO> getByTitle(@RequestParam String title) {
        Optional<Film> film = Optional.ofNullable(filmService.getByTitle(title));
        return film.map(entity -> ResponseEntity.ok(toDTO(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable int id) {
        Optional<Film> film = Optional.ofNullable(filmService.get(id));
        return film.map(entity -> ResponseEntity.ok(toDTO(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Новый метод для получения всех фильмов
    @GetMapping("/all")
    public ResponseEntity<List<FilmDTO>> getAllFilms() {
        List<FilmDTO> filmDTOS = filmService.getAll().stream().map(this::toDTO).toList();
        return ResponseEntity.ok(filmDTOS);
    }

    private Film toEntity(FilmDTO from) {
        var to = new Film();
        to.setId(from.getId());
        to.setLink(from.getLink());
        to.setYear(from.getYear());
        to.setTitle(from.getTitle());
        //entity.setDirector(Director.builder().id(dto.getDirector_id()).build());
        to.setDirector(from.getDirectorId() == null ? null : new Director(from.getDirectorId()));
        return to;
    }

    private FilmDTO toDTO(Film from) {
        return FilmDTO.builder()
                .id(from.getId())
                .link(from.getLink())
                .year(from.getYear())
                .title(from.getTitle())
                .directorId(from.getDirector().getId())
                //director(конвертация в directorDTO
                .build();
    }


}
