package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.model.ActorDto;
import org.example.model.DirectorDto;
import org.example.model.FilmDto;
import org.example.model.db.Actor;
import org.example.model.db.Director;
import org.example.model.db.Film;
import org.example.service.FilmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class
FilmControllerTest {

    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    private Film testFilm;
    private FilmDto testFilmDto;

    @BeforeEach
    void setUp() {
        Director director = new Director();
        director.setId(1L);
        director.setFirstName("Christopher");
        director.setSecondName("");
        director.setLastName("Nolan");

        DirectorDto directorDto = new DirectorDto(1L, "Christopher", "Nolan", null, null);

        Actor actor = new Actor();
        actor.setId(1L);
        actor.setFirstName("Leonardo");
        actor.setSecondName("");
        actor.setLastName("DiCaprio");

        ActorDto actorDto = new ActorDto(1L, "Leonardo", "DiCaprio", null, null);

        testFilm = new Film();
        testFilm.setId(1L);
        testFilm.setTitle("Inception");
        testFilm.setYear(2010);
        testFilm.setDirector(director);
        testFilm.setActors(List.of(actor));

        testFilmDto = FilmDto.builder()
                .id(1L)
                .title("Inception")
                .year(2010)
                .director(directorDto)
                .actors(List.of(actorDto))
                .build();
    }

    @Test
    void create_ShouldReturnCreatedResponse() {
        ResponseEntity<String> response = filmController.create(testFilmDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Film created successfully", response.getBody());
        verify(filmService).create(any(Film.class));
    }

    @Test
    void createFilmsBulk_ShouldReturnCreatedResponse() {
        List<FilmDto> filmDtos = List.of(testFilmDto);

        ResponseEntity<String> response = filmController.createFilmsBulk(filmDtos);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Successfully created 1 films", response.getBody());
        verify(filmService).createAll(anyList());
    }

    @Test
    void put_ShouldReturnOkResponse() {
        ResponseEntity<String> response = filmController.put(testFilmDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Film changed successfully", response.getBody());
        verify(filmService).put(any(Film.class));
    }

    @Test
    void patch_ShouldReturnOkResponse() {
        ResponseEntity<String> response = filmController.patch(testFilmDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Film changed successfully", response.getBody());
        verify(filmService).patch(any(Film.class));
    }

    @Test
    void delete_ShouldReturnOkResponse() {
        ResponseEntity<String> response = filmController.delete(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Film deleted successfully", response.getBody());
        verify(filmService).delete(1L);
    }

    @Test
    void getByTitle_ShouldReturnFilms() {
        when(filmService.getByTitle("Inception")).thenReturn(List.of(testFilm));

        ResponseEntity<List<FilmDto>> response = filmController.getByTitle("Inception");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals("Inception", response.getBody().get(0).getTitle());
    }

    @Test
    void getByTitle_ShouldReturnNotFound() {
        when(filmService.getByTitle("Unknown")).thenReturn(List.of());

        ResponseEntity<List<FilmDto>> response = filmController.getByTitle("Unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getByTitle_ShouldReturnNotFound_WhenFilmsIsNull() {
        when(filmService.getByTitle("Inception")).thenReturn(null);

        ResponseEntity<List<FilmDto>> response = filmController.getByTitle("Inception");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    void get_ShouldReturnFilm() {
        when(filmService.get(1L)).thenReturn(testFilm);

        ResponseEntity<FilmDto> response = filmController.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Inception", response.getBody().getTitle());
    }

    @Test
    void get_ShouldReturnNotFound() {
        when(filmService.get(999L)).thenReturn(null);

        ResponseEntity<FilmDto> response = filmController.get(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllFilms_ShouldReturnAllFilms() {
        when(filmService.getAll()).thenReturn(List.of(testFilm));

        ResponseEntity<List<FilmDto>> response = filmController.getAllFilms(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getAllFilms_ShouldReturnFilmsByDirector() {
        when(filmService.getByDirector("Nolan")).thenReturn(List.of(testFilm));

        ResponseEntity<List<FilmDto>> response = filmController.getAllFilms("Nolan", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getAllFilms_ShouldReturnFilmsByActor() {
        when(filmService.getByActor("DiCaprio")).thenReturn(List.of(testFilm));

        ResponseEntity<List<FilmDto>> response = filmController.getAllFilms(null, "DiCaprio");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getAllFilms_ShouldReturnFilmsByActorAndDirector() {
        when(filmService.findByActorAndDirector("DiCaprio", "Nolan")).thenReturn(List.of(testFilm));

        ResponseEntity<List<FilmDto>> response = filmController.getAllFilms("Nolan", "DiCaprio");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }
}