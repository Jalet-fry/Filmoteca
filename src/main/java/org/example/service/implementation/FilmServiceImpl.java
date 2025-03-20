
package org.example.service.implementation;


import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.example.model.db.Director;
import org.example.model.db.Film;
import org.example.repository.ActorRepository;
import org.example.repository.DirectorRepository;
import org.example.repository.FilmRepository;
import org.example.service.FilmService;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final ActorRepository actorRepository;
    private final FilmRepository filmRepository;
    private final DirectorRepository directorRepository;


    @Override
    public List<Film> getByTitle(String title) {
        return filmRepository.findByTitle(title);
    }

    @Override
    public Film get(long id) {
        return filmRepository.findById(id).orElse(null);
    }

    @Override
    public List<Film> getAll() {
        return (List<Film>) filmRepository.findAll();
    }

    @Override
    public List<Film> getByDirector(String director) {
        return filmRepository.findByDirector(director);
    }

    @Override
    @Transactional
    public void create(Film film) {
        film.setActors(film.getActors().stream()
                .map(actor -> actorRepository
                        .getByFirstNameAndSecondNameAndLastName(
                            actor.getFirstName(),
                            actor.getSecondName(),
                            actor.getLastName())
                        .orElse(actor)).toList());
        Director director = film.getDirector();
        if (director != null) {
            film.setDirector(directorRepository
                    .getByFirstNameAndSecondNameAndLastName(
                            director.getFirstName(),
                            director.getSecondName(),
                            director.getLastName())
                    .orElse(film.getDirector()));
        }
        filmRepository.save(film);
    }

    @Override
    @Transactional
    public void update(Film film) {
        Film existed = filmRepository.findById(film.getId()).orElseThrow();
        if (film.getTitle() != null) {
            existed.setTitle(film.getTitle());
        }
        Director director = film.getDirector();
        if (director == null) {
            existed.setDirector(null);
        } else {
            Director oldDirector = existed.getDirector();
            if (oldDirector == null
                    || !oldDirector.getFirstName().equals(director.getFirstName())
                    || !oldDirector.getSecondName().equals(director.getSecondName())
                    || !oldDirector.getLastName().equals(director.getLastName())) {
                existed.setDirector(directorRepository.getByFirstNameAndSecondNameAndLastName(
                                director.getFirstName(),
                                director.getSecondName(),
                                director.getLastName())
                        .orElse(film.getDirector()));
            }
        }
        if (film.getActors() != null) {
            existed.setActors(film.getActors().stream()
                    .map(actor -> actorRepository
                            .getByFirstNameAndSecondNameAndLastName(
                                    actor.getFirstName(),
                                    actor.getSecondName(),
                                    actor.getLastName())
                            .orElse(actor))
                    .collect(Collectors.toList())); //toList doesn't work, Hibernate hate it
        }
        if (film.getYear() != null) {
            existed.setYear(film.getYear());
        }
        if (film.getLink() != null) {
            existed.setLink(film.getLink());
        }
        filmRepository.save(existed);
    }

    @Override
    public void delete(long id) {
        filmRepository.deleteById(id);
    }
}
