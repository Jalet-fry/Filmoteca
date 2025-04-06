package org.example.service.implementation;

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
import org.example.exception.BulkOperation;
import org.example.exception.DirectorAlreadyExists;
import org.example.model.db.Director;
import org.example.repository.DirectorRepository;
import org.example.service.FilmService;
import org.example.service.InMemoryCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DirectorServiceImplTest {

    private DirectorRepository directorRepository;
    private FilmService filmService;
    private InMemoryCache<Long, Director> inMemoryCache;
    private DirectorServiceImpl directorService;

    @BeforeEach
    void setUp() {
        directorRepository = mock(DirectorRepository.class);
        filmService = mock(FilmService.class);
        inMemoryCache = mock(InMemoryCache.class);
        directorService = new DirectorServiceImpl(directorRepository, filmService, inMemoryCache);
    }

    @Test
    void create_shouldSaveDirectorAndCacheIt() {
        Director director = new Director();
        director.setId(1L);

        when(directorRepository.save(director)).thenReturn(director);

        directorService.create(director);

        verify(directorRepository).save(director);
        verify(inMemoryCache).put(director.getId(), director);
    }

    @Test
    void create_shouldThrowIfDirectorAlreadyExists() {
        Director director = new Director();
        director.setId(1L);

        when(directorRepository.save(director)).thenThrow(RuntimeException.class);

        assertThrows(DirectorAlreadyExists.class, () -> directorService.create(director));
    }

    @Test
    void createAll_shouldSaveAndCacheAllDirectors() {
        List<Director> directors = List.of(new Director(), new Director());
        when(directorRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        directorService.createAll(directors);

        verify(directorRepository).saveAll(directors);
        verify(inMemoryCache, times(2)).put(anyLong(), any());
    }

    @Test
    void createAll_shouldThrowBulkOperationException() {
        List<Director> directors = List.of(new Director(), new Director());
        when(directorRepository.saveAll(any())).thenThrow(RuntimeException.class);

        assertThrows(BulkOperation.class, () -> directorService.createAll(directors));
    }

    @Test
    void get_shouldReturnFromCacheIfExists() {
        Director director = new Director();
        director.setId(1L);
        when(inMemoryCache.get(1L)).thenReturn(Optional.of(director));

        Director result = directorService.get(1L);

        assertEquals(director, result);
        verify(directorRepository, never()).findById(anyLong());
    }

    @Test
    void get_shouldLoadFromDbAndCacheIfNotInCache() {
        Director director = new Director();
        director.setId(1L);
        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        Director result = directorService.get(1L);

        assertEquals(director, result);
        verify(inMemoryCache).put(1L, director);
    }

    @Test
    void getAll_shouldReturnAndCacheAll() {
        Director director1 = new Director(); director1.setId(1L);
        Director director2 = new Director(); director2.setId(2L);
        List<Director> all = List.of(director1, director2);
        when(directorRepository.findAll()).thenReturn(all);

        List<Director> result = directorService.getAll();

        assertEquals(all, result);
        verify(inMemoryCache, times(2)).put(anyLong(), any());
    }

    @Test
    void put_shouldUpdateAndSaveDirector() {
        Director input = new Director(); input.setId(1L);
        Director existing = mock(Director.class); when(existing.getId()).thenReturn(1L);

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(existing));

        directorService.put(input);

        verify(existing).updateForPut(input);
        verify(directorRepository).save(existing);
        verify(inMemoryCache).put(1L, existing);
    }

    @Test
    void patch_shouldUpdateAndSaveDirector() {
        Director input = new Director(); input.setId(1L);
        Director existing = mock(Director.class); when(existing.getId()).thenReturn(1L);

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(existing));

        directorService.patch(input);

        verify(existing).updateForPatch(input);
        verify(directorRepository).save(existing);
        verify(inMemoryCache).put(1L, existing);
    }

    @Test
    void delete_shouldThrowIfNotExists() {
        when(directorRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> directorService.delete(1L));
    }

    @Test
    void delete_shouldRemoveFromRepoCacheAndNotifyFilmService() {
        when(directorRepository.existsById(1L)).thenReturn(true);

        directorService.delete(1L);

        verify(directorRepository).deleteById(1L);
        verify(filmService).removeDirectorFromFilmsCache(1L);
        verify(inMemoryCache).del(1L);
    }

    @Test
    void getByName_shouldReturnNull() {
        assertNull(directorService.getByName("Any"));
    }
}
