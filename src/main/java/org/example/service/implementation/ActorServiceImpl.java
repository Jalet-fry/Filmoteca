
package org.example.service.implementation;


import jakarta.persistence.EntityExistsException;
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
        if (actorRepository.existsByName(actor.getName())) {
            throw new EntityExistsException("Actor already exists");
        }
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
    public void update(Actor actor) {
        Actor existed = actorRepository.findById(actor.getId()).orElseThrow();
        if (actor.getName() != null && !actor.getName().equals(existed.getName())) {
            if (actorRepository.existsByName(actor.getName())) {
                throw new EntityExistsException("Actor already exists");
            }
            existed.setName(actor.getName());
        }
        actorRepository.save(existed);
    }

    @Override
    public void delete(long id) {
        actorRepository.deleteById(id);

    }

    @Override
    public Actor getByName(String name) {
        return actorRepository.getByName(name).orElseThrow();
    }
}
