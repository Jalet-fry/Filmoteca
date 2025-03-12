package org.example.service;

import java.util.List;
import org.example.model.Film;

public interface FilmService {
    void create(Film film);

    Film getByTitle(String title);

    Film get(int id);

    List<Film> getAll();  // Новый метод
}

