package org.example.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.example.exception.ActorAlreadyExists;
import org.example.exception.BulkOperation;
import org.example.model.db.Actor;
import org.example.repository.ActorRepository;
import org.example.service.FilmService;
import org.example.service.InMemoryCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActorServiceImplTest {

    @Mock
    private ActorRepository actorRepository;
    @Mock
    private FilmService filmService;
    @Mock
    private InMemoryCache<Long, Actor> inMemoryCache;

    @InjectMocks
    private ActorServiceImpl actorService;

    private Actor testActor;

    @BeforeEach
    void setUp() {
        testActor = new Actor();
        testActor.setId(1L);
        testActor.setFirstName("John");
        testActor.setSecondName("Constantin");
        testActor.setLastName("Doe");
    }

    @Test
    void create_shouldSaveActorAndCacheIt() {
        when(actorRepository.save(testActor)).thenReturn(testActor);

        actorService.create(testActor);

        verify(actorRepository).save(testActor);
        verify(inMemoryCache).put(testActor.getId(), testActor);
    }

    @Test
    void create_shouldThrowIfActorAlreadyExists() {
        when(actorRepository.save(testActor)).thenThrow(RuntimeException.class);

        assertThrows(ActorAlreadyExists.class, () -> actorService.create(testActor));
    }

    @Test
    void createAll_shouldSaveAndCacheAllActors() {
        List<Actor> actors = List.of(testActor);
        when(actorRepository.saveAll(any())).thenReturn(actors);

        actorService.createAll(actors);

        verify(actorRepository).saveAll(actors);
        verify(inMemoryCache).put(testActor.getId(), testActor);
    }

    @Test
    void createAll_shouldThrowBulkOperationException() {
        List<Actor> actors = List.of(testActor);
        when(actorRepository.saveAll(any())).thenThrow(RuntimeException.class);

        assertThrows(BulkOperation.class, () -> actorService.createAll(actors));
    }

    @Test
    void get_shouldReturnFromCacheIfExists() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testActor));

        Actor result = actorService.get(1L);

        assertEquals(testActor, result);
        verify(actorRepository, never()).findById(anyLong());
    }

    @Test
    void get_shouldLoadFromDbAndCacheIfNotInCache() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(actorRepository.findById(1L)).thenReturn(Optional.of(testActor));

        Actor result = actorService.get(1L);

        assertEquals(testActor, result);
        verify(inMemoryCache).put(1L, testActor);
    }

    @Test
    void get_shouldThrowWhenActorNotFound() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(actorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> actorService.get(1L));
    }

    @Test
    void getAll_shouldReturnAndCacheAll() {
        List<Actor> actors = List.of(testActor);
        when(actorRepository.findAll()).thenReturn(actors);

        List<Actor> result = actorService.getAll();

        assertEquals(1, result.size());
        verify(inMemoryCache).put(testActor.getId(), testActor);
    }

    @Test
    void put_shouldUpdateAndSaveActor() {
        Actor update = new Actor();
        update.setId(1L);
        update.setFirstName("Updated");
        update.setLastName("Name");

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testActor));

        actorService.put(update);

        assertEquals("Updated", testActor.getFirstName());
        assertEquals("Name", testActor.getLastName());
        verify(actorRepository).save(testActor);
        verify(inMemoryCache).put(1L, testActor);
    }

    @Test
    void patch_shouldPartialUpdateAndSaveActor() {
        Actor partialUpdate = new Actor();
        partialUpdate.setId(1L);
        partialUpdate.setFirstName("It");
        partialUpdate.setSecondName("Was");

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testActor));

        actorService.patch(partialUpdate);

        assertEquals("It", testActor.getFirstName());
        assertEquals("Was", testActor.getSecondName());
        verify(actorRepository).save(testActor);
        verify(inMemoryCache).put(1L, testActor);
    }

    @Test
    void delete_shouldThrowIfNotExists() {
        when(actorRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> actorService.delete(1L));
    }

    @Test
    void delete_shouldRemoveFromRepoCacheAndNotifyFilmService() {
        when(actorRepository.existsById(1L)).thenReturn(true);

        actorService.delete(1L);

        verify(actorRepository).deleteById(1L);
        verify(filmService).removeActorFromFilmsCache(1L);
        verify(inMemoryCache).del(1L);
    }

    @Test
    void getByName_shouldReturnActor() {
        when(actorRepository.getByFirstNameAndSecondNameAndLastName("John", "", "Doe"))
                .thenReturn(Optional.of(testActor));

        Actor result = actorService.getByName("John", "", "Doe");

        assertEquals(testActor, result);
    }

    @Test
    void getByName_shouldThrowWhenNotFound() {
        when(actorRepository.getByFirstNameAndSecondNameAndLastName("John", "", "Doe"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> actorService.getByName("John", "", "Doe"));
    }
}