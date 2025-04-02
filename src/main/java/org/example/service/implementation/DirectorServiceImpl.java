
package org.example.service.implementation;


import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.annotations.CacheBean;
import org.example.exception.DirectorAlreadyExists;
import org.example.model.db.Director;
import org.example.repository.DirectorRepository;
import org.example.service.DirectorService;
import org.example.service.FilmService;
import org.example.service.InMemoryCache;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@AllArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;
    private final FilmService filmService;
    @CacheBean("directors")
    private final InMemoryCache<Long, Director> inMemoryCache;

    @Override
    @SneakyThrows
    public void create(Director director) {
        try {
            directorRepository.save(director);
            inMemoryCache.put(director.getId(), director);
        } catch (Exception ex) {
            throw new DirectorAlreadyExists(director.toString());
        }
    }

    @Override
    public Director get(Long id) {
        Optional<Director> cachedDirector = inMemoryCache.get(id);
        if (cachedDirector.isPresent()) {
            log.info("Director {} fetched from cache.", id);
            return cachedDirector.get();
        } else {
            Director director = directorRepository.findById(id).orElseThrow();
            inMemoryCache.put(id, director);
            log.info("Director {} fetched from database and cached.", id);
            return director;
        }
    }

    @Override
    public List<Director> getAll() {
        List<Director> result =  directorRepository.findAll();
        result.forEach(elem -> inMemoryCache.put(elem.getId(), elem));
        return result;
    }

    @Override
    public void put(Director director) {
        Director existed = inMemoryCache.get(director.getId())
                .orElseGet(() -> directorRepository.findById(director.getId()).orElseThrow());
        existed.updateForPut(director);
        inMemoryCache.put(existed.getId(), existed);
        directorRepository.save(existed);
    }

    @Override
    public void patch(Director director) {
        Director existed = inMemoryCache.get(director.getId())
                .orElseGet(() -> directorRepository.findById(director.getId()).orElseThrow());
        existed.updateForPatch(director);
        inMemoryCache.put(existed.getId(), existed);
        directorRepository.save(existed);
    }

    @Override
    public void delete(long id) {
        directorRepository.deleteById(id);
        filmService.removeDirectorFromFilmsCache(id);
        inMemoryCache.del(id);
    }

    @Override
    public Director getByName(String name) {
        return null;
    }
}
