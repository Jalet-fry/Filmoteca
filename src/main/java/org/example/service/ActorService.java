package org.example.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.SneakyThrows;
import org.example.model.db.Actor;

public interface ActorService {
    void create(Actor actor);

    void createAll(List<Actor> actors);

    Actor get(Long id);

    List<Actor> getAll();

    void put(Actor entity);

    void patch(Actor actor);

    void delete(long id);

    Actor getByName(String name);
}

