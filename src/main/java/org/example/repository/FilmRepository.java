package org.example.repository;

import java.util.List;
import org.example.model.db.Film;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> getByTitle(String title);

    Boolean existsByTitle(String title);

    @Query(value = """
            select * from film where  director_id = (
                select id from director where name = :name
                )
        """, nativeQuery = true)
    List<Film> findByDirector(String name);
}
