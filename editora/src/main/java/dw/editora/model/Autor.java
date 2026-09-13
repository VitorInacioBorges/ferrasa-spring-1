package dw.editora.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "autor")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false)
    private Date dataNascimento;

    @Column(nullable = false, length = 80)
    private String nacionalidade;

    @Column(nullable = false, length = 80)
    private String email;

    public Autor() {
    }

    public Autor(String nome, Date dataNascimento, String nacionalidade, String email) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.nacionalidade = nacionalidade;
        this.email = email;
    }

    
}
