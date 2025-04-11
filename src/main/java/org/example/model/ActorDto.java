package org.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
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
    @Size(max = 20)
    private String firstName;
    @Size(max = 20)
    @Length(max = 20)
    private String secondName;
    @Length(max = 20)
    @Size(max = 20)
    private String lastName;
    private List<FilmDto> films;
}