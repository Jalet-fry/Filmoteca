
package org.example.service.implementation;


import java.util.List;
import org.example.model.Film;
import org.example.repository.FilmRepository;
import org.example.service.FilmService;
import org.springframework.stereotype.Service;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;

    public FilmServiceImpl(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Override
    public void create(Film film) {
        filmRepository.save(film);
    }

    @Override
    public Film getByTitle(String title) {
        return filmRepository.findByTitle(title).orElse(null);
    }

    @Override
    public Film get(int id) {
        return filmRepository.findById(id).orElse(null);
    }

    @Override
    public List<Film> getAll() {
        return filmRepository.findAll();
    }
}
