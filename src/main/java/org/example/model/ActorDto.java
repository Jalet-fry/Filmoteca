package org.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActorDto {
    private long id;
    @Length(max = 20)
    private String firstName;
    @Length(max = 20)
    private String secondName;
    @Length(max = 20)
    private String lastName;
    private List<FilmDto> films;
}