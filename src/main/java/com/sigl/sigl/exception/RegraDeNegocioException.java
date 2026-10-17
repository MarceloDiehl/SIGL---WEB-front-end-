package com.sigl.sigl.exception;

import org.springframework.http.HttpStatus;

/**
 * Pedido recusado por uma regra do negócio (estoque insuficiente, vendedor inexistente...).
 * A mensagem é escrita para o usuário e vai para a tela como está.
 */
public class RegraDeNegocioException extends RuntimeException {

    private final HttpStatus status;

    public RegraDeNegocioException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
