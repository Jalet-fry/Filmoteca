package org.example.service;

import java.util.List;
import org.example.model.db.Film;

public interface FilmService {
    List<Film> getByDirector(String director);

    List<Film> getByActor(String actor);

    List<Film> findByActorAndDirector(String actorName, String directorName);

    void create(Film film);

    List<Film> getByTitle(String title);

    Film get(Long id);

    List<Film> getAll();  // Новый метод

    void createAll(List<Film> films);

    void put(Film entity);

    void patch(Film film);

    void delete(long id);

    void removeActorFromFilmsCache(Long actorId);

    void removeDirectorFromFilmsCache(Long directorId);
}

