
package org.example.service.implementation;


import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.example.model.db.Actor;
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
        return filmRepository.getByTitle(title);
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
                .map(actor -> actorRepository.getByName(actor.getName())
                        .orElse(actor)).toList());
        film.setDirector(directorRepository.getByName(film.getDirector().getName())
                .orElse(film.getDirector()));
        filmRepository.save(film);
    }

    @Override
    @Transactional
    public void update(Film film) {
        Film existed = filmRepository.findById(film.getId()).orElseThrow();
        if (film.getTitle() != null && !existed.getTitle().equals(film.getTitle())) {
            existed.setTitle(film.getTitle());
        }
        if (film.getDirector().getName() != null
                 && !film.getDirector().getName().equals(existed.getDirector().getName())) {
            existed.setDirector(directorRepository.getByName(film.getDirector().getName())
                     .orElse(film.getDirector()));
        }
        if (film.getActors() != null) {
            Map<String, Actor> existedMap = existed.getActors().stream()
                    .collect(Collectors.toMap(Actor::getName, actor -> actor));
            existed.setActors(film.getActors().stream()
                    .map(actor -> existedMap.getOrDefault(
                            actor.getName(),
                            actorRepository.getByName(actor.getName()).orElse(actor)))
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
