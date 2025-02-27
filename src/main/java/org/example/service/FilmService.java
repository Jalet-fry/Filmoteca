
package org.example.service;

import org.example.model.Film;

public interface FilmService {
    void create(Film film);

    Film get(String title);
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