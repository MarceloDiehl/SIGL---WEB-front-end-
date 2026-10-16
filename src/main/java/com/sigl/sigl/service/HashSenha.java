package com.sigl.sigl.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hash das senhas como está gravado hoje em cadastroFuncionario (MD5 em hexadecimal,
 * o mesmo que o CryptoJS.MD5 gera no cadastro). Ponto único para a troca por BCrypt.
 */
public final class HashSenha {

    private HashSenha() {
    }

    public static String md5(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 indisponível na JVM", e);
        }
    }
}
