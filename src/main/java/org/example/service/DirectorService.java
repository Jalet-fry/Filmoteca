package org.example.service;

import java.util.List;
import org.example.model.db.Director;

public interface DirectorService {
    void create(Director director);

    Director get(Long id);

    List<Director> getAll();  // Новый метод

    void put(Director entity);

    void patch(Director director);

    void delete(long id);

    Director getByName(String name);
}

