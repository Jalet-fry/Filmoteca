package org.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActorDto {
    private long id;
    private String firstName;
    private String secondName;
    private String lastName;
    private List<FilmDto> films;
}