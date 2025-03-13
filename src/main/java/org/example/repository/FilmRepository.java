package org.example.repository;

import org.example.model.db.Film;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends CrudRepository<Film, Long> {
    Film getByTitle(String title);
}
