package dw.editora.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dw.editora.model.Autor;
import dw.editora.repository.AutorRepository;

@Service
public class AutorService {
    private final AutorRepository repository;

    public AutorService(AutorRepository repository) {
        this.repository = repository;
    }

    public List<Autor> listar(String nome) {
        if (nome == null) {
            return repository.findAll();
        }
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public Optional<Autor> buscarPorId(long id) {
        return repository.findById(id);
    }

    public Autor criar(Autor autor) {
        return repository.save(new Autor(autor.getNome(), autor.getBiografia(), autor.getEmail()));
    }
}
