package org.example.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class DirectorServiceImplTest {

    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private FilmService filmService;
    @Mock
    private InMemoryCache<Long, Director> inMemoryCache;

    @InjectMocks
    private DirectorServiceImpl directorService;

    private Director testDirector;

    @BeforeEach
    void setUp() {
        testDirector = new Director();
        testDirector.setId(1L);
        testDirector.setFirstName("Christopher");
        testDirector.setLastName("Nolan");
    }

    @Test
    void create_ShouldSaveAndCacheDirector() {
        when(directorRepository.save(testDirector)).thenReturn(testDirector);

        directorService.create(testDirector);

        verify(directorRepository).save(testDirector);
        verify(inMemoryCache).put(testDirector.getId(), testDirector);
    }

    @Test
    void create_ShouldThrowWhenDirectorAlreadyExists() {
        when(directorRepository.save(testDirector)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DirectorAlreadyExists.class, () -> directorService.create(testDirector));
    }

    @Test
    void createAll_ShouldSaveAndCacheAllDirectors() {
        List<Director> directors = List.of(testDirector);
        when(directorRepository.saveAll(any())).thenReturn(directors);

        directorService.createAll(directors);

        verify(directorRepository).saveAll(directors);
        verify(inMemoryCache).put(testDirector.getId(), testDirector);
    }

    @Test
    void createAll_ShouldThrowBulkOperationException() {
        when(directorRepository.saveAll(any())).thenThrow(RuntimeException.class);

        assertThrows(BulkOperation.class, () -> directorService.createAll(List.of(testDirector)));
    }

    @Test
    void get_ShouldReturnFromCache() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testDirector));

        Director result = directorService.get(1L);

        assertEquals(testDirector, result);
        verifyNoInteractions(directorRepository);
    }

    @Test
    void get_ShouldFetchFromDbAndCacheWhenNotInCache() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(directorRepository.findById(1L)).thenReturn(Optional.of(testDirector));

        Director result = directorService.get(1L);

        assertEquals(testDirector, result);
        verify(inMemoryCache).put(1L, testDirector);
    }

    @Test
    void get_ShouldThrowWhenDirectorNotFound() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.empty());
        when(directorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> directorService.get(1L));
    }

    @Test
    void getAll_ShouldReturnAndCacheAllDirectors() {
        List<Director> directors = List.of(testDirector);
        when(directorRepository.findAll()).thenReturn(directors);

        List<Director> result = directorService.getAll();

        assertEquals(1, result.size());
        verify(inMemoryCache).put(testDirector.getId(), testDirector);
    }

    @Test
    void put_ShouldUpdateAndSaveDirector() {
        Director update = new Director();
        update.setId(1L);
        update.setFirstName("Updated");

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testDirector));
        when(directorRepository.save(testDirector)).thenReturn(testDirector);

        directorService.put(update);

        assertEquals("Updated", testDirector.getFirstName());
        verify(inMemoryCache).put(1L, testDirector);
    }

    @Test
    void patch_ShouldPartialUpdateAndSaveDirector() {
        Director partialUpdate = new Director();
        partialUpdate.setId(1L);
        partialUpdate.setFirstName("Updated");

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testDirector));
        when(directorRepository.save(testDirector)).thenReturn(testDirector);

        directorService.patch(partialUpdate);

        assertEquals("Updated", testDirector.getFirstName());
        assertEquals("Nolan", testDirector.getLastName());
        verify(inMemoryCache).put(1L, testDirector);
    }

    @Test
    void delete_ShouldRemoveDirector() {
        when(directorRepository.existsById(1L)).thenReturn(true);

        directorService.delete(1L);

        verify(directorRepository).deleteById(1L);
        verify(filmService).removeDirectorFromFilmsCache(1L);
        verify(inMemoryCache).del(1L);
    }

    @Test
    void delete_ShouldThrowWhenDirectorNotFound() {
        when(directorRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> directorService.delete(1L));
    }

    @Test
    void getByName_ShouldReturnNull() {
        assertNull(directorService.getByName("Christopher"));
    }
}