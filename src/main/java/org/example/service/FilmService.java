package org.example.service;

import java.util.List;
import org.example.model.db.Film;

public interface FilmService {
    void create(Film film);

    Film getByTitle(String title);

    Film get(long id);

    List<Film> getAll();  // Новый метод

    void update(Film entity);
}

