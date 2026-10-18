package dw.editora.model;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "autor")
// Ignora os campos internos do proxy do Hibernate (usado quando o Autor é carregado de forma LAZY pelo Artigo)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Autor {

    // Attributes

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 80)
    private String nome;

    // TEXT no PostgreSQL não tem limite de tamanho (texto longo)
    @Column(nullable = true, columnDefinition = "TEXT")
    private String biografia;

    @Column(nullable = false, unique = true)
    private String email;

    // Lado inverso do relacionamento (1:N).
    // @JsonIgnore corta o ciclo Autor -> Artigo -> Autor na serialização.
    // Os artigos de um autor são consultados pelo endpoint GET /api/autores/{id}/artigos.
    @JsonIgnore
    @OneToMany(mappedBy = "autor")
    private List<Artigo> artigos = new ArrayList<>();

    // Constructors

    public Autor() {
    }

    public Autor(String nome, String biografia, String email) {
        this.nome = nome;
        this.biografia = biografia;
        this.email = email;
    }

    // Getters

    public long getId() { return this.id; }
    public String getNome() { return this.nome; }
    public String getBiografia() { return this.biografia; }
    public String getEmail() { return this.email; }
    public List<Artigo> getArtigos() { return this.artigos; }

    // Setters

    public void setId(long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public void setEmail(String email) { this.email = email; }
    public void setArtigos(List<Artigo> artigos) { this.artigos = artigos; }

    // Overrides

    // Não inclui 'artigos' para não gerar recursão infinita no toString
    @Override
    public String toString() {
        return "Autor [id=" + id + ", nome=" + nome + ", email=" + email + ", biografia=" + biografia + "]";
    }
}
