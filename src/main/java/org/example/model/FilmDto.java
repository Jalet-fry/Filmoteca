package org.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class FilmDto {
    private long id;
    private DirectorDto director;
    private List<ActorDto> actors;
    @Length(max = 20)
    private String title;
    private String link;
    @Max(2077)
    @Min(1896)
    private Integer year;
}