package org.example.repository;

import java.util.List;
import java.util.Optional;
import org.example.model.db.Film;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByTitle(String title);

    Film getByTitleAndYearAndDirectorId(String title, int year, Long directorId);

    @Query(value = """
            select * from film where  director_id = (
                select id from director where first_name = :name
                )
        """, nativeQuery = true)
    List<Film> findByDirector(String name);
}
