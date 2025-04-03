package org.example.controller;

import org.example.model.db.Film;
import org.example.service.FilmService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    @Test
    public void getFilm_WhenFilmExists_ReturnsOk() throws Exception {
        // Arrange
        Long filmId = 1L;
        Film mockFilm = new Film(); // create your film object with test data
        mockFilm.setId(filmId);
        Mockito.when(filmService.get(filmId)).thenReturn(mockFilm);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/{id}", filmId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(filmId));
    }

    @Test
    public void getFilm_WhenFilmDoesNotExist_ReturnsNotFound() throws Exception {
        // Arrange
        Long filmId = 999L;
        Mockito.when(filmService.get(filmId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/{id}", filmId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getFilm_WithInvalidId_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/{id}", 0))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}