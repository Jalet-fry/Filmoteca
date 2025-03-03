// Old but work
/*
package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.model.Film;
import org.example.service.FilmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@AllArgsConstructor
@RequestMapping("/films")
public class FilmController {
    // @Autowired
    private FilmService filmService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Film film) {
        filmService.create(film);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Film> getByTitle(@RequestParam String title) {
        Film film = filmService.getByTitle(title);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Film> get(@PathVariable(name = "id") int id) {
        Film film = filmService.get(id);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }
}
*/
package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Film;
import org.example.service.FilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Film film) {
        filmService.create(film);
        return ResponseEntity.status(201).body("Film created successfully");
    }

    @GetMapping
    public ResponseEntity<?> getByTitle(@RequestParam String title) {
        Optional<Film> film = Optional.ofNullable(filmService.getByTitle(title));
        return film.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable int id) {
        Optional<Film> film = Optional.ofNullable(filmService.get(id));
        return film.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Новый метод для получения всех фильмов
    @GetMapping("/all")
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> films = filmService.getAll();
        return ResponseEntity.ok(films);
    }
}
