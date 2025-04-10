package org.example.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
    private Film testFilmSecond;
    private Actor testActor;
    private Actor testActorSecond;
    private Director testDirector;
    private Director testDirectorSecond;

    @BeforeEach
    void setUp() {
        testDirector = new Director();
        testDirector.setId(1L);
        testDirector.setFirstName("Christopher");
        testDirector.setLastName("Nolan");

        testDirectorSecond = new Director();
        testDirectorSecond.setId(2L);
        testDirectorSecond.setFirstName("Martin");
        testDirectorSecond.setLastName("Scorcese");

        testActor = new Actor();
        testActor.setId(1L);
        testActor.setFirstName("Leonardo");
        testActor.setLastName("DiCaprio");

        testActorSecond = new Actor();
        testActorSecond.setId(2L);
        testActorSecond.setFirstName("Charlie");
        testActorSecond.setLastName("Chuplin");

        testFilm = new Film();
        testFilm.setId(1L);
        testFilm.setTitle("Inception");
        testFilm.setYear(2010);
        testFilm.setDirector(testDirector);
        testFilm.setActors(List.of(testActor));

        testFilmSecond = new Film();
        testFilmSecond.setId(2L);
        testFilmSecond.setTitle("Inception");
        testFilmSecond.setYear(2010);
        testFilmSecond.setDirector(testDirector);
        testFilmSecond.setActors(List.of(testActor, testActorSecond));
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

    @Test
    void getAll_ShouldReturnAndCacheAllFilms() {
        List<Film> films = List.of(testFilm, testFilmSecond);
        when(filmRepository.findAll()).thenReturn(films);

        List<Film> result = filmService.getAll();

        assertEquals(2, result.size());
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
        verify(inMemoryCache).put(testFilmSecond.getId(), testFilmSecond);
    }

    @Test
    void getByDirector_ShouldReturnAndCacheFilms() {
        when(filmRepository.findByDirector("Nolan")).thenReturn(List.of(testFilm));

        List<Film> result = filmService.getByDirector("Nolan");

        assertEquals(1, result.size());
        assertEquals(testFilm, result.get(0));
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void getByDirector_ShouldReturnEmptyListWhenNoFilmsFound() {
        when(filmRepository.findByDirector("Unknown")).thenReturn(List.of());

        List<Film> result = filmService.getByDirector("Unknown");

        assertTrue(result.isEmpty());
        verifyNoInteractions(inMemoryCache);
    }

    @Test
    void getByActor_ShouldReturnAndCacheFilms() {
        when(filmRepository.findByActor("DiCaprio")).thenReturn(List.of(testFilm));

        List<Film> result = filmService.getByActor("DiCaprio");

        assertEquals(1, result.size());
        assertEquals(testFilm, result.get(0));
        verify(inMemoryCache).put(testFilm.getId(), testFilm);
    }

    @Test
    void getByActor_ShouldReturnEmptyListWhenNoFilmsFound() {
        when(filmRepository.findByActor("Unknown")).thenReturn(List.of());

        List<Film> result = filmService.getByActor("Unknown");

        assertTrue(result.isEmpty());
        verifyNoInteractions(inMemoryCache);
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
    void put_WasInCache() {
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
    void put_NotWasInCache() {
        Film update = new Film();
        update.setId(1L);
        update.setTitle("Updated Title");
        update.setYear(2020);

        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(filmRepository.findById(1L)).thenReturn(Optional.of(testFilm));
        when(filmRepository.save(testFilm)).thenReturn(testFilm);

        filmService.put(update);

        assertEquals("Updated Title", testFilm.getTitle());
        assertEquals(2020, testFilm.getYear());
        verify(inMemoryCache).put(1L, testFilm);
    }

    private void invokePutDirector(Film existed, Film film) throws Exception {
        Method method = FilmServiceImpl.class.getDeclaredMethod("putDirector", Film.class, Film.class);
        method.setAccessible(true);
        method.invoke(filmService, existed, film);
    }

    private void invokePatchDirector(Film existed, Film film) throws Exception {
        Method method = FilmServiceImpl.class.getDeclaredMethod("patchDirector", Film.class, Film.class);
        method.setAccessible(true);
        method.invoke(filmService, existed, film);
    }

    private void invokePutActors(Film existed, Film film) throws Exception {
        Method method = FilmServiceImpl.class.getDeclaredMethod("putActors", Film.class, Film.class);
        method.setAccessible(true);
        method.invoke(filmService, existed, film);
    }

    private void invokePatchActors(Film existed, Film film) throws Exception {
        Method method = FilmServiceImpl.class.getDeclaredMethod("patchActors", Film.class, Film.class);
        method.setAccessible(true);
        method.invoke(filmService, existed, film);
    }


    private static Stream<Arguments> directorNameVariations() {
        final String N1 = "N1";
        final String N2 = "N2";
        final String N3 = "N3";
        return Stream.of(
                // new is null
                Arguments.of(0, null, null),
                // old is null, new has id
                Arguments.of(1, Director.builder().id(1L).build(),
                        null),
                Arguments.of(2, Director.builder().id(1L).build(),
                        Director.builder().id(2L).build()),
                // old is null, new hasn't id
                Arguments.of(3, Director.builder().build(),
                        null),
                // both same
                Arguments.of(4,
                        Director.builder().firstName(N1).secondName(N2).lastName(N3).build(),
                        Director.builder().id(2L).firstName(N1).secondName(N2).lastName(N3).build()),
                // first name differ
                Arguments.of(5,
                        Director.builder().firstName(N1).secondName(N2).lastName(N3).build(),
                        Director.builder().id(2L).firstName(N2).secondName(N2).lastName(N2).build()),
                // second name differ
                Arguments.of(6,
                        Director.builder().firstName(N1).secondName(N2).lastName(N3).build(),
                        Director.builder().id(2L).firstName(N1).secondName(N3).lastName(N2).build()),
                // last name differ
                Arguments.of(7,
                        Director.builder().firstName(N1).secondName(N2).lastName(N3).build(),
                        Director.builder().id(2L).firstName(N1).secondName(N2).lastName(N2).build()),
                Arguments.of(8, Director.builder().id(2L).build(),
                        Director.builder().id(2L).build())
        );
    }
    @ParameterizedTest
    @MethodSource("directorNameVariations")
    void putDirector(
            Integer numberOfTest, Director newDirector,
            Director oldDirector) throws Exception {
        // Setup test film with old director
        testFilm.setDirector(oldDirector);
        Film update = new Film();
        update.setDirector(newDirector);
        if (numberOfTest == 1 || numberOfTest == 2) {
            when(directorRepository.findById(newDirector.getId()))
                    .thenReturn(Optional.of(newDirector));
        }
        invokePutDirector(testFilm, update);
        if (numberOfTest == 0) {
            assertNull(testFilm.getDirector());
        } else {
            assertNotNull(testFilm.getDirector());
        }
    }


    @ParameterizedTest
    @MethodSource("directorNameVariations")
    void patchDirector(
            Integer numberOfTest, Director newDirector,
            Director oldDirector) throws Exception {
        // Setup test film with old director
        testFilm.setDirector(oldDirector);
        Film update = new Film();
        update.setDirector(newDirector);
        if (numberOfTest == 1 || numberOfTest == 2) {
            when(directorRepository.findById(newDirector.getId()))
                    .thenReturn(Optional.of(newDirector));
        }
        invokePatchDirector(testFilm, update);
        if (numberOfTest == 0) {
            assertNull(testFilm.getDirector());
        } else {
            assertNotNull(testFilm.getDirector());
        }
    }

    @Test
    void putActors_ShouldUpdateExistingActorById() throws Exception {
        Actor updatedActor = new Actor();
        updatedActor.setId(1L);
        updatedActor.setFirstName("Updated");
        updatedActor.setLastName("Name");

        Film update = new Film();
        update.setActors(List.of(updatedActor));

        invokePutActors(testFilm, update);

        assertEquals("Updated", testFilm.getActors().get(0).getFirstName());
        assertEquals("Name", testFilm.getActors().get(0).getLastName());
    }

    @Test
    void putActors_ShouldAddNewActorByName() throws Exception {
        Actor newActor = new Actor();
        newActor.setId(0L);
        newActor.setFirstName("New");
        newActor.setLastName("Actor");

        Actor existingActor = new Actor();
        existingActor.setId(2L);
        existingActor.setFirstName("New");
        existingActor.setLastName("Actor");

        Film update = new Film();
        update.setActors(List.of(newActor));

        when(actorRepository.getByFirstNameAndSecondNameAndLastName(
                "New", null, "Actor"))
                .thenReturn(Optional.of(existingActor));

        invokePutActors(testFilm, update);

        assertEquals(2L, testFilm.getActors().get(0).getId());
        assertEquals("New", testFilm.getActors().get(0).getFirstName());
    }

    @Test
    void patchActors_ShouldPartiallyUpdateExistingActor() throws Exception {
        Actor partialUpdate = new Actor();
        partialUpdate.setId(1L);
        partialUpdate.setFirstName("Updated");
        // lastName not set - should keep old value

        Film update = new Film();
        update.setActors(List.of(partialUpdate));

        invokePatchActors(testFilm, update);

        assertEquals("Updated", testFilm.getActors().get(0).getFirstName());
        assertEquals("DiCaprio", testFilm.getActors().get(0).getLastName());
    }

    @Test
    void patchActors_ShouldAddNewActorByName() throws Exception {
        Actor newActor = new Actor();
        newActor.setId(0L);
        newActor.setFirstName("New");
        newActor.setSecondName("Sparky");
        newActor.setLastName("Actor");
        Film update = new Film();
        update.setActors(List.of(newActor));
        invokePatchActors(testFilm, update);

        assertEquals(0L, testFilm.getActors().get(0).getId());
        assertEquals("New", testFilm.getActors().get(0).getFirstName());
    }


    @Test
    void putActors_ShouldSetNullWhenActorsIsNull() throws Exception {
        Film update = new Film();
        update.setActors(null);

        invokePutActors(testFilm, update);

        assertNull(testFilm.getActors());
    }

    @Test
    void patchActors_ShouldUpdateExistingActor() throws Exception {
        Actor updatedActor = new Actor();
        updatedActor.setId(1L);
        updatedActor.setFirstName("Updated");

        Film update = new Film();
        update.setActors(List.of(updatedActor));

        invokePatchActors(testFilm, update);

        assertEquals("Updated", testFilm.getActors().get(0).getFirstName());
        assertEquals("DiCaprio", testFilm.getActors().get(0).getLastName());
    }

    @Test
    void patch_WasInCache() {
        Film partialUpdate = new Film();
        partialUpdate.setId(1L);
        partialUpdate.setTitle("Updated Title");
        partialUpdate.setLink("Updated Title");
        partialUpdate.setYear(1984);

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testFilm));
        when(filmRepository.save(testFilm)).thenReturn(testFilm);

        filmService.patch(partialUpdate);

        assertEquals("Updated Title", testFilm.getTitle());
        assertEquals(1984, testFilm.getYear());
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
}