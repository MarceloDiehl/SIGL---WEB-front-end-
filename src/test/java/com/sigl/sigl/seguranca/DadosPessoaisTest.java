package com.sigl.sigl.seguranca;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DadosPessoaisTest {

    @ParameterizedTest(name = "\"{0}\" -> \"{1}\"")
    @CsvSource(value = {
            "529.982.247-25 | ***.982.247-**",   // com máscara
            "52998224725    | ***.982.247-**",   // só dígitos
            "123            | ***",              // incompleto: nada é revelado
            "NULO           | NULO",
    }, delimiter = '|', nullValues = "NULO")
    void mascararCpf(String cpf, String esperado) {
        assertEquals(esperado, DadosPessoais.mascararCpf(cpf));
    }
}
