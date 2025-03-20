package org.example.repository;

import java.util.Optional;
import org.example.model.db.Actor;
import org.example.model.db.Director;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends CrudRepository<Director, Long> {
    Optional<Director> getByFirstNameAndSecondNameAndLastName(
            String firstName, String secondName, String lastName);
}
