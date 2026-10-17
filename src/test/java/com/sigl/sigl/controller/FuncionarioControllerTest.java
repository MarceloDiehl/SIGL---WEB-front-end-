package com.sigl.sigl.controller;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.repository.FuncionarioRepository;
import com.sigl.sigl.service.FuncionarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuncionarioController.class)
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FuncionarioRepository funcionarioRepository;

    @MockitoBean
    private FuncionarioService funcionarioService;

    @Test
    @DisplayName("Listagem de funcionários não devolve a senha")
    void listarFuncionarios_NaoDevolveSenha() throws Exception {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(3);
        funcionario.setNome("Marcelo");
        funcionario.setCpf("390.533.447-05");
        funcionario.setEmail("marcelo@orion.com.br");
        funcionario.setSenha("e10adc3949ba59abbe56e057f20f883e");
        when(funcionarioRepository.findAll()).thenReturn(List.of(funcionario));

        mockMvc.perform(get("/api/funcionarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Marcelo"))
                .andExpect(jsonPath("$[0].email").value("marcelo@orion.com.br"))
                .andExpect(jsonPath("$[0].senha").doesNotExist());
    }

    @Test
    @DisplayName("Filtro por CPF continua funcionando e também não devolve a senha")
    void listarFuncionarios_ComFiltroDeCpf_NaoDevolveSenha() throws Exception {
        Funcionario funcionario = new Funcionario();
        funcionario.setCpf("390.533.447-05");
        funcionario.setSenha("e10adc3949ba59abbe56e057f20f883e");
        when(funcionarioRepository.findAll()).thenReturn(List.of(funcionario));

        mockMvc.perform(get("/api/funcionarios").param("cpf", "390.533"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].senha").doesNotExist());
    }
}
