package com.sigl.sigl.service;

import com.sigl.sigl.model.Cliente;
import com.sigl.sigl.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ValidatorService validatorService;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, ValidatorService validatorService) {
        this.clienteRepository = clienteRepository;
        this.validatorService = validatorService;
    }

    public Cliente salvar(Cliente cliente) {
        validarCliente(cliente);
        return clienteRepository.save(cliente);
    }

    private void validarCliente(Cliente cliente) {
        validatorService.validarCpf(cliente.getCpf());
        validatorService.validarCep(cliente.getCep());
        validatorService.validarTelefone(cliente.getTelefone());
    }
}