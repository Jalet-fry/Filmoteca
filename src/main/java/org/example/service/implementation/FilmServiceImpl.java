package org.example.service.implementation;

import java.util.HashMap;
import org.example.model.Film;
import org.example.service.FilmService;
import org.springframework.stereotype.Service;

@Service
public class FilmServiceImpl implements FilmService {
    private final HashMap<String, Film> films = new HashMap<>();

    @Override
    public void create(Film film) {
        films.put(film.getTitle(), film);
    }

    @Override
    public Film get(String title) {
        return films.get(title);
    }
}

/*
//New not good
package org.example.unknowed.service.implementation;

import org.example.unknowed.model.Film;
import org.example.unknowed.service.FilmService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class FilmServiceImpl implements FilmService {
    private HashMap<String, Film> films = new HashMap<String, Film>();

    @Override
    public void create(Film film) {
        films.put(film.getTitle(), film);
    }

    @Override
    public Film get(String title) {
        return films.get(title);
    }
}

*/


/*
package org.example.unknowed.service.implementation;
import org.example.unknowed.model.Film;
import org.example.unknowed.service.FilmService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FilmServiceImpl implements FilmService {
    private final HashMap<String, Film> films = new HashMap<>();

    public FilmServiceImpl() {
        // Добавление примеров фильмов при запуске сервиса
        addSampleFilms();
    }

    private void addSampleFilms() {
        create(new Film(1L, "Inception", "Sci-Fi", 2010));
        create(new Film(2L, "The Dark Knight", "Action", 2008));
        create(new Film(3L, "Interstellar", "Sci-Fi", 2014));
        create(new Film(4L, "Pulp Fiction", "Crime", 1994));
    }

    @Override
    public void create(Film film) {
        films.put(film.getTitle(), film);
    }

    @Override
    public Film get(String title) {
        return films.get(title);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void delete(String title) {
        films.remove(title);
    }

    @Override
    public void update(Film film) {
        films.put(film.getTitle(), film);
    }
}
*/