// Camada Controller do padrão MVC.
package dw.editora.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dw.editora.model.Artigo;
import dw.editora.repository.ArtigoRepository;
import dw.editora.service.AutorService;
import dw.editora.model.Autor;

// Permite que o front-end (mesmo em domínios diferentes) consuma esta API.
@CrossOrigin(origins = "*") 
// @RestController diz ao Spring: "Você gerencia esta classe e ela responde a requisições web".
// Isso é um exemplo clássico de Inversão de Controle (IoC): o framework controla o ciclo de vida do Controller.
@RestController 
public class ArtigoController {

    private static final Logger log = LoggerFactory.getLogger(ArtigoController.class);

    // Dependência da camada de dados (Repository)
    // Está marcada como 'final' para garantir que não será alterada após a injeção.
    private final ArtigoRepository rep;
    private final AutorService autorService;

    /* 
     * INJEÇÃO DE DEPENDÊNCIA (DI):
     * Em vez da classe criar uma instância do repositório (ex: rep = new ArtigoRepositoryImpl()), 
     * o framework Spring "injeta" a dependência através do construtor. Isso diminui o acoplamento
     * e facilita a criação de testes (podemos injetar um "mock" no lugar do repositório real).
     */
    public ArtigoController( ArtigoRepository rep, AutorService autorService) {
        this.rep = rep;
        this.autorService = autorService;
    }

    /*
     * GET / : listar todos os artigos
     */
    @GetMapping("/")
    public ResponseEntity<List<Artigo>> getAllArtigos(@RequestParam(required = false) String titulo){
        try {
            List<Artigo> la = new ArrayList<Artigo>();
            
            // Regra de negócio simples para filtrar pelo título se ele for passado na URL
            if (titulo == null)
                rep.findAll().forEach(la::add);
            else
                rep.findByTituloContainingIgnoreCase(titulo).forEach(la::add);

            if (la.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 se estiver vazio
            return new ResponseEntity<>(la, HttpStatus.OK); // Retorna 200 e a lista
        } catch (Exception e) {
            log.error("Erro ao listar artigos", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * GET /:id : obter um artigo dado um id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Artigo> getArtigo(@PathVariable("id") long id)
    {
        // O Optional ajuda a evitar NullPointerException
        Optional<Artigo> data = rep.findById(id);

        if (data.isPresent())
        {
            Artigo a = data.get();
            return new ResponseEntity<>(a, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 se não achar o id
    }
    /*
     * Título e resumo são obrigatórios e o título tem no máximo 80 caracteres (mesmas regras das colunas).
     * Validar aqui evita que o erro do banco vire um 500.
     */
    private boolean dadosInvalidos(Artigo ar) {
        return ar.getTitulo() == null || ar.getTitulo().isBlank() || ar.getTitulo().length() > 80
                || ar.getResumo() == null || ar.getResumo().isBlank();
    }

    /*
    * POST /api/artigos : criar um artigo associado a um autor
    */
    @PostMapping("/api/artigos")
    public ResponseEntity<Artigo> createArtigo(@RequestBody Artigo ar) {
        try {
            // Verifica se foi informado um ID positivo e os campos obrigatórios.
            if (ar.getIdAutor() <= 0 || dadosInvalidos(ar)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Procura o autor no banco pelo ID recebido.
            Autor autor = autorService
                    .buscarPorId(ar.getIdAutor())
                    .orElse(null);

            if (autor == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Cria o artigo com os dados recebidos.
            Artigo artigo = new Artigo(
                    ar.getTitulo(),
                    ar.getResumo(),
                    ar.isPublicado()
            );

            // Associa o autor e preenche o idAutor.
            artigo.setAutor(autor);

            // Salva o artigo no banco.
            Artigo salvo = rep.save(artigo);

            return new ResponseEntity<>(salvo, HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Erro ao criar artigo", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * PUT /:id : atualizar artigo dado um id
     */
    @PutMapping("/{id}")
    public ResponseEntity<Artigo> updateArtigo(@PathVariable("id") long id, @RequestBody Artigo a)
    {
        if (dadosInvalidos(a))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Optional<Artigo> data = rep.findById(id);

        if (data.isPresent())
        {
            Artigo ar = data.get();
            ar.setPublicado(a.isPublicado());
            ar.setResumo(a.getResumo());
            ar.setTitulo(a.getTitulo());

            return new ResponseEntity<>(rep.save(ar), HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /*
     * DEL /:id : remover artigo dado um id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteArtigo(@PathVariable("id") long id){
        try {
            rep.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Erro ao remover artigo {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * DEL / : remover todos os artigos
     */
    @DeleteMapping("/")
    public ResponseEntity<HttpStatus> deleteAllArtigo()
    {
        try {
            rep.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Erro ao remover todos os artigos", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }        
    }

    /*
     * GET /publicado : buscar por artigos publicados 
     */
    @GetMapping("/publicado")
    public ResponseEntity<List<Artigo>> getAllPublicado(){
        try {
            List<Artigo> la = new ArrayList<Artigo>();
            rep.findByPublicado(true).forEach(la::add);

            if (la.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return new ResponseEntity<>(la, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erro ao listar artigos publicados", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}