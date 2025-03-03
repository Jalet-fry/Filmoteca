//package org.example.service.implementation;
//
//import org.example.model.Film;
//import org.example.service.FilmService;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class FilmServiceImpl implements FilmService {
//    private final HashMap<Integer, Film> films = new HashMap<>(
//            Map.of(
//                    1, new Film(1, "123", "C:\film", 2015),
//                    2, Film.builder().id(2).title("234").year(2005).build()
//
//
//            )
//
//    );
//
//    @Override
//    public void create(Film film) {
//        films.put(film.getId(), film);
//    }
//
//    @Override
//    public Film getByTitle(String title) {
//        return films.values().stream()
//                .filter(v -> title.equals(v.getTitle()))
//                .findFirst().orElse(null);
//    }
//
//    @Override
//    public Film get(int id) {
//        return films.get(id);
//    }
//}

package org.example.service.implementation;

import org.example.model.Film;
import org.example.repository.FilmRepository;
import org.example.service.FilmService;
import org.springframework.stereotype.Service;

import java.util.List;

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
