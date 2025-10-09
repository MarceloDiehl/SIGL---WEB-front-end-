package com.sigl.sigl.controller;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.repository.FuncionarioRepository;
import com.sigl.sigl.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService; // Injeta o Service

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @PostMapping
    public ResponseEntity<String> criarFuncionario(@RequestBody Funcionario funcionario) {
        try {
            funcionarioService.salvar(funcionario);
            return new ResponseEntity<>("Funcionário cadastrado com sucesso!", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao cadastrar funcionário: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public List<Funcionario> listarFuncionarios(@RequestParam(value = "cpf", required = false) String cpf) {
        if (cpf != null && !cpf.isEmpty()) {
            return funcionarioRepository.findAll()
                    .stream()
                    .filter(f -> f.getCpf().contains(cpf))
                    .toList();
        }
        return funcionarioRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public String excluirFuncionario(@PathVariable Integer id) {
        try {
            funcionarioRepository.deleteById(id);
            return "Funcionário excluído com sucesso!";
        } catch (Exception e) {
            return "Erro ao excluir funcionário: " + e.getMessage();
        }
    }
}

