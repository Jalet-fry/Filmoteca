package org.example.service;

import java.util.List;
import org.example.model.db.Actor;

public interface ActorService {
    void create(Actor actor);

    Actor get(long id);

    List<Actor> getAll();

    void put(Actor entity);

    void patch(Actor actor);

    void delete(long id);

    Actor getByName(String name);
}

