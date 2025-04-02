
package org.example.service.implementation;


import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.annotations.CacheBean;
import org.example.exception.ActorAlreadyExists;
import org.example.model.db.Actor;
import org.example.repository.ActorRepository;
import org.example.service.ActorService;
import org.example.service.FilmService;
import org.example.service.InMemoryCache;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@AllArgsConstructor
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;
    private final FilmService filmService;
    @CacheBean("actors")
    private final InMemoryCache<Long, Actor> inMemoryCache;

    @SneakyThrows
    @Override
    public void create(Actor actor) {
        try {
            actorRepository.save(actor);
            inMemoryCache.put(actor.getId(), actor);
        } catch (Exception ex) {
            throw new ActorAlreadyExists(actor.toString());
        }
    }

    @Override
    public Actor get(Long id) {
        Optional<Actor> cachedActor = inMemoryCache.get(id);
        if (cachedActor.isPresent()) {
            log.info("Actor {} fetched from cache.", id);
            return cachedActor.get();
        } else {
            Actor actor = actorRepository.findById(id).orElseThrow();
            inMemoryCache.put(id, actor);
            log.info("Actor {} fetched from database and cached.", id);
            return actor;
        }
    }

    @Override
    public List<Actor> getAll() {
        List<Actor> result = (List<Actor>) actorRepository.findAll();
        result.forEach(elem -> inMemoryCache.put(elem.getId(), elem));
        return result;
    }

    @Override
    public void put(Actor actor) {
        Actor existed = inMemoryCache.get(actor.getId())
                .orElseGet(() -> actorRepository.findById(actor.getId()).orElseThrow());
        existed.updateForPut(actor);
        inMemoryCache.put(existed.getId(), existed);
        actorRepository.save(existed);
    }

    @Override
    public void patch(Actor actor) {
        Actor existed = inMemoryCache.get(actor.getId())
                .orElseGet(() -> actorRepository.findById(actor.getId()).orElseThrow());
        existed.updateForPatch(actor);
        inMemoryCache.put(existed.getId(), existed);
        actorRepository.save(existed);
    }

    @Override
    public void delete(long id) {
        actorRepository.deleteById(id);
        filmService.removeActorFromFilmsCache(id);
        inMemoryCache.del(id);
    }

    @Override
    public Actor getByName(String name) {
        return null;
    }
}
