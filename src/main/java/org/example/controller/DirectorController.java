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
import org.example.model.Convert;
import org.example.model.DirectorDto;
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
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody DirectorDto directorDto) {
        Director director = toEntity(directorDto);
        director.setId(0);
        director.setFilms(null);
        directorService.create(director);
        return ResponseEntity.status(201).body("Director created successfully");
    }

    @PutMapping
    public ResponseEntity<String> put(@Valid @RequestBody DirectorDto directorDto) {
        directorService.put(toEntity(directorDto));
        return ResponseEntity.status(200).body("Director changed successfully");
    }

    @PatchMapping
    public ResponseEntity<String> patch(@Valid @RequestBody DirectorDto directorDto) {
        directorService.patch(toEntity(directorDto));
        return ResponseEntity.status(200).body("Director changed successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@Valid @Min(1) @PathVariable long id) {
        directorService.delete(id);
        return ResponseEntity.status(200).body("Director deleted successfully");
    }

    @GetMapping
    public ResponseEntity<DirectorDto> getByName(
            @Valid @Size(max = MAXTEXTSIZE)
            @RequestParam String name) {
        Optional<Director> director = Optional.ofNullable(directorService.getByName(name));
        return director.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorDto> get(@Valid @Min(1) @PathVariable Long id) {
        Optional<Director> director = Optional.ofNullable(directorService.get(id));
        return director.map(entity -> ResponseEntity.ok(toDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<DirectorDto>> getAllDirectors() {
        List<DirectorDto> directorDtos = Convert.toDtoListDirectors(directorService.getAll());
        return ResponseEntity.ok(directorDtos);
    }
}
