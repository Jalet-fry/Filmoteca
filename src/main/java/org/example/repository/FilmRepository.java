package org.example.repository;

import org.example.model.Film;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class FilmRepository {
    private final Map<Integer, Film> films = new HashMap<>(
            Map.of(
                    1, new Film(1, "123", "C:\\film", 2015),
                    2, Film.builder().id(2).title("234").year(2005).build()
            )
    );

    public void save(Film film) {
        films.put(film.getId(), film);
    }

    public Optional<Film> findByTitle(String title) {
        return films.values().stream()
                .filter(f -> f.getTitle().equals(title))
                .findFirst();
    }

    public Optional<Film> findById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

}
