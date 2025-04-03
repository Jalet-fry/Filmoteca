package org.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
public class DirectorDto {
    private long id;
    @Length(max = 20)
    private String firstName;
    @Length(max = 20)
    private String secondName;
    @Length(max = 20)
    private String lastName;
    private List<FilmDto> films;
}