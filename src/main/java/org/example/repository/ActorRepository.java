package org.example.repository;

import java.util.Optional;
import org.example.model.db.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
    Optional<Actor> getByFirstNameAndSecondNameAndLastName(
            String firstName, String secondName, String lastName);
//    List<Actor> saveAll(List<Actor> actors);
}
