
package org.example.service.implementation;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.annotations.CacheBean;
import org.example.exception.ActorAlreadyExists;
import org.example.exception.BulkOperation;
import org.example.model.db.Actor;
import org.example.repository.ActorRepository;
import org.example.service.ActorService;
import org.example.service.FilmService;
import org.example.service.InMemoryCache;
import org.springframework.dao.DataIntegrityViolationException;
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

    @SneakyThrows
    @Override
    public void createAll(List<Actor> actors) {
        actors.forEach(actor -> actor.setId(0));

        try {
            List<Actor> savedActors = actorRepository.saveAll(actors);
            savedActors.forEach(actor -> inMemoryCache.put(actor.getId(), actor));
        } catch (Exception ex) {
            throw new BulkOperation("actors");
        }
    }

    @Override
    public Actor get(Long id) {
        Optional<Actor> cachedActor = inMemoryCache.get(id);
        if (cachedActor.isPresent()) {
            log.info(String.format("Actor %d fetched from cache.", id));
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
        //List<Actor> result = (List<Actor>) actorRepository.findAll();
        List<Actor> result = actorRepository.findAll();
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
        if (!actorRepository.existsById(id)) {
            throw new EntityNotFoundException("Actor with id " + id + " not found");
        }
        actorRepository.deleteById(id);
        filmService.removeActorFromFilmsCache(id);
        inMemoryCache.del(id);
    }

    @Override
    public Actor getByName(String firstName, String secondName, String lastName) {
        return actorRepository
                .getByFirstNameAndSecondNameAndLastName(firstName, secondName, lastName)
                .orElseThrow(() -> new EntityNotFoundException("Actor not found with name: "
                        + firstName + " " + secondName + " " + lastName));
    }

}
