package org.example.controller;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.model.FilmDto;
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
        List<FilmDto> filmDtos = filmService.getAll().stream().map(this::toDto).toList();
        return ResponseEntity.ok(filmDtos);
    }

    private Film toEntity(FilmDto from) {
        var to = new Film();
        to.setId(from.getId());
        to.setLink(from.getLink());
        to.setYear(from.getYear());
        to.setTitle(from.getTitle());
        //entity.setDirector(Director.builder().id(dto.getDirector_id()).build());
        to.setDirector(from.getDirectorId() == null ? null : new Director(from.getDirectorId()));
        return to;
    }

    private FilmDto toDto(Film from) {
        return FilmDto.builder()
                .id(from.getId())
                .link(from.getLink())
                .year(from.getYear())
                .title(from.getTitle())
                .directorId(from.getDirector().getId())
                //director(конвертация в directorDTO
                .build();
    }


}
