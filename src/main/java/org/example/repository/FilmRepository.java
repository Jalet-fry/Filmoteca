package org.example.repository;

import java.util.List;
import org.example.model.db.Film;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByTitle(String title);

    Film getByTitleAndYearAndDirectorId(String title, int year, Long directorId);

    @Query(value = """
            select * from film where  director_id in (
                select id from director where first_name = :firstName
                )
        """, nativeQuery = true)
    List<Film> findByDirector2(String firstName);

    @Query(value = """
            select f.* from film f join director d on f.director_id = d.Id 
            where d.first_name = :firstName
        """, nativeQuery = true)
    List<Film> findByDirector(String firstName);

    @Query(value = """
            select f.* from film f  
                join film_actors fa on f.id = fa.films_id
                join actor a on fa.actors_id = a.id
            where a.first_name = :firstName
        """, nativeQuery = true)
    List<Film> findByActor(String firstName);

    @Query(value = """
            select f.* from film f  
                join film_actors fa on f.id = fa.films_id
                join actor a on fa.actors_id = a.id
                join director d on f.director_id = d.Id
            where a.first_name = :actorName and d.first_name = :directorName
        """, nativeQuery = true)
    List<Film> findByActorAndDirector(String actorName, String directorName);
}
