package org.example.service;

import java.util.List;
import org.example.model.db.Film;

public interface FilmService {
    List<Film> getByDirector(String director);

    void create(Film film);

    List<Film> getByTitle(String title);

    Film get(long id);

    List<Film> getAll();  // Новый метод

    void update(Film entity);

    void delete(long id);
}

