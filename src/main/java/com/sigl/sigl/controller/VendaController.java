package com.sigl.sigl.controller;

import com.sigl.sigl.dto.NovaVendaPedido;
import com.sigl.sigl.dto.VendaResposta;
import com.sigl.sigl.model.Venda;
import com.sigl.sigl.seguranca.UsuarioLogado;
import com.sigl.sigl.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public List<VendaResposta> listar(@RequestParam(value = "codigo", required = false) String codigo,
                                     @AuthenticationPrincipal UsuarioLogado usuario) {
        return vendaService.listar(codigo).stream().map(v -> VendaResposta.de(v, usuario.isPodeGerir())).toList();
    }

    @GetMapping("/{id}")
    public VendaResposta buscar(@PathVariable Integer id, @AuthenticationPrincipal UsuarioLogado usuario) {
        return VendaResposta.de(vendaService.buscar(id), usuario.isPodeGerir());
    }

    @PostMapping
    public ResponseEntity<VendaResposta> registrar(@Valid @RequestBody NovaVendaPedido pedido,
                                                   @AuthenticationPrincipal UsuarioLogado usuario) {
        // a venda é sempre registrada em nome de quem está logado
        Venda venda = vendaService.registrar(pedido, usuario.getId());
        URI endereco = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(venda.getId()).toUri();
        return ResponseEntity.created(endereco).body(VendaResposta.de(venda, usuario.isPodeGerir()));
    }

    @PostMapping("/{id}/cancelar")
    public VendaResposta cancelar(@PathVariable Integer id, @AuthenticationPrincipal UsuarioLogado usuario) {
        return VendaResposta.de(vendaService.cancelar(id), usuario.isPodeGerir());
    }
}
