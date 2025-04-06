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
        testFilm = new Film();
        testFilm.setId(1L);
        testFilm.setTitle("Inception");
        testFilm.setYear(2010);

        testActor = new Actor();
        testActor.setId(1L);
        testActor.setFirstName("Leonardo");
        testActor.setLastName("DiCaprio");

        testDirector = new Director();
        testDirector.setId(1L);
        testDirector.setFirstName("Christopher");
        testDirector.setLastName("Nolan");
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
    void get_ShouldFetchFromDbAndCache_WhenNotInCache() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(filmRepository.findById(1L)).thenReturn(Optional.of(testFilm));

        Film result = filmService.get(1L);

        assertEquals(testFilm, result);
        verify(inMemoryCache).put(1L, testFilm);
    }

    @Test
    void get_ShouldThrowException_WhenFilmNotFound() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(filmRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> filmService.get(1L));
    }

    @Test
    void create_ShouldSaveFilm_WhenSuccessful() {
        testFilm.setActors(Collections.singletonList(testActor));
        testFilm.setDirector(testDirector);

        when(filmRepository.existsByTitleAndYearAndDirector(anyString(), anyInt(), any()))
                .thenReturn(false);
        when(actorRepository.getByFirstNameAndSecondNameAndLastName(any(), any(), any()))
                .thenReturn(Optional.of(testActor));
        when(directorRepository.getByFirstNameAndSecondNameAndLastName(any(), any(), any()))
                .thenReturn(Optional.of(testDirector));
        when(filmRepository.save(any(Film.class))).thenReturn(testFilm);

        filmService.create(testFilm);

        verify(filmRepository).save(testFilm);
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void create_ShouldThrowFilmAlreadyExists_WhenDuplicate() {
        when(filmRepository.existsByTitleAndYearAndDirector(anyString(), anyInt(), any()))
                .thenReturn(true);

        assertThrows(FilmAlreadyExists.class, () -> filmService.create(testFilm));
    }

    @Test
    void createAll_ShouldSaveAllFilmsAndCacheThem() {
        List<Film> films = List.of(testFilm);
        when(filmRepository.saveAll(any())).thenReturn(films);

        filmService.createAll(films);

        verify(filmRepository).saveAll(films);
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void createAll_ShouldThrowBulkOperation_WhenErrorOccurs() {
        List<Film> films = List.of(testFilm);
        when(filmRepository.saveAll(any())).thenThrow(new RuntimeException());

        assertThrows(BulkOperation.class, () -> filmService.createAll(films));
    }

    @Test
    void delete_ShouldRemoveFilm_WhenExists() {
        when(filmRepository.existsById(1L)).thenReturn(true);

        filmService.delete(1L);

        verify(filmRepository).deleteById(1L);
        verify(inMemoryCache).del(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenFilmNotExists() {
        when(filmRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> filmService.delete(1L));
    }

    @Test
    void put_ShouldUpdateFilm() {
        Film updatedFilm = new Film();
        updatedFilm.setId(1L);
        updatedFilm.setTitle("Updated Title");

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testFilm));
        when(filmRepository.save(any())).thenReturn(testFilm);

        filmService.put(updatedFilm);

        assertEquals("Updated Title", testFilm.getTitle());
        verify(filmRepository).save(testFilm);
        verify(inMemoryCache).put(1L, testFilm);
    }

    @Test
    void patch_ShouldUpdateOnlyProvidedFields() {
        Film partialUpdate = new Film();
        partialUpdate.setId(1L);
        partialUpdate.setTitle("Updated Title");

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testFilm));
        when(filmRepository.save(any())).thenReturn(testFilm);

        filmService.patch(partialUpdate);

        assertEquals("Updated Title", testFilm.getTitle());
        assertEquals(2010, testFilm.getYear());
        verify(filmRepository).save(testFilm);
    }

    @Test
    void getAll_ShouldReturnAllFilmsAndCacheThem() {
        List<Film> films = List.of(testFilm);
        when(filmRepository.findAll()).thenReturn(films);

        List<Film> result = filmService.getAll();

        assertEquals(1, result.size());
        assertEquals(testFilm, result.get(0));
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void getByTitle_ShouldReturnFilms() {
        List<Film> films = List.of(testFilm);
        when(filmRepository.findByTitle("Inception")).thenReturn(films);

        List<Film> result = filmService.getByTitle("Inception");

        assertEquals(1, result.size());
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void getByDirector_ShouldReturnFilms() {
        List<Film> films = List.of(testFilm);
        when(filmRepository.findByDirector("Nolan")).thenReturn(films);

        List<Film> result = filmService.getByDirector("Nolan");

        assertEquals(1, result.size());
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void getByActor_ShouldReturnFilms() {
        List<Film> films = List.of(testFilm);
        when(filmRepository.findByActor("DiCaprio")).thenReturn(films);

        List<Film> result = filmService.getByActor("DiCaprio");

        assertEquals(1, result.size());
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void findByActorAndDirector_ShouldReturnFilms() {
        List<Film> films = List.of(testFilm);
        when(filmRepository.findByActorAndDirector("DiCaprio", "Nolan")).thenReturn(films);

        List<Film> result = filmService.findByActorAndDirector("DiCaprio", "Nolan");

        assertEquals(1, result.size());
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void removeActorFromFilmsCache_ShouldRemoveActor() {
        testFilm.setActors(new ArrayList<>(List.of(testActor)));
        when(inMemoryCache.getAllValues()).thenReturn(List.of(testFilm));

        filmService.removeActorFromFilmsCache(1L);

        assertTrue(testFilm.getActors().isEmpty());
    }

    @Test
    void removeDirectorFromFilmsCache_ShouldRemoveDirector() {
        testFilm.setDirector(testDirector);
        when(inMemoryCache.getAllValues()).thenReturn(List.of(testFilm));

        filmService.removeDirectorFromFilmsCache(1L);

        assertNull(testFilm.getDirector());
    }
}