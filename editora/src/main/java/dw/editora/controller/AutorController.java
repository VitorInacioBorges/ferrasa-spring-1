package dw.editora.controller;

import java.util.ArrayList;
import java.util.List;

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
import dw.editora.repository.AutorRepository;

@CrossOrigin(origins = "*")
@RestController 
public class AutorController {
    private final AutorRepository rep;

    public AutorController(AutorRepository rep) {
        this.rep = rep;
    }

    @GetMapping("/autores")
    public ResponseEntity<List<Autor>> getAllAutores(@RequestParam(required = false) String nome) {
        try {
            List<Autor> la = new ArrayList<Autor>();

            if (nome == null) 
              rep.findAll().forEach(la::add);
            else 
                la = rep.findByNomeContainingIgnoreCase(nome);
            
            if (la.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return ResponseEntity.ok(la);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } 

    @GetMapping("/autores/{id}")
    public ResponseEntity<Autor> getAutorById(@PathVariable("id") long id) {
        try {
            Autor a = rep.findById(id).orElse(null);
            if (a == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(a, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/autores")
    public ResponseEntity<Autor> createAutor(@RequestBody Autor au) {
        try {
            Autor a = rep.save(new Autor(au.getNome(), au.getBiografia(), au.getEmail()));
            return new ResponseEntity<>(a, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
