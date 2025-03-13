
package org.example.service.implementation;


import org.example.model.db.Film;
import org.example.repository.FilmRepository;
import org.example.service.FilmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;

    public FilmServiceImpl(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Override
    @Transactional
    public void create(Film film) {
        filmRepository.save(film);
    }

    @Override
    public Film getByTitle(String title) {
        return filmRepository.getByTitle(title);
    }

    @Override
    public Film get(long id) {
        return filmRepository.findById(id).orElse(null);
    }

    @Override
    public List<Film> getAll() {
        return (List<Film>) filmRepository.findAll();
    }
}
