//package org.example.controller;
//
//import org.example.model.ActorDto;
//import org.example.model.db.Actor;
//import org.example.service.ActorService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ActorControllerTest {
//
//    @Mock
//    private ActorService actorService;
//
//    @InjectMocks
//    private ActorController actorController;
//
//    @Test
//    void create_ShouldReturnCreated_WhenSuccessful() {
//        ActorDto actorDto = new ActorDto();
//        actorDto.setFirstName("John");
//
//        when(actorService.create(any(Actor.class))).thenAnswer(invocation -> {
//            Actor actor = invocation.getArgument(0);
//            actor.setId(1L);
//            return null;
//        });
//
//        ResponseEntity<String> response = actorController.create(actorDto);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("Actor created successfully", response.getBody());
//    }
//
//    @Test
//    void get_ShouldReturnActor_WhenExists() {
//        Actor actor = new Actor();
//        actor.setId(1L);
//        actor.setFirstName("John");
//
//        when(actorService.get(1L)).thenReturn(actor);
//
//        ResponseEntity<ActorDto> response = actorController.get(1L);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("John", response.getBody().getFirstName());
//    }
//
//    @Test
//    void delete_ShouldReturnOk_WhenSuccessful() {
//        ResponseEntity<String> response = actorController.delete(1L);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Actor deleted successfully", response.getBody());
//        verify(actorService).delete(1L);
//    }
//}