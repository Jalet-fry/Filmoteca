
package org.example.service.implementation;


import lombok.RequiredArgsConstructor;
import org.example.model.db.Actor;
import org.example.model.db.Film;
import org.example.repository.ActorRepository;
import org.example.repository.FilmRepository;
import org.example.service.FilmService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final ActorRepository actorRepository;
    private final FilmRepository filmRepository;

    public FilmServiceImpl(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Override
    //@Transactional
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

    @Override
    public void update(Film film) {
        actorRepository.saveAll(film.getActors());
        filmRepository.save(film);
    }
}
