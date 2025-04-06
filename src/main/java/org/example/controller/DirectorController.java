package org.example.controller;

import static org.example.model.Convert.toDto;
import static org.example.model.Convert.toEntity;
import static org.example.model.Convert.toEntityListActors;
import static org.example.model.Convert.toEntityListDirectors;
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
import org.example.model.ActorDto;
import org.example.model.Convert;
import org.example.model.DirectorDto;
import org.example.model.db.Actor;
import org.example.model.db.Director;
import org.example.service.DirectorService;
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
@RequestMapping("/directors")
@Tag(name = "Director Controller", description = "API for managing film directors")
public class DirectorController {
    private final DirectorService directorService;

    @Operation(summary = "Create a new director")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Director created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",

			content = @Content),
        @ApiResponse(responseCode = "409", description = "Director already exists",

			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",

			content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody DirectorDto directorDto) {
        Director director = toEntity(directorDto);
        director.setId(0);
        director.setFilms(null);
        directorService.create(director);
        return ResponseEntity.status(201).body("Director created successfully");
    }


    @Operation(summary = "Create multiple directors in bulk")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Actors created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "One or more directors already exist"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/bulk")
    public ResponseEntity<String> createDirectorsBulk(
            @Valid @RequestBody List<DirectorDto> directorDtos) {
        List<Director> directors = toEntityListDirectors(directorDtos);
        directorService.createAll(directors);
        return ResponseEntity.status(201).body(
                "Successfully created " + directors.size() + " directors");
    }

    @Operation(summary = "Update a director with full details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Director updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",

			content = @Content),
        @ApiResponse(responseCode = "409", description = "Director already exists",

			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",

			content = @Content)
    })
    @PutMapping
    public ResponseEntity<String> put(@Valid @RequestBody DirectorDto directorDto) {
        directorService.put(toEntity(directorDto));
        return ResponseEntity.status(200).body("Director changed successfully");
    }

    @Operation(summary = "Partially update a director")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Director partially updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",

			content = @Content),
        @ApiResponse(responseCode = "404", description = "Director not found",

			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",

			content = @Content)
    })
    @PatchMapping
    public ResponseEntity<String> patch(@Valid @RequestBody DirectorDto directorDto) {
        directorService.patch(toEntity(directorDto));
        return ResponseEntity.status(200).body("Director changed successfully");
    }

    @Operation(summary = "Delete a director by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Director deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied",

			content = @Content),
        @ApiResponse(responseCode = "404", description = "Director not found",

			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",

			content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@Valid @Min(1) @PathVariable long id) {
        directorService.delete(id);
        return ResponseEntity.status(200).body("Director deleted successfully");
    }

    @Operation(summary = "Get a director by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Director found"),
        @ApiResponse(responseCode = "400", description = "Invalid name supplied",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Director not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @GetMapping
    public ResponseEntity<DirectorDto> getByName(
            @Valid @Size(max = MAXTEXTSIZE)
            @RequestParam String name) {
        Optional<Director> director = Optional.ofNullable(directorService.getByName(name));
        return director.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a director by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Director found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Director not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DirectorDto> get(@Valid @Min(1) @PathVariable Long id) {
        Optional<Director> director = Optional.ofNullable(directorService.get(id));
        return director.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all directors")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of all directors"),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @GetMapping("/all")
    public ResponseEntity<List<DirectorDto>> getAllDirectors() {
        List<DirectorDto> directorDtos = Convert.toDtoListDirectors(directorService.getAll());
        return ResponseEntity.ok(directorDtos);
    }
}



