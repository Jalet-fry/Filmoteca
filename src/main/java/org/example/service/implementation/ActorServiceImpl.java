
package org.example.service.implementation;


import java.util.List;
import lombok.AllArgsConstructor;
import org.example.model.db.Actor;
import org.example.repository.ActorRepository;
import org.example.service.ActorService;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;

    @Override
    public void create(Actor actor) {
        actorRepository.save(actor);
    }

    @Override
    public Actor get(long id) {
        return actorRepository.findById(id).orElse(null);
    }

    @Override
    public List<Actor> getAll() {
        return (List<Actor>) actorRepository.findAll();
    }

    @Override
    public void put(Actor actor) {
        Actor existed = actorRepository.findById(actor.getId()).orElseThrow();
        existed.setFirstName(actor.getFirstName());
        existed.setSecondName(actor.getSecondName());
        existed.setLastName(actor.getLastName());
        actorRepository.save(existed);
    }

    @Override
    public void patch(Actor actor) {
        Actor existed = actorRepository.findById(actor.getId()).orElseThrow();
        if (!actor.getFirstName().isEmpty()) {
            existed.setFirstName(actor.getFirstName());
        }
        if (!actor.getSecondName().isEmpty()) {
            existed.setSecondName(actor.getSecondName());
        }
        if (!actor.getLastName().isEmpty()) {
            existed.setLastName(actor.getLastName());
        }
        actorRepository.save(existed);
    }

    @Override
    public void delete(long id) {
        actorRepository.deleteById(id);

    }

    @Override
    public Actor getByName(String name) {
        return null;
    }
}
