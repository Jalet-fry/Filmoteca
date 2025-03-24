
package org.example.service.implementation;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.annotations.CacheBean;
import org.example.model.db.Actor;
import org.example.model.db.Director;
import org.example.model.db.Film;
import org.example.repository.DirectorRepository;
import org.example.service.DirectorService;
import org.example.service.InMemoryCache;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;
    private @CacheBean("directors") final InMemoryCache<Long, Director> inMemoryCache;

    @Override
    //@Transactional
    public void create(Director director) {
        directorRepository.save(director);
        inMemoryCache.put(director.getId(), director);
    }

    @Override
    public Director get(long id) {
        return inMemoryCache.get(id)
                .orElseGet(() -> inMemoryCache.put(id,  directorRepository
                        .findById(id).orElse(null)));
    }

    @Override
    public List<Director> getAll() {
        List<Director> result = (List<Director>) directorRepository.findAll();
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
        inMemoryCache.del(id);
        directorRepository.deleteById(id);
    }

    @Override
    public Director getByName(String name) {
        return null;
    }
}
