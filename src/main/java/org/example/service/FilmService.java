package org.example.service;

import org.example.model.Film;

import java.util.List;

public interface FilmService {
    void create(Film film);
    Film getByTitle(String title);
    Film get(int id);
    List<Film> getAll();  // Новый метод
}


/*
//New not good
package org.example.unknowed.service;
import java.util.List;
import org.example.unknowed.model.Film;

public interface FilmService {
    void create(Film film);
    Film get(String title);
    List<Film> getAll();
    void delete(String title);
    void update(Film film);
}
*/