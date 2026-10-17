package com.sigl.sigl.seguranca;

/**
 * Tratamento de dados pessoais para quem não precisa vê-los inteiros (LGPD: princípio da necessidade).
 */
public final class DadosPessoais {

    private DadosPessoais() {
    }

    /**
     * CPF com os 3 primeiros e os 2 últimos dígitos ocultos: "529.982.247-25" -> "***.982.247-**".
     * Aceita o CPF com ou sem máscara; o que não tiver 11 dígitos vira "***" (nada é revelado).
     */
    public static String mascararCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            return "***";
        }
        return "***." + digitos.substring(3, 6) + "." + digitos.substring(6, 9) + "-**";
    }
}
