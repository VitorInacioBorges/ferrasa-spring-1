package dw.editora.repository;

import java.util.List;
import dw.editora.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByNomeContainingIgnoreCase(String nome);
    List<Autor> findByEmailContainingIgnoreCase(String email);
}
