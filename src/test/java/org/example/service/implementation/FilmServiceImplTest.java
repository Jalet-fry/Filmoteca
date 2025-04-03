package org.example.service.implementation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.model.db.Film;
import org.example.repository.ActorRepository;
import org.example.repository.DirectorRepository;
import org.example.repository.FilmRepository;
import org.example.service.InMemoryCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FilmServiceImplTest {
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private FilmRepository filmRepository;
    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private InMemoryCache<Long, Film> inMemoryCache;
    @InjectMocks
    private FilmServiceImpl filmServiceImpl;

    @BeforeEach
    void init_mocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void get() {
        Film film = Film.builder().build();
        when(filmRepository.findById(anyLong())).thenReturn(Optional.of(film));

        Film result = filmServiceImpl.get(1l);

        assertNotNull(result);
    }
}
