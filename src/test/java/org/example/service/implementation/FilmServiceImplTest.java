package org.example.service.implementation;

//import org.example.exception.FilmAlreadyExists;
//import org.example.model.db.Actor;
//import org.example.model.db.Director;
//import org.example.model.db.Film;
//import org.example.repository.ActorRepository;
//import org.example.repository.DirectorRepository;
//import org.example.repository.FilmRepository;
//import org.example.service.InMemoryCache;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Collections;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class FilmServiceImplTest {
//
//    @Mock
//    private FilmRepository filmRepository;
//
//    @Mock
//    private ActorRepository actorRepository;
//
//    @Mock
//    private DirectorRepository directorRepository;
//
//    @Mock
//    private InMemoryCache<Long, Film> inMemoryCache;
//
//    @InjectMocks
//    private FilmServiceImpl filmService;
//
//    @Test
//    void get_ShouldReturnFilm_WhenExists() {
//        Film film = new Film();
//        film.setId(1L);
//
//        when(inMemoryCache.get(1L)).thenReturn(Optional.of(film));
//
//        Film result = filmService.get(1L);
//
//        assertEquals(film, result);
//    }
//
//    @Test
//    void create_ShouldSaveFilm_WhenSuccessful() {
//        Film film = new Film();
//        film.setTitle("Test Film");
//
//        when(filmRepository.save(any(Film.class))).thenReturn(film);
//        when(filmRepository.existsByTitleAndYearAndDirector(anyString(), anyInt(), any()))
//                .thenReturn(false);
//
//        filmService.create(film);
//
//        verify(filmRepository).save(film);
//        verify(inMemoryCache).put(anyLong(), eq(film));
//    }
//
//    @Test
//    void delete_ShouldRemoveFilm_WhenExists() {
//        when(filmRepository.existsById(1L)).thenReturn(true);
//
//        filmService.delete(1L);
//
//        verify(filmRepository).deleteById(1L);
//        verify(inMemoryCache).del(1L);
//    }
//}

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import org.example.exception.BulkOperation;
import org.example.exception.FilmAlreadyExists;
import org.example.model.db.*;
import org.example.repository.*;
import org.example.service.InMemoryCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {

    @Mock
    private FilmRepository filmRepository;
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private InMemoryCache<Long, Film> inMemoryCache;

    @InjectMocks
    private FilmServiceImpl filmService;

    private Film testFilm;
    private Actor testActor;
    private Director testDirector;

    @BeforeEach
    void setUp() {
        testDirector = new Director();
        testDirector.setId(1L);
        testDirector.setFirstName("Christopher");
        testDirector.setLastName("Nolan");

        testActor = new Actor();
        testActor.setId(1L);
        testActor.setFirstName("Leonardo");
        testActor.setLastName("DiCaprio");

        testFilm = new Film();
        testFilm.setId(1L);
        testFilm.setTitle("Inception");
        testFilm.setYear(2010);
        testFilm.setLink("http://example.com/inception");
        testFilm.setDirector(testDirector);
        testFilm.setActors(List.of(testActor));
    }

    @Test
    void get_ShouldReturnFilmFromCache() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testFilm));

        Film result = filmService.get(1L);

        assertEquals(testFilm, result);
        verify(inMemoryCache).get(1L);
        verifyNoInteractions(filmRepository);
    }

    @Test
    void get_ShouldThrowWhenIdIsNull() {
        assertThrows(NullPointerException.class, () -> filmService.get(null));
    }

    @Test
    void create_ShouldSaveFilmWithRelations() {
        when(filmRepository.save(any(Film.class))).thenReturn(testFilm);
        when(directorRepository.getByFirstNameAndSecondNameAndLastName(any(), any(), any()))
                .thenReturn(Optional.of(testDirector));
        when(actorRepository.getByFirstNameAndSecondNameAndLastName(any(), any(), any()))
                .thenReturn(Optional.of(testActor));

        filmService.create(testFilm);

        verify(filmRepository).save(testFilm);
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void create_ShouldThrowWhenFilmIsNull() {
        assertThrows(NullPointerException.class, () -> filmService.create(null));
    }

    @Test
    void updateForPut_ShouldUpdateAllFields() {
        Film update = new Film();
        update.setId(1L);
        update.setTitle("New Title");
        update.setYear(2021);
        update.setLink("http://new.link");
        update.setDirector(new Director());
        update.setActors(List.of(new Actor()));

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testFilm));
        when(filmRepository.save(any(Film.class))).thenReturn(testFilm);

        filmService.put(update);

        assertEquals("New Title", testFilm.getTitle());
        assertEquals(2021, testFilm.getYear());
        assertEquals("http://new.link", testFilm.getLink());
        verify(filmRepository).save(testFilm);
    }

    @Test
    void updateForPatch_ShouldUpdateOnlyNonNullFields() {
        Film partialUpdate = new Film();
        partialUpdate.setId(1L);
        partialUpdate.setTitle("New Title");
        // year and link remain null

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testFilm));
        when(filmRepository.save(any(Film.class))).thenReturn(testFilm);

        filmService.patch(partialUpdate);

        assertEquals("New Title", testFilm.getTitle());
        assertEquals(2010, testFilm.getYear()); // remains unchanged
        assertEquals("http://example.com/inception", testFilm.getLink()); // remains unchanged
    }

    @Test
    void delete_ShouldRemoveFilmAndClearCache() {
        when(filmRepository.existsById(1L)).thenReturn(true);

        filmService.delete(1L);

        verify(filmRepository).deleteById(1L);
        verify(inMemoryCache).del(1L);
    }

    @Test
    void findByActorAndDirector_ShouldReturnFilms() {
        when(filmRepository.findByActorAndDirector("DiCaprio", "Nolan"))
                .thenReturn(List.of(testFilm));

        List<Film> results = filmService.findByActorAndDirector("DiCaprio", "Nolan");

        assertEquals(1, results.size());
        assertEquals(testFilm, results.get(0));
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void removeActorFromFilmsCache_ShouldClearActorFromFilms() {
        when(inMemoryCache.getAllValues()).thenReturn(List.of(testFilm));

        filmService.removeActorFromFilmsCache(1L);

        assertTrue(testFilm.getActors().isEmpty());
    }

    @Test
    void removeDirectorFromFilmsCache_ShouldClearDirectorFromFilms() {
        when(inMemoryCache.getAllValues()).thenReturn(List.of(testFilm));

        filmService.removeDirectorFromFilmsCache(1L);

        assertNull(testFilm.getDirector());
    }
}