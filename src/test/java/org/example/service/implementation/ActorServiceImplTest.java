package org.example.service.implementation;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoInteractions;
//import static org.mockito.Mockito.when;
//
//import jakarta.persistence.EntityNotFoundException;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import org.example.annotations.CacheBean;
//import org.example.exception.ActorAlreadyExists;
//import org.example.exception.BulkOperation;
//import org.example.model.db.Actor;
//import org.example.repository.ActorRepository;
//import org.example.service.FilmService;
//import org.example.service.InMemoryCache;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class ActorServiceImplTest {
//
//    @Mock
//    private ActorRepository actorRepository;
//
//    @Mock
//    private FilmService filmService;
//
//    @Mock
//    @CacheBean("actors")
//    private InMemoryCache<Long, Actor> inMemoryCache;
//
//    @InjectMocks
//    private ActorServiceImpl actorService;
//
//    private Actor testActor;
//
//    @BeforeEach
//    void setUp() {
//        testActor = new Actor();
//        testActor.setId(1L);
//        testActor.setFirstName("John");
//        testActor.setLastName("Doe");
//    }
//
//    @Test
//    void create_ShouldSaveActorAndPutToCache_WhenActorIsNew() {
//        when(actorRepository.save(any(Actor.class))).thenReturn(testActor);
//
//        actorService.create(testActor);
//
//        verify(actorRepository).save(testActor);
//        verify(inMemoryCache).put(testActor.getId(), testActor);
//    }
//
//    @Test
//    void create_ShouldThrowActorAlreadyExists_WhenActorExists() {
//        when(actorRepository.save(any(Actor.class))).thenThrow(new RuntimeException());
//
//        assertThrows(ActorAlreadyExists.class, () -> actorService.create(testActor));
//    }
//
//    @Test
//    void createAll_ShouldSaveAllActorsAndPutToCache() {
//        List<Actor> actors = List.of(testActor);
//        when(actorRepository.saveAll(any())).thenReturn(actors);
//
//        actorService.createAll(actors);
//
//        verify(actorRepository).saveAll(actors);
//        verify(inMemoryCache).put(testActor.getId(), testActor);
//    }
//
//    @Test
//    void createAll_ShouldThrowBulkOperation_WhenErrorOccurs() {
//        List<Actor> actors = List.of(testActor);
//        when(actorRepository.saveAll(any())).thenThrow(new RuntimeException());
//
//        assertThrows(BulkOperation.class, () -> actorService.createAll(actors));
//    }
//
//    @Test
//    void get_ShouldReturnFromCache_WhenActorIsCached() {
//        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testActor));
//
//        Actor result = actorService.get(1L);
//
//        assertEquals(testActor, result);
//        verify(inMemoryCache).get(1L);
//        verifyNoInteractions(actorRepository);
//    }
//
//    @Test
//    void get_ShouldFetchFromDbAndCache_WhenActorNotInCache() {
//        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
//        when(actorRepository.findById(1L)).thenReturn(Optional.of(testActor));
//
//        Actor result = actorService.get(1L);
//
//        assertEquals(testActor, result);
//        verify(inMemoryCache).put(1L, testActor);
//    }
//
//    @Test
//    void delete_ShouldDeleteActorAndRemoveFromCache() {
//        when(actorRepository.existsById(1L)).thenReturn(true);
//
//        actorService.delete(1L);
//
//        verify(actorRepository).deleteById(1L);
//        verify(inMemoryCache).del(1L);
//        verify(filmService).removeActorFromFilmsCache(1L);
//    }
//
//    @Test
//    void put_ShouldUpdateExistingActor() {
//        Actor updatedActor = new Actor();
//        updatedActor.setId(1L);
//        updatedActor.setFirstName("Updated");
//
//        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testActor));
//
//        actorService.put(updatedActor);
//
//        assertEquals("Updated", testActor.getFirstName());
//        verify(actorRepository).save(testActor);
//        verify(inMemoryCache).put(1L, testActor);
//    }
//
//    @Test
//    void getAll_ShouldReturnAllActorsAndCacheThem() {
//        List<Actor> actors = List.of(testActor);
//        when(actorRepository.findAll()).thenReturn(actors);
//
//        List<Actor> result = actorService.getAll();
//
//        assertEquals(1, result.size());
//        assertEquals(testActor, result.get(0));
//        verify(inMemoryCache).put(testActor.getId(), testActor);
//    }
//
//    @Test
//    void getAll_ShouldHandleEmptyList() {
//        when(actorRepository.findAll()).thenReturn(Collections.emptyList());
//
//        List<Actor> result = actorService.getAll();
//
//        assertTrue(result.isEmpty());
//        verify(inMemoryCache, never()).put(any(), any());
//    }
//
//    @Test
//    void patch_ShouldUpdateOnlyProvidedFields() {
//        Actor partialUpdate = new Actor();
//        partialUpdate.setId(1L);
//        partialUpdate.setFirstName("UpdatedName");
//        // lastName не установлен - должен остаться прежним
//
//        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testActor));
//
//        actorService.patch(partialUpdate);
//
//        assertEquals("UpdatedName", testActor.getFirstName());
//        assertEquals("Doe", testActor.getLastName()); // Проверяем, что не изменилось
//        verify(actorRepository).save(testActor);
//        verify(inMemoryCache).put(1L, testActor);
//    }
//
//    @Test
//    void patch_ShouldThrowException_WhenActorNotFound() {
//        Actor partialUpdate = new Actor();
//        partialUpdate.setId(999L);
//
//        when(inMemoryCache.get(999L)).thenReturn(Optional.empty());
//        when(actorRepository.findById(999L)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> actorService.patch(partialUpdate));
//    }
//}

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

class ActorServiceImplTest {

    private ActorRepository actorRepository;
    private FilmService filmService;
    private InMemoryCache<Long, Actor> inMemoryCache;
    private ActorServiceImpl actorService;

    @BeforeEach
    void setUp() {
        actorRepository = mock(ActorRepository.class);
        filmService = mock(FilmService.class);
        inMemoryCache = mock(InMemoryCache.class);
        actorService = new ActorServiceImpl(actorRepository, filmService, inMemoryCache);
    }

    @Test
    void create_shouldSaveActorAndCacheIt() {
        Actor actor = new Actor();
        actor.setId(1L);

        when(actorRepository.save(actor)).thenReturn(actor);

        actorService.create(actor);

        verify(actorRepository).save(actor);
        verify(inMemoryCache).put(actor.getId(), actor);
    }

    @Test
    void create_shouldThrowIfActorAlreadyExists() {
        Actor actor = new Actor();
        actor.setId(1L);

        when(actorRepository.save(actor)).thenThrow(RuntimeException.class);

        assertThrows(ActorAlreadyExists.class, () -> actorService.create(actor));
    }

    @Test
    void createAll_shouldSaveAndCacheAllActors() {
        List<Actor> actors = List.of(new Actor(), new Actor());
        when(actorRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        actorService.createAll(actors);

        verify(actorRepository).saveAll(actors);
        verify(inMemoryCache, times(2)).put(anyLong(), any());
    }

    @Test
    void createAll_shouldThrowBulkOperationException() {
        List<Actor> actors = List.of(new Actor(), new Actor());
        when(actorRepository.saveAll(any())).thenThrow(RuntimeException.class);

        assertThrows(BulkOperation.class, () -> actorService.createAll(actors));
    }

    @Test
    void get_shouldReturnFromCacheIfExists() {
        Actor actor = new Actor();
        actor.setId(1L);
        when(inMemoryCache.get(1L)).thenReturn(Optional.of(actor));

        Actor result = actorService.get(1L);

        assertEquals(actor, result);
        verify(actorRepository, never()).findById(anyLong());
    }

    @Test
    void get_shouldLoadFromDbAndCacheIfNotInCache() {
        Actor actor = new Actor();
        actor.setId(1L);
        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor));

        Actor result = actorService.get(1L);

        assertEquals(actor, result);
        verify(inMemoryCache).put(1L, actor);
    }

    @Test
    void getAll_shouldReturnAndCacheAll() {
        Actor actor1 = new Actor(); actor1.setId(1L);
        Actor actor2 = new Actor(); actor2.setId(2L);
        List<Actor> all = List.of(actor1, actor2);
        when(actorRepository.findAll()).thenReturn(all);

        List<Actor> result = actorService.getAll();

        assertEquals(all, result);
        verify(inMemoryCache, times(2)).put(anyLong(), any());
    }

    @Test
    void put_shouldUpdateAndSaveActor() {
        Actor input = new Actor(); input.setId(1L);
        Actor existing = mock(Actor.class); when(existing.getId()).thenReturn(1L);

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(existing));

        actorService.put(input);

        verify(existing).updateForPut(input);
        verify(actorRepository).save(existing);
        verify(inMemoryCache).put(1L, existing);
    }

    @Test
    void patch_shouldUpdateAndSaveActor() {
        Actor input = new Actor(); input.setId(1L);
        Actor existing = mock(Actor.class); when(existing.getId()).thenReturn(1L);

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(existing));

        actorService.patch(input);

        verify(existing).updateForPatch(input);
        verify(actorRepository).save(existing);
        verify(inMemoryCache).put(1L, existing);
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
    void getByName_shouldReturnNull() {
        assertNull(actorService.getByName("Any"));
    }
}