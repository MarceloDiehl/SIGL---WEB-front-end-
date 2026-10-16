package com.sigl.sigl.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HashSenhaTest {

    @Test
    @DisplayName("Gera o MD5 em hexadecimal minúsculo, igual ao CryptoJS.MD5 do cadastro")
    void md5_GeraHashHexadecimal() {
        assertEquals("900150983cd24fb0d6963f7d28e17f72", HashSenha.md5("abc"));
    }
}
