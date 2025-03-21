
package org.example.service.implementation;


import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
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
    private final EntityManager entityManager;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;


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
    public void put(Film film) {
        Film existed = filmRepository.findById(film.getId()).orElseThrow();
        existed.setTitle(film.getTitle());

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
                oldDirector.setFirstName(director.getFirstName());
                oldDirector.setSecondName(director.getSecondName());
                oldDirector.setLastName(director.getLastName());
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
                            if (oldActor != null) {
                                oldActor.setFirstName(actor.getFirstName());
                                oldActor.setSecondName(actor.getSecondName());
                                oldActor.setLastName(actor.getLastName());
                                return oldActor;
                            } else {
                                oldActor = actorRepository.findById(actor.getId()).orElseThrow();
                                oldActor.setFirstName(actor.getFirstName());
                                oldActor.setSecondName(actor.getSecondName());
                                oldActor.setLastName(actor.getLastName());
                                return oldActor;
                            }
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
        existed.setYear(film.getYear());
        existed.setLink(film.getLink());
        filmRepository.save(existed);
    }

    @Override
    @Transactional
    public void patch(Film film) {
        Film existed = filmRepository.findById(film.getId()).orElseThrow();
        if (film.getTitle() != null) {
            existed.setTitle(film.getTitle());
        }
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
                if (director.getFirstName() != null) {
                    oldDirector.setFirstName(director.getFirstName());
                }
                if (director.getSecondName() != null) {
                    oldDirector.setSecondName(director.getSecondName());
                }
                if (director.getLastName() != null) {
                    oldDirector.setLastName(director.getLastName());
                }
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
                            if (oldActor != null) {
                                if (actor.getFirstName() != null) {
                                    oldActor.setFirstName(actor.getFirstName());
                                }
                                if (actor.getSecondName() != null) {
                                    oldActor.setSecondName(actor.getSecondName());
                                }
                                if (actor.getLastName() != null) {
                                    oldActor.setLastName(actor.getLastName());
                                }
                                return oldActor;
                            } else {
                                oldActor = actorRepository.findById(actor.getId()).orElseThrow();
                                if (actor.getFirstName() != null) {
                                    oldActor.setFirstName(actor.getFirstName());
                                }
                                if (actor.getSecondName() != null) {
                                    oldActor.setSecondName(actor.getSecondName());
                                }
                                if (actor.getLastName() != null) {
                                    oldActor.setLastName(actor.getLastName());
                                }
                                return oldActor;
                            }
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
