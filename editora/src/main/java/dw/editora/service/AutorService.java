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

    // Regras: nome e email obrigatórios, email em formato válido e único
    public Autor criar(Autor autor) {
        String nome = autor.getNome() == null ? "" : autor.getNome().trim();
        String email = autor.getEmail() == null ? "" : autor.getEmail().trim().toLowerCase();

        if (nome.isEmpty() || nome.length() > 80)
            throw new IllegalArgumentException("Nome é obrigatório e deve ter até 80 caracteres");
        if (email.isEmpty() || !email.contains("@"))
            throw new IllegalArgumentException("Email é obrigatório e deve ser válido");
        if (repository.existsByEmail(email))
            throw new EmailJaCadastradoException(email);

        return repository.save(new Autor(nome, autor.getBiografia(), email));
    }
}
