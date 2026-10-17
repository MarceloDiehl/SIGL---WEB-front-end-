package com.sigl.sigl.controller;

import com.sigl.sigl.dto.NovaVendaPedido;
import com.sigl.sigl.dto.VendaResposta;
import com.sigl.sigl.model.Venda;
import com.sigl.sigl.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    /** Vendas da mais recente para a mais antiga; com ?codigo=, só as que têm aquele produto. */
    @GetMapping
    public List<VendaResposta> listar(@RequestParam(value = "codigo", required = false) String codigo) {
        return vendaService.listar(codigo).stream().map(VendaResposta::de).toList();
    }

    @GetMapping("/{id}")
    public VendaResposta buscar(@PathVariable Integer id) {
        return VendaResposta.de(vendaService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<VendaResposta> registrar(@Valid @RequestBody NovaVendaPedido pedido) {
        Venda venda = vendaService.registrar(pedido);
        URI endereco = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(venda.getId()).toUri();
        return ResponseEntity.created(endereco).body(VendaResposta.de(venda));
    }

    @PostMapping("/{id}/cancelar")
    public VendaResposta cancelar(@PathVariable Integer id) {
        return VendaResposta.de(vendaService.cancelar(id));
    }
}
