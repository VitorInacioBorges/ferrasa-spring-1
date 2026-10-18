package dw.editora.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dw.editora.model.Autor;
import dw.editora.service.AutorService;
import dw.editora.service.EmailJaCadastradoException;
import dw.editora.model.Artigo;
import dw.editora.repository.ArtigoRepository;

@CrossOrigin(origins = "*")
@RestController
public class AutorController {
    private static final Logger log = LoggerFactory.getLogger(AutorController.class);

    private final AutorService service;
    private final ArtigoRepository artigoRepository;

    public AutorController(AutorService service, ArtigoRepository artigoRepository) {
        this.service = service;
        this.artigoRepository = artigoRepository;
    }

    // getAllAutores
    @GetMapping("/api/autores")
    public ResponseEntity<List<Autor>> getAllAutores(@RequestParam(required = false) String nome) {
        try {
            List<Autor> la = service.listar(nome);

            if (la.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return ResponseEntity.ok(la);

        } catch (Exception e) {
            log.error("Erro ao listar autores", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // getAutorById
    @GetMapping("/api/autores/{id}")
    public ResponseEntity<Autor> getAutorById(@PathVariable("id") long id) {
        try {
            Autor a = service.buscarPorId(id).orElse(null);
            if (a == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(a, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erro ao buscar autor {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // createAutor
    @PostMapping("/api/autores")
    public ResponseEntity<Autor> createAutor(@RequestBody Autor au) {
        try {
            Autor a = service.criar(au);
            return new ResponseEntity<>(a, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Dados inválidos (nome ou email ausentes / mal formatados)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EmailJaCadastradoException | DataIntegrityViolationException e) {
            // Email já cadastrado (checagem do service ou constraint UNIQUE do banco)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Erro ao criar autor", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // getArtigosByAutorId
    @GetMapping("/api/autores/{id}/artigos")
    public ResponseEntity<List<Artigo>> getArtigosPorAutor(
            @PathVariable("id") long id) {
        try {
            // Verifica se o autor existe.
            if (service.buscarPorId(id).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Consulta apenas os artigos publicados do autor.
            List<Artigo> artigos = artigoRepository.findByAutorIdAndPublicado(id, true);

            // Mesmo padrão das outras listagens: 204 quando não há resultados
            if (artigos.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return ResponseEntity.ok(artigos);

        } catch (Exception e) {
            log.error("Erro ao listar artigos do autor {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
