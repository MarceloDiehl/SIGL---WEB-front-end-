package com.sigl.sigl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorServiceTest {

    private ValidatorService validatorService;

    @BeforeEach
    void setUp() {
        this.validatorService = new ValidatorService();
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF inválido (dígitos repetidos)")
    void validarCpf_DeveLancarExcecao_QuandoCpfInvalidoComDigitosRepetidos() {
        String cpfInvalido = "111.111.111-11";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validatorService.validarCpf(cpfInvalido);
        });
        assertEquals("CPF inválido.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF com dígito verificador incorreto")
    void validarCpf_DeveLancarExcecao_QuandoCpfComDigitoVerificadorIncorreto() {
        String cpfInvalido = "123.456.789-10";
        assertThrows(IllegalArgumentException.class, () -> {
            validatorService.validarCpf(cpfInvalido);
        });
    }

    @Test
    @DisplayName("Não deve lançar exceção para CPF válido")
    void validarCpf_NaoDeveLancarExcecao_QuandoCpfValido() {
        String cpfValido = "015.210.560-30";
        assertDoesNotThrow(() -> {
            validatorService.validarCpf(cpfValido);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para CEP com formato inválido")
    void validarCep_DeveLancarExcecao_QuandoCepComFormatoInvalido() {
        String cepInvalido = "901234-567";
        assertThrows(IllegalArgumentException.class, () -> {
            validatorService.validarCep(cepInvalido);
        });
    }

    @Test
    @DisplayName("Não deve lançar exceção para CEP válido")
    void validarCep_NaoDeveLancarExcecao_QuandoCepValido() {
        String cepValido = "90010-210";
        assertDoesNotThrow(() -> {
            validatorService.validarCep(cepValido);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para Telefone com formato inválido")
    void validarTelefone_DeveLancarExcecao_QuandoTelefoneComFormatoInvalido() {
        String telefoneInvalido = "51 99876-5432";
        assertThrows(IllegalArgumentException.class, () -> {
            validatorService.validarTelefone(telefoneInvalido);
        });
    }

    @Test
    @DisplayName("Não deve lançar exceção para Telefone válido")
    void validarTelefone_NaoDeveLancarExcecao_QuandoTelefoneValido() {
        String telefoneValido = "(51) 99876-5432";
        assertDoesNotThrow(() -> {
            validatorService.validarTelefone(telefoneValido);
        });
    }
}