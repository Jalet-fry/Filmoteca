package org.example.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
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
}