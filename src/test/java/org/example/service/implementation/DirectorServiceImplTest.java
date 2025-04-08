package org.example.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
    void create_ShouldSaveDirector() {
        when(directorRepository.save(testDirector)).thenReturn(testDirector);

        directorService.create(testDirector);

        verify(directorRepository).save(testDirector);
        verify(inMemoryCache).put(testDirector.getId(), testDirector);
    }

    @Test
    void get_ShouldReturnDirectorFromCache() {
        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testDirector));

        Director result = directorService.get(1L);

        assertEquals(testDirector, result);
        verifyNoInteractions(directorRepository);
    }

    @Test
    void updateForPut_ShouldUpdateAllNameFields() {
        Director update = new Director();
        update.setId(1L);
        update.setFirstName("New");
        update.setSecondName("Middle");
        update.setLastName("Name");

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testDirector));
        when(directorRepository.save(any(Director.class))).thenReturn(testDirector);

        directorService.put(update);

        assertEquals("New", testDirector.getFirstName());
        assertEquals("Middle", testDirector.getSecondName());
        assertEquals("Name", testDirector.getLastName());
    }

    @Test
    void updateForPatch_ShouldUpdateOnlyNonEmptyFields() {
        Director partialUpdate = new Director();
        partialUpdate.setId(1L);
        partialUpdate.setFirstName("New");
        // secondName and lastName remain empty

        when(inMemoryCache.get(1L)).thenReturn(Optional.of(testDirector));
        when(directorRepository.save(any(Director.class))).thenReturn(testDirector);

        directorService.patch(partialUpdate);

        assertEquals("New", testDirector.getFirstName());
        assertEquals("", testDirector.getSecondName()); // remains empty
        assertEquals("Nolan", testDirector.getLastName()); // remains unchanged
    }

    @Test
    void delete_ShouldRemoveDirectorAndClearCache() {
        when(directorRepository.existsById(1L)).thenReturn(true);

        directorService.delete(1L);

        verify(directorRepository).deleteById(1L);
        verify(inMemoryCache).del(1L);
        verify(filmService).removeDirectorFromFilmsCache(1L);
    }

    @Test
    void getFullName_ShouldHandleEmptyParts() {
        Director director = new Director();
        director.setFirstName("First");
        director.setLastName("Last");

        assertEquals("First Last", director.getFullName());
    }
}