package org.example.repository;

import java.util.List;
import java.util.Optional;
import org.example.model.db.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    Optional<Director> getByFirstNameAndSecondNameAndLastName(
            String firstName, String secondName, String lastName);
}
