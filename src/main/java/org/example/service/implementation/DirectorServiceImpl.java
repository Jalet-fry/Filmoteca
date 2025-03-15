
package org.example.service.implementation;


import jakarta.persistence.EntityExistsException;
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
        if (directorRepository.existsByName(director.getName())) {
            throw new EntityExistsException("Director already exists");
        }
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
    public void update(Director director) {
        Director existed = directorRepository.findById(director.getId()).orElseThrow();
        if (director.getName() != null) {
            existed.setName(director.getName());
        }
        directorRepository.save(existed);
    }

    @Override
    public void delete(long id) {
        directorRepository.deleteById(id);
    }

    @Override
    public Director getByName(String name) {
        return directorRepository.getByName(name).orElseThrow();
    }
}
