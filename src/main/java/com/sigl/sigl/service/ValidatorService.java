package com.sigl.sigl.service;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class ValidatorService {

    public void validarCpf(String cpf) {
        if (cpf == null || !isCpfValido(cpf)) {
            throw new IllegalArgumentException("CPF inválido.");
        }
    }

    public void validarCep(String cep) {
        if (cep == null || !Pattern.matches("\\d{5}-\\d{3}", cep)) {
            throw new IllegalArgumentException("Formato de CEP inválido. Use o formato 00000-000.");
        }
    }

    public void validarTelefone(String telefone) {
        if (telefone == null || !Pattern.matches("\\(\\d{2}\\) \\d{5}-\\d{4}", telefone)) {
            throw new IllegalArgumentException("Formato de telefone inválido. Use (XX) XXXXX-XXXX.");
        }
    }

    private boolean isCpfValido(String cpf) {
        String cpfNumerico = cpf.replaceAll("[^0-9]", "");
        if (cpfNumerico.length() != 11 || cpfNumerico.matches("(\\d)\\1{10}")) {
            return false;
        }
        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Integer.parseInt(String.valueOf(cpfNumerico.charAt(i))) * (10 - i);
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 > 9) digito1 = 0;
            if (digito1 != Integer.parseInt(String.valueOf(cpfNumerico.charAt(9)))) return false;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Integer.parseInt(String.valueOf(cpfNumerico.charAt(i))) * (11 - i);
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 > 9) digito2 = 0;
            return digito2 == Integer.parseInt(String.valueOf(cpfNumerico.charAt(10)));
        } catch (NumberFormatException e) {
            return false;
        }
    }
}