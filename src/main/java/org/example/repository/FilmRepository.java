package org.example.repository;

import java.util.List;
import org.example.model.db.Film;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByTitle(String title);

    Film getByTitleAndYearAndDirectorId(String title, int year, Long directorId);

    @Query(value = """
            select f.* from film f join director d on f.director_id = d.Id 
            where d.first_name = :firstName
        """, nativeQuery = true)
    List<Film> findByDirector(String firstName);

    @Query("SELECT f FROM Film f JOIN f.actors a WHERE a.firstName = :firstName")
    List<Film> findByActor(String firstName);

    @Query(value = """
            select f.* from film f  
                join film_actors fa on f.id = fa.films_id
                join actor a on fa.actors_id = a.id
                join director d on f.director_id = d.Id
            where a.first_name = :actorName and d.first_name = :directorName
        """, nativeQuery = true)
    List<Film> findByActorAndDirector(String actorName, String directorName);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END "
            + "FROM Film f WHERE f.title = :title AND f.year = :year "
            + "AND (:directorId IS NULL AND f.director IS NULL OR "
            + "f.director.id = :directorId)")
    boolean existsByTitleAndYearAndDirector(
            @Param("title") String title,
            @Param("year") Integer year,
            @Param("directorId") Long directorId
    );
}
