package org.example.service;

import java.util.List;
import org.example.model.db.Director;

public interface DirectorService {
    void create(Director director);

    Director get(long id);

    List<Director> getAll();  // Новый метод

    void update(Director entity);

    void delete(long id);

    Director getByName(String name);
}

