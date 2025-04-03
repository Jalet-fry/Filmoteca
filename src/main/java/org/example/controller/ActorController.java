package org.example.controller;

import static org.example.model.Convert.toDto;
import static org.example.model.Convert.toEntity;
import static org.example.utils.Utils.MAXTEXTSIZE;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.model.ActorDto;
import org.example.model.Convert;
import org.example.model.db.Actor;
import org.example.service.ActorService;
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
@RequestMapping("/actors")
public class ActorController {
    private final ActorService actorService;

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody ActorDto actorDto) {
        Actor actor = toEntity(actorDto);
        actor.setId(0);
        actor.setFilms(null);
        actorService.create(actor);
        return ResponseEntity.status(201).body("Actor created successfully");
    }

    @PutMapping
    public ResponseEntity<String> put(@Valid @RequestBody ActorDto actorDto) {
        Actor actor = toEntity(actorDto);
        actor.setFilms(null);
        actorService.put(actor);
        return ResponseEntity.status(200).body("Actor changed successfully");
    }

    @PatchMapping
    public ResponseEntity<String> patch(@Valid @RequestBody ActorDto actorDto) {
        Actor actor = toEntity(actorDto);
        actor.setFilms(null);
        actorService.patch(actor);
        return ResponseEntity.status(200).body("Actor changed successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@Valid @Min(1) @PathVariable long id) {
        actorService.delete(id);
        return ResponseEntity.status(200).body("Actor deleted successfully");
    }

    @GetMapping
    public ResponseEntity<ActorDto> getByName(
            @Valid @Size(max = MAXTEXTSIZE)
            @RequestParam String name) {
        Optional<Actor> actor = Optional.ofNullable(actorService.getByName(name));
        return actor.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorDto> get(@Valid @Min(1) @PathVariable Long id) {
        Optional<Actor> actor = Optional.ofNullable(actorService.get(id));
        return actor.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ActorDto>> getAllActors() {
        List<ActorDto> actorDtos = Convert.toDtoListActors(actorService.getAll());
        return ResponseEntity.ok(actorDtos);
    }

}
