package com.sigl.sigl.service;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.repository.FuncionarioRepository;
import com.sigl.sigl.seguranca.SegurancaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    private final PasswordEncoder passwordEncoder = new SegurancaConfig().passwordEncoder();
    private FuncionarioService funcionarioService;
    private Funcionario funcionario;

    @BeforeEach
    void preparar() {
        funcionarioService = new FuncionarioService(funcionarioRepository, new ValidatorService(), passwordEncoder);
        funcionario = new Funcionario();
        funcionario.setCpf("529.982.247-25");
        funcionario.setCep("90050-320");
        funcionario.setTelefone("(51) 99812-4410");
        funcionario.setEmail("nova@orion.com.br");
    }

    @Test
    @DisplayName("Grava a senha só como hash BCrypt, nunca como foi digitada")
    void salvar_GravaSenhaEmBcrypt() {
        funcionario.setSenha("senha-nova");
        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(i -> i.getArgument(0));

        Funcionario salvo = funcionarioService.salvar(funcionario);

        assertTrue(salvo.getSenha().startsWith("{bcrypt}"));
        assertNotEquals("senha-nova", salvo.getSenha());
        assertTrue(passwordEncoder.matches("senha-nova", salvo.getSenha()));
    }

    @Test
    @DisplayName("Recusa senha com menos de 5 caracteres")
    void salvar_SenhaCurta_Recusa() {
        funcionario.setSenha("1234");

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> funcionarioService.salvar(funcionario));
        assertEquals("A senha deve ter pelo menos 5 caracteres.", erro.getMessage());
        verify(funcionarioRepository, never()).save(any());
    }
}
