// Old but work

package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.model.Film;
import org.example.service.FilmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class FilmController {
    // @Autowired
    private FilmService filmService;

    @GetMapping(value = "/films")
    public ResponseEntity<?> create(@RequestParam String title) {
        filmService.create(new Film(title));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/films/{title}")
    public ResponseEntity<Film> get(@PathVariable(name = "title") String title) {
        // System.out.println(name);
        Film film = filmService.get(title);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }
}

/*
//New not good
package org.example.unknowed.controller;

import lombok.AllArgsConstructor;
import org.example.unknowed.model.Film;
import org.example.unknowed.service.FilmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class FilmController {
    private FilmService filmService;

    @GetMapping(value = "/films")
    public ResponseEntity<?> create(@RequestParam String title) {
        filmService.create(new Film(title));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/films/{title}")
    public ResponseEntity<Film> get(@PathVariable(name = "title") String title) {
        Film film = filmService.get(title);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }
}

 */


/*
@RestController
@RequestMapping("/films")

@Controller
@AllArgsConstructor
public class FilmController {
    private FilmService filmService;

    @GetMapping
    public ResponseEntity<?> create(@RequestBody Film film) {
        filmService.create(film);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "films/{title}")
    public ResponseEntity<Film> get(@PathVariable String title) {
        Film film = filmService.get(title);
        return film != null ? ResponseEntity.ok(film) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAll() {
        return ResponseEntity.ok(filmService.getAll());
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Film film) {
        filmService.update(film);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<?> delete(@PathVariable String title) {
        filmService.delete(title);
        return ResponseEntity.noContent().build();
    }
}
*/