package dw.editora.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/")
    public ResponseEntity<List<Autor>> getAllAutores(@RequestParam(required = false) String nome) {
        try {

        } catch (Exception e) {
            
        }
    } 
}
