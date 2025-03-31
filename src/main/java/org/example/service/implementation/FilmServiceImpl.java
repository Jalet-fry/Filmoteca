
package org.example.service.implementation;


import static java.util.function.Function.identity;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.annotations.CacheBean;
import org.example.exception.FilmAlreadyExists;
import org.example.model.db.Actor;
import org.example.model.db.Director;
import org.example.model.db.Film;
import org.example.repository.ActorRepository;
import org.example.repository.DirectorRepository;
import org.example.repository.FilmRepository;
import org.example.service.FilmService;
import org.example.service.InMemoryCache;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final ActorRepository actorRepository;
    private final FilmRepository filmRepository;
    private final DirectorRepository directorRepository;
    @CacheBean("films")
    private final InMemoryCache<Long, Film> inMemoryCache;

    @Override
    public List<Film> getByTitle(String title) {
        return filmRepository.findByTitle(title);
    }

    /*
        @Override
        public Film get(Long id) {
            Film result = inMemoryCache.get(id)
                    .orElseGet(() -> inMemoryCache.put(id, filmRepository
                            .findById(id).orElseThrow()));
            log.info("get: {}", result);
            return result;
        }
    */

    @Override
    public Film get(Long id) {
        Optional<Film> cachedFilm = inMemoryCache.get(id);
        if (cachedFilm.isPresent()) {
            log.info("Film {} fetched from cache.", id);
            return cachedFilm.get();
        } else {
            Film film = filmRepository.findById(id).orElseThrow();
            inMemoryCache.put(id, film);
            log.info("Film {} fetched from database and cached.", id);
            return film;
        }
    }

    @Override
    public List<Film> getAll() {
        List<Film> result = (List<Film>) filmRepository.findAll();
        result.forEach(elem -> inMemoryCache.put(elem.getId(), elem));
        return result;
    }

    @Override
    public List<Film> getByDirector(String director) {
        List<Film> result = filmRepository.findByDirector(director);
        result.forEach(elem -> inMemoryCache.put(elem.getId(), elem));
        return result;
    }

    @Override
    public List<Film> getByActor(String actor) {
        List<Film> result = filmRepository.findByActor(actor);
        result.forEach(elem -> inMemoryCache.put(elem.getId(), elem));
        return result;
    }

    @Override
    public List<Film> findByActorAndDirector(String actorName, String directorName) {
        List<Film> result = filmRepository.findByActorAndDirector(
                actorName,
                directorName);
        result.forEach(elem -> inMemoryCache.put(elem.getId(), elem));
        return result;
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
        inMemoryCache.put(film.getId(), film);
    }

    private void saveToCacheAndDb(Film film) {
        Film savedFilm = filmRepository.save(film);
        savedFilm.getActors().size();
        inMemoryCache.put(film.getId(), savedFilm);
    }

    @Override
    @Transactional
    public void put(Film film) {
        Film existed = inMemoryCache.get(film.getId())
                .orElseGet(() -> filmRepository.findById(film.getId()).orElseThrow());
        existed.updateForPut(film);
        putDirector(existed, film);
        putActors(existed, film);
        saveToCacheAndDb(existed);
    }

    @Override
    @Transactional
    public void patch(Film film) {
        Film existed = inMemoryCache.get(film.getId())
                .orElseGet(() -> filmRepository.findById(film.getId()).orElseThrow());
        existed.updateForPatch(film);
        patchDirector(existed, film);
        patchActors(existed, film);
        saveToCacheAndDb(existed);
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

    private void putActors(Film existed, Film film) {
        if (film.getActors() == null) {
            existed.setActors(null);
        } else {
            Map<Long, Actor> existingActorsMap = existed.getActors().stream()
                    .collect(Collectors.toMap(Actor::getId, actor -> actor));
            List<Actor> updatedActors = film.getActors().stream()
                    .map(actor -> {
                        if (actor.getId() != 0) {
                            Actor oldActor = existingActorsMap.get(actor.getId());
                            if (oldActor == null) {
                                oldActor = actorRepository.findById(actor.getId()).orElseThrow();
                            }
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

    private void patchActors(Film existed, Film film) {
        if (film.getActors() != null) {
            Map<Long, Actor> existingActorsMap = existed.getActors().stream()
                    .collect(Collectors.toMap(Actor::getId, actor -> actor));
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
        inMemoryCache.del(id);
        filmRepository.deleteById(id);
    }

    @Transactional
    public void removeActorFromFilmsCache(Long actorId) {
        inMemoryCache.getAllValues().forEach(film -> {
            if (film.getActors() != null) {
                film.getActors().removeIf(actor -> actor.getId() == actorId);
            }
        });
    }

    @Transactional
    public void removeDirectorFromFilmsCache(Long directorId) {
        inMemoryCache.getAllValues().forEach(film -> {
            if (film.getDirector() != null && film.getDirector().getId() == directorId) {
                film.setDirector(null);
            }
        });
    }
}
