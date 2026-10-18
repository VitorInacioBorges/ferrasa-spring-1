package dw.editora.service;

// Lançada quando se tenta cadastrar um autor com um email que já existe
public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("Já existe um autor com o email " + email);
    }
}
