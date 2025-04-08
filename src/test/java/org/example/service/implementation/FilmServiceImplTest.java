package org.example.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.exception.BulkOperation;
import org.example.exception.FilmAlreadyExists;
import org.example.model.db.Actor;
import org.example.model.db.Director;
import org.example.model.db.Film;
import org.example.repository.ActorRepository;
import org.example.repository.DirectorRepository;
import org.example.repository.FilmRepository;
import org.example.service.InMemoryCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {

    @Mock
    private ActorRepository actorRepository;
    @Mock
    private FilmRepository filmRepository;
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
        testFilm.setDirector(testDirector);
        testFilm.setActors(List.of(testActor));
    }

    @Test
    void get_ShouldReturnFromCache() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testFilm));

        Film result = filmService.get(1L);

        assertEquals(testFilm, result);
        verifyNoInteractions(filmRepository);
    }

    @Test
    void get_ShouldFetchFromDbAndCacheWhenNotInCache() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(filmRepository.findById(1L)).thenReturn(Optional.of(testFilm));

        Film result = filmService.get(1L);

        assertEquals(testFilm, result);
        verify(inMemoryCache).put(1L, testFilm);
    }

    @Test
    void get_ShouldThrowWhenFilmNotFound() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(filmRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> filmService.get(1L));
    }

    @Test
    void getAll_ShouldReturnAndCacheAllFilms() {
        List<Film> films = List.of(testFilm);
        when(filmRepository.findAll()).thenReturn(films);

        List<Film> result = filmService.getAll();

        assertEquals(1, result.size());
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void create_ShouldSaveAndCacheFilm() {
        when(filmRepository.save(testFilm)).thenReturn(testFilm);

        filmService.create(testFilm);

        verify(filmRepository).save(testFilm);
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void create_ShouldThrowWhenFilmAlreadyExists() {
        when(filmRepository.save(testFilm)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(FilmAlreadyExists.class, () -> filmService.create(testFilm));
    }

    @Test
    void createAll_ShouldSaveAndCacheAllFilms() {
        List<Film> films = List.of(testFilm);
        when(filmRepository.saveAll(any())).thenReturn(films);

        filmService.createAll(films);

        verify(filmRepository).saveAll(films);
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void createAll_ShouldThrowBulkOperationException() {
        when(filmRepository.saveAll(any())).thenThrow(RuntimeException.class);

        assertThrows(BulkOperation.class, () -> filmService.createAll(List.of(testFilm)));
    }

    @Test
    void put_ShouldUpdateFilm() {
        Film update = new Film();
        update.setId(1L);
        update.setTitle("Updated Title");
        update.setYear(2020);

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testFilm));
        when(filmRepository.save(testFilm)).thenReturn(testFilm);

        filmService.put(update);

        assertEquals("Updated Title", testFilm.getTitle());
        assertEquals(2020, testFilm.getYear());
        verify(inMemoryCache).put(1L, testFilm);
    }

    @Test
    void patch_ShouldPartialUpdateFilm() {
        Film partialUpdate = new Film();
        partialUpdate.setId(1L);
        partialUpdate.setTitle("Updated Title");

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testFilm));
        when(filmRepository.save(testFilm)).thenReturn(testFilm);

        filmService.patch(partialUpdate);

        assertEquals("Updated Title", testFilm.getTitle());
        assertEquals(2010, testFilm.getYear());
        verify(inMemoryCache).put(1L, testFilm);
    }

    @Test
    void delete_ShouldRemoveFilm() {
        when(filmRepository.existsById(1L)).thenReturn(true);

        filmService.delete(1L);

        verify(filmRepository).deleteById(1L);
        verify(inMemoryCache).del(1L);
    }

    @Test
    void delete_ShouldThrowWhenFilmNotFound() {
        when(filmRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> filmService.delete(1L));
    }

    @Test
    void removeActorFromFilmsCache_ShouldClearActorFromFilms() {
        Film mutableFilm = new Film();
        mutableFilm.setId(testFilm.getId());
        mutableFilm.setTitle(testFilm.getTitle());
        mutableFilm.setYear(testFilm.getYear());
        mutableFilm.setDirector(testFilm.getDirector());
        mutableFilm.setActors(new ArrayList<>(testFilm.getActors()));

        // 2. Мокируем возврат изменяемого списка
        List<Film> films = new ArrayList<>();
        films.add(mutableFilm);
        when(inMemoryCache.getAllValues()).thenReturn(films);

        filmService.removeActorFromFilmsCache(1L);

        assertTrue(mutableFilm.getActors().isEmpty());
    }

    @Test
    void removeDirectorFromFilmsCache_ShouldClearDirectorFromFilms() {
        when(inMemoryCache.getAllValues()).thenReturn(List.of(testFilm));

        filmService.removeDirectorFromFilmsCache(1L);

        assertNull(testFilm.getDirector());
    }

    @Test
    void getByTitle_ShouldReturnFilms() {
        when(filmRepository.findByTitle("Inception")).thenReturn(List.of(testFilm));

        List<Film> result = filmService.getByTitle("Inception");

        assertEquals(1, result.size());
        assertEquals(testFilm, result.get(0));
    }

    @Test
    void findByActorAndDirector_ShouldReturnFilms() {
        when(filmRepository.findByActorAndDirector("DiCaprio", "Nolan"))
                .thenReturn(List.of(testFilm));

        List<Film> result = filmService.findByActorAndDirector("DiCaprio", "Nolan");

        assertEquals(1, result.size());
        assertEquals(testFilm, result.get(0));
    }
}