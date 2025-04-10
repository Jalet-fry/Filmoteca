package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.model.ActorDto;
import org.example.model.db.Actor;
import org.example.service.ActorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ActorControllerTest {

    @Mock
    private ActorService actorService;

    @InjectMocks
    private ActorController actorController;

    private Actor testActor;
    private ActorDto testActorDto;

    @BeforeEach
    void setUp() {
        testActor = new Actor();
        testActor.setId(1L);
        testActor.setFirstName("Leonardo");
        testActor.setSecondName("");
        testActor.setLastName("DiCaprio");

        testActorDto = new ActorDto(1L, "Leonardo", "DiCaprio", null, null);
    }

    @Test
    void create_ShouldReturnCreatedResponse() {
        ResponseEntity<String> response = actorController.create(testActorDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Actor created successfully", response.getBody());
        verify(actorService).create(any(Actor.class));
    }

    @Test
    void createActorsBulk_ShouldReturnCreatedResponse() {
        List<ActorDto> actorDtos = List.of(testActorDto);

        ResponseEntity<String> response = actorController.createActorsBulk(actorDtos);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Successfully created 1 actors", response.getBody());
        verify(actorService).createAll(anyList());
    }

    @Test
    void put_ShouldReturnOkResponse() {
        ResponseEntity<String> response = actorController.put(testActorDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Actor changed successfully", response.getBody());
        verify(actorService).put(any(Actor.class));
    }

    @Test
    void patch_ShouldReturnOkResponse() {
        ResponseEntity<String> response = actorController.patch(testActorDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Actor changed successfully", response.getBody());
        verify(actorService).patch(any(Actor.class));
    }

    @Test
    void delete_ShouldReturnOkResponse() {
        ResponseEntity<String> response = actorController.delete(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Actor deleted successfully", response.getBody());
        verify(actorService).delete(1L);
    }

    @Test
    void getByName_ShouldReturnActor() {
        when(actorService.getByName("Leonardo", "", "DiCaprio")).thenReturn(testActor);

        ResponseEntity<ActorDto> response = actorController.getByName("Leonardo", "", "DiCaprio");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Leonardo", response.getBody().getFirstName());
        assertEquals("DiCaprio", response.getBody().getLastName());
    }

    @Test
    void getByName_ShouldReturnNotFound() {
        when(actorService.getByName("Unknown", "", "Actor")).thenReturn(null);

        ResponseEntity<ActorDto> response = actorController.getByName("Unknown", "", "Actor");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void get_ShouldReturnActor() {
        when(actorService.get(1L)).thenReturn(testActor);

        ResponseEntity<ActorDto> response = actorController.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Leonardo", response.getBody().getFirstName());
    }

    @Test
    void get_ShouldReturnNotFound() {
        when(actorService.get(999L)).thenReturn(null);

        ResponseEntity<ActorDto> response = actorController.get(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllActors_ShouldReturnAllActors() {
        when(actorService.getAll()).thenReturn(List.of(testActor));

        ResponseEntity<List<ActorDto>> response = actorController.getAllActors();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals("Leonardo", response.getBody().get(0).getFirstName());
    }
}