package dw.editora.model;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autor")
public class Autor {

    // Attributes

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false, length = 200)
    private String biografia;

    @Column(nullable = false, unique = true)
    private String email;

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

    // Setters

    public void setId(long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public void setEmail(String email) { this.email = email; }

    // Overrides

    @Override
    public String toString() {
        return "Autor [id=" + id + ", nome=" + nome + ", email=" + email + ", biografia=" + biografia + "]";
    }
}
