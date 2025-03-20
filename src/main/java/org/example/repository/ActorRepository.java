package org.example.repository;

import java.util.Optional;
import org.example.model.db.Actor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends CrudRepository<Actor, Long> {
    Optional<Actor> getByFirstNameAndSecondNameAndLastName(
            String firstName, String secondName, String lastName);
}
