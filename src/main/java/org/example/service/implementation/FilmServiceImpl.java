
package org.example.service.implementation;


import static java.util.function.Function.identity;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.exception.FilmAlreadyExists;
import org.example.model.db.Actor;
import org.example.model.db.Director;
import org.example.model.db.Film;
import org.example.repository.ActorRepository;
import org.example.repository.DirectorRepository;
import org.example.repository.FilmRepository;
import org.example.service.FilmService;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
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

    @SneakyThrows
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
        } else {
            var existedFilm = filmRepository.getByTitleAndYearAndDirectorId(
                    film.getTitle(), film.getYear(), null);
            if (existedFilm != null) {
                throw new FilmAlreadyExists(existedFilm.getTitle());
            }
        }
        filmRepository.save(film);
    }

    @Override
    @Transactional
    public void put(Film film) {
        Film existed = filmRepository.findById(film.getId()).orElseThrow();
        existed.updateForPut(film);
        putDirector(existed, film);
        putActors(existed, film);
        filmRepository.save(existed);
    }

    @Override
    @Transactional
    public void patch(Film film) {
        Film existed = filmRepository.findById(film.getId()).orElseThrow();
        existed.updateForPatch(film);
        patchDirector(existed, film);
        patchActors(existed, film);
        filmRepository.save(existed);
    }

    private void putDirector(Film existed, Film film) {
        Director director = film.getDirector();
        if (director == null) {
            existed.setDirector(null);
        } else {
            Director oldDirector = existed.getDirector();
            if (director.getId() != 0) {
                if (oldDirector == null || oldDirector.getId() != director.getId()) {
                    oldDirector = directorRepository.findById(director.getId()).orElseThrow();
                    existed.setDirector(oldDirector);
                }
                oldDirector.updateForPut(director);
            } else if (oldDirector == null
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
    }

    private void patchDirector(Film existed, Film film) {
        Director director = film.getDirector();
        if (director != null) {
            Director oldDirector = existed.getDirector();
            if (director.getId() != 0) {
                if (oldDirector == null || oldDirector.getId() != director.getId()) {
                    oldDirector = directorRepository.findById(director.getId()).orElseThrow();
                    existed.setDirector(oldDirector);
                }
                oldDirector.updateForPatch(director);
            } else if (oldDirector == null
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
    }

    @SuppressWarnings("java:S1144")
    private void putActorsOld(Film existed, Film film) {
        if (film.getActors() == null) {
            existed.setActors(null);
        } else {
            Map<Long, Actor> existingActorsMap = existed.getActors().stream()
                    .collect(Collectors.toMap(Actor::getId, identity()));
            List<Actor> updatedActors = film.getActors().stream()
                    .map(actor -> {
                        if (actor.getId() != 0) {
                            Actor oldActor = existingActorsMap.getOrDefault(
                                    actor.getId(),
                                    actorRepository.findById(actor.getId()).orElseThrow());
                            oldActor.updateForPut(actor);
                            return oldActor;
                        } else {
                            return actorRepository
                                    .getByFirstNameAndSecondNameAndLastName(
                                            actor.getFirstName(),
                                            actor.getSecondName(),
                                            actor.getLastName())
                                    .orElse(actor);
                        }
                    })
                    .collect(Collectors.toList());
            existed.setActors(updatedActors);
        }
    }

    private void putActors(Film existed, Film film) {
        if (film.getActors() == null) {
            existed.setActors(null);
        } else {
            Map<Long, Actor> existingActorsMap = existed.getActors().stream()
                    .collect(Collectors.toMap(Actor::getId, identity()));
            Set<Long> newActorIds = film.getActors().stream()
                    .map(Actor::getId)
                    .collect(Collectors.toSet());
            existed.getActors().removeIf(actor -> !newActorIds.contains(actor.getId()));
            film.getActors().forEach(actor -> {
                if (actor.getId() != 0) {
                    Actor oldActor = existingActorsMap.get(actor.getId());
                    if (oldActor == null) {
                        oldActor = actorRepository.findById(actor.getId()).orElseThrow();
                        existed.getActors().add(oldActor);
                    }
                    oldActor.updateForPut(actor);
                } else {
                    existed.getActors().add(actorRepository
                            .getByFirstNameAndSecondNameAndLastName(
                                    actor.getFirstName(),
                                    actor.getSecondName(),
                                    actor.getLastName())
                            .orElse(actor));
                }
            });
        }
    }

    private void patchActors(Film existed, Film film) {
        if (film.getActors() != null) {
            Map<Long, Actor> existingActorsMap = existed.getActors().stream()
                    .collect(Collectors.toMap(Actor::getId, actor -> actor));
            Set<Long> newActorIds = film.getActors().stream()
                    .map(Actor::getId)
                    .collect(Collectors.toSet());
            existed.getActors().removeIf(actor -> !newActorIds.contains(actor.getId()));
            List<Actor> updatedActors = film.getActors().stream()
                    .map(actor -> {
                        if (actor.getId() != 0) {
                            Actor oldActor = existingActorsMap.get(actor.getId());
                            if (oldActor == null) {
                                oldActor = actorRepository.findById(actor.getId()).orElseThrow();
                            }
                            oldActor.updateForPatch(actor);
                            return oldActor;
                        } else {
                            return actorRepository
                                    .getByFirstNameAndSecondNameAndLastName(
                                            actor.getFirstName(),
                                            actor.getSecondName(),
                                            actor.getLastName())
                                    .orElse(actor);
                        }
                    })
                    .collect(Collectors.toList());

            existed.setActors(updatedActors);
        }
    }

    @Override
    public void delete(long id) {
        filmRepository.deleteById(id);
    }
}
