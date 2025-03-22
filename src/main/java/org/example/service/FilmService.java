package org.example.service;

import java.util.List;
import org.example.exception.FilmAlreadyExists;
import org.example.model.db.Film;

public interface FilmService {
    List<Film> getByDirector(String director);

    void create(Film film) throws FilmAlreadyExists;

    List<Film> getByTitle(String title);

    Film get(long id);

    List<Film> getAll();  // Новый метод

    void put(Film entity);

    void patch(Film film);

    void delete(long id);
}

