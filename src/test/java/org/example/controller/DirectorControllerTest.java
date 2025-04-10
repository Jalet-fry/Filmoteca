package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.example.model.DirectorDto;
import org.example.model.db.Director;
import org.example.service.DirectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DirectorControllerTest {

    @Mock
    private DirectorService directorService;

    @InjectMocks
    private DirectorController directorController;

    private Director testDirector;
    private DirectorDto testDirectorDto;

    @BeforeEach
    void setUp() {
        testDirector = new Director();
        testDirector.setId(1L);
        testDirector.setFirstName("Christopher");
        testDirector.setSecondName("");
        testDirector.setLastName("Nolan");

        testDirectorDto = new DirectorDto(1L, "Christopher", "Nolan", null, null);
    }

    @Test
    void create_ShouldReturnCreatedResponse() {
        ResponseEntity<String> response = directorController.create(testDirectorDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Director created successfully", response.getBody());
        verify(directorService).create(any(Director.class));
    }

    @Test
    void createDirectorsBulk_ShouldReturnCreatedResponse() {
        List<DirectorDto> directorDtos = List.of(testDirectorDto);

        ResponseEntity<String> response = directorController.createDirectorsBulk(directorDtos);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Successfully created 1 directors", response.getBody());
        verify(directorService).createAll(anyList());
    }

    @Test
    void put_ShouldReturnOkResponse() {
        ResponseEntity<String> response = directorController.put(testDirectorDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Director changed successfully", response.getBody());
        verify(directorService).put(any(Director.class));
    }

    @Test
    void patch_ShouldReturnOkResponse() {
        ResponseEntity<String> response = directorController.patch(testDirectorDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Director changed successfully", response.getBody());
        verify(directorService).patch(any(Director.class));
    }

    @Test
    void delete_ShouldReturnOkResponse() {
        ResponseEntity<String> response = directorController.delete(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Director deleted successfully", response.getBody());
        verify(directorService).delete(1L);
    }

    @Test
    void getByName_ShouldReturnDirector() {
        when(directorService.getByName("Christopher", "", "Nolan")).thenReturn(testDirector);

        ResponseEntity<DirectorDto> response = directorController.getByName("Christopher", "", "Nolan");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Christopher", response.getBody().getFirstName());
        assertEquals("Nolan", response.getBody().getLastName());
    }

    @Test
    void getByName_ShouldReturnNotFound() {
        when(directorService.getByName("Unknown", "", "Director")).thenReturn(null);

        ResponseEntity<DirectorDto> response = directorController.getByName("Unknown", "", "Director");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void get_ShouldReturnDirector() {
        when(directorService.get(1L)).thenReturn(testDirector);

        ResponseEntity<DirectorDto> response = directorController.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Christopher", response.getBody().getFirstName());
    }

    @Test
    void get_ShouldReturnNotFound() {
        when(directorService.get(999L)).thenReturn(null);

        ResponseEntity<DirectorDto> response = directorController.get(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllDirectors_ShouldReturnAllDirectors() {
        when(directorService.getAll()).thenReturn(List.of(testDirector));

        ResponseEntity<List<DirectorDto>> response = directorController.getAllDirectors();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals("Christopher", response.getBody().get(0).getFirstName());
    }
}