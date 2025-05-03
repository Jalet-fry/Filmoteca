package org.example.controller;

import static org.example.model.Convert.toDto;
import static org.example.model.Convert.toDtoList;
import static org.example.model.Convert.toEntity;
import static org.example.model.Convert.toEntityListFilms;
import static org.example.utils.Utils.MAXTEXTSIZE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.model.Convert;
import org.example.model.FilmDto;
import org.example.model.db.Film;
import org.example.service.FilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Tag(name = "Film Controller", description = "API for managing films")
@Validated
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/test-load")
    public String testEndpoint() {
        return "OK";
    }

    @Operation(summary = "Create a new film")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Film created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
			content = @Content),
        @ApiResponse(responseCode = "409", description = "Film already exists",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody FilmDto filmDto) {
        Film film = toEntity(filmDto);
        film.setId(0);
        filmService.create(film);
        return ResponseEntity.status(201).body("Film created successfully");
    }

    @Operation(summary = "Create multiple films in bulk")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Films created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "One or more films already exist"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/bulk")
    public ResponseEntity<String> createFilmsBulk(@Valid @RequestBody List<FilmDto> filmDtos) {
        List<Film> films = toEntityListFilms(filmDtos);
        filmService.createAll(films);
        return ResponseEntity.status(201).body("Successfully created " + films.size() + " films");
    }

    @Operation(summary = "Update a film with full details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Film updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Film not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @PutMapping
    public ResponseEntity<String> put(@Valid @RequestBody FilmDto filmDto) {
        filmService.put(toEntity(filmDto));
        return ResponseEntity.status(200).body("Film changed successfully");
    }

    @Operation(summary = "Partially update a film") // Добавить
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Film partially updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Film not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @PatchMapping
    public ResponseEntity<String> patch(@Valid @RequestBody FilmDto filmDto) {
        filmService.patch(toEntity(filmDto));
        return ResponseEntity.status(200).body("Film changed successfully");
    }

    @Operation(summary = "Delete a film by ID") // Добавить
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Film deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Film not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@Valid @Min(1) @PathVariable long id) {
        filmService.delete(id);
        return ResponseEntity.status(200).body("Film deleted successfully");
    }

    @Operation(summary = "Get films by title") // Добавить
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Films found"),
        @ApiResponse(responseCode = "400", description = "Invalid title supplied",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "No films found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<FilmDto>> getByTitle(
            @Valid @Size(max = MAXTEXTSIZE) @RequestParam String title) {
        List<Film> films = filmService.getByTitle(title);
        if (films != null && !films.isEmpty()) {
            return ResponseEntity.ok(toDtoList(films));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get a film by ID") // Добавить
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Film found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Film not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<FilmDto> get(@Valid @Min(1) @PathVariable Long id) {
        Optional<Film> film = Optional.ofNullable(filmService.get(id));
        return film.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all films with optional filters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of films"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @GetMapping("/all")
    public ResponseEntity<List<FilmDto>> getAllFilms(
            @Valid @Size(max = MAXTEXTSIZE) @RequestParam(required = false) String director,
            @Valid @Size(max = MAXTEXTSIZE) @RequestParam(required = false) String actor
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