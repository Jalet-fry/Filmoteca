package org.example.service;

import java.util.List;
import org.example.model.db.Actor;

public interface ActorService {
    void create(Actor actor);

    Actor get(long id);

    List<Actor> getAll();

    void update(Actor entity);

    void delete(long id);

    Actor getByName(String name);
}

