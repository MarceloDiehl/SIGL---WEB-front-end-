package com.sigl.sigl.seguranca;

import com.sigl.sigl.service.HashSenha;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Confere senhas gravadas no formato antigo (MD5 sem prefixo). Nunca gera hash novo:
 * senhas novas e atualizadas são sempre BCrypt (ver SegurancaConfig.passwordEncoder).
 */
public class Md5LegadoPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence senha) {
        throw new UnsupportedOperationException("MD5 serve só para conferir senhas antigas.");
    }

    @Override
    public boolean matches(CharSequence senhaDigitada, String hashGravado) {
        if (senhaDigitada == null || hashGravado == null) {
            return false;
        }
        // comparação em tempo constante: não revela pelo tempo de resposta quantos caracteres batem
        return MessageDigest.isEqual(
                HashSenha.md5(senhaDigitada.toString()).getBytes(StandardCharsets.UTF_8),
                hashGravado.getBytes(StandardCharsets.UTF_8));
    }
}
