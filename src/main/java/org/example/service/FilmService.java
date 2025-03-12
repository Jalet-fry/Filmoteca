package org.example.service;

import org.example.model.Film;

import java.util.List;

public interface FilmService {
    void create(Film film);

    Film getByTitle(String title);

    Film get(int id);

    List<Film> getAll();  // Новый метод
}

