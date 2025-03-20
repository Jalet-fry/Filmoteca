
package org.example.service.implementation;


import java.util.List;
import lombok.AllArgsConstructor;
import org.example.model.db.Director;
import org.example.repository.DirectorRepository;
import org.example.service.DirectorService;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;

    @Override
    //@Transactional
    public void create(Director director) {
        directorRepository.save(director);
    }

    @Override
    public Director get(long id) {
        return directorRepository.findById(id).orElse(null);
    }

    @Override
    public List<Director> getAll() {
        return (List<Director>) directorRepository.findAll();
    }

    @Override
    public void put(Director director) {
        Director existed = directorRepository.findById(director.getId()).orElseThrow();
        existed.setFirstName(director.getFirstName());
        existed.setSecondName(director.getSecondName());
        existed.setLastName(director.getLastName());
        directorRepository.save(existed);
    }

    @Override
    public void patch(Director director) {
        Director existed = directorRepository.findById(director.getId()).orElseThrow();
        if (!director.getFirstName().isEmpty()) {
            existed.setFirstName(director.getFirstName());
        }
        if (!director.getSecondName().isEmpty()) {
            existed.setSecondName(director.getSecondName());
        }
        if (!director.getLastName().isEmpty()) {
            existed.setLastName(director.getLastName());
        }
        directorRepository.save(existed);
    }

    @Override
    public void delete(long id) {
        directorRepository.deleteById(id);
    }

    @Override
    public Director getByName(String name) {
        return null;
    }
}
