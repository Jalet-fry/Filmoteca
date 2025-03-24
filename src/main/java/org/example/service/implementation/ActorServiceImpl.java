
package org.example.service.implementation;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.annotations.CacheBean;
import org.example.model.db.Actor;
import org.example.repository.ActorRepository;
import org.example.service.ActorService;
import org.example.service.InMemoryCache;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;
    @CacheBean("actors")
    private final InMemoryCache<Long, Actor> inMemoryCache;

    @Override
    public void create(Actor actor) {
        actorRepository.save(actor);
        inMemoryCache.put(actor.getId(), actor);
    }

    @Override
    public Actor get(long id) {
        return inMemoryCache.get(id)
                .orElseGet(() -> inMemoryCache.put(id, actorRepository
                        .findById(id).orElse(null)));
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
        inMemoryCache.del(id);
        actorRepository.deleteById(id);
    }

    @Override
    public Actor getByName(String name) {
        return null;
    }
}
