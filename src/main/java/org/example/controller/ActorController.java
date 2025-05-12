package org.example.controller;

import static org.example.model.Convert.toDto;
import static org.example.model.Convert.toEntity;
import static org.example.model.Convert.toEntityListActors;
import static org.example.utils.Utils.MAXTEXTSIZE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.model.ActorDto;
import org.example.model.Convert;
import org.example.model.db.Actor;
import org.example.service.ActorService;
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
@RequestMapping("/actors")
@Tag(name = "Actor Controller", description = "API for managing film actors")
@Validated
public class ActorController {
    private final ActorService actorService;

    @Operation(summary = "Create a new actor") // Добавить
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Actor created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
			content = @Content),
        @ApiResponse(responseCode = "409", description = "Actor already exists",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody ActorDto actorDto) {
        Actor actor = toEntity(actorDto);
        actor.setId(0);
        actor.setFilms(null);
        actorService.create(actor);
        return ResponseEntity.status(201).body("Actor created successfully");
    }

    @Operation(summary = "Create multiple actors in bulk")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Actors created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "One or more actors already exist"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/bulk")
    public ResponseEntity<String> createActorsBulk(@Valid @RequestBody List<ActorDto> actorDtos) {
        List<Actor> actors = toEntityListActors(actorDtos);
        actorService.createAll(actors);
        return ResponseEntity.status(201).body("Successfully created " + actors.size() + " actors");
    }

    @Operation(summary = "Update an actor with full details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Actor updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Actor not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @PutMapping
    public ResponseEntity<String> put(@Valid @RequestBody ActorDto actorDto) {
        Actor actor = toEntity(actorDto);
        actor.setFilms(null);
        actorService.put(actor);
        return ResponseEntity.status(200).body("Actor changed successfully");
    }

    @Operation(summary = "Partially update an actor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Actor partially updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Actor not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @PatchMapping
    public ResponseEntity<String> patch(@Valid @RequestBody ActorDto actorDto) {
        Actor actor = toEntity(actorDto);
        actor.setFilms(null);
        actorService.patch(actor);
        return ResponseEntity.status(200).body("Actor changed successfully");
    }

    @Operation(summary = "Delete an actor by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Actor deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Actor not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@Valid @Min(1) @PathVariable long id) {
        actorService.delete(id);
        return ResponseEntity.status(200).body("Actor deleted successfully");
    }

    @Operation(summary = "Get an actor by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Actor found"),
        @ApiResponse(responseCode = "400", description = "Invalid name supplied",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Actor not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @GetMapping
    public ResponseEntity<ActorDto> getByName(
            @Valid @Size(max = MAXTEXTSIZE) @RequestParam String firstName,
            @Valid @Size(max = MAXTEXTSIZE) @RequestParam String secondName,
            @Valid @Size(max = MAXTEXTSIZE) @RequestParam String lastName) {
        Optional<Actor> actor = Optional.ofNullable(
                actorService.getByName(firstName, secondName, lastName));
        return actor.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get an actor by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Actor found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
			content = @Content),
        @ApiResponse(responseCode = "404", description = "Actor not found",
			content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActorDto> get(@Valid @Min(1) @PathVariable Long id) {
        Optional<Actor> actor = Optional.ofNullable(actorService.get(id));
        return actor.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all actors")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of all actors"),
        @ApiResponse(responseCode = "500", description = "Internal server error",
			content = @Content)
    })
    @GetMapping("/all")
    public ResponseEntity<List<ActorDto>> getAllActors() {
        List<ActorDto> actorDtos = Convert.toDtoListActors(
                actorService.getAll().stream().sorted(
                        Comparator.comparingLong(Actor::getId)).toList());
        return ResponseEntity.ok(actorDtos);
    }

}