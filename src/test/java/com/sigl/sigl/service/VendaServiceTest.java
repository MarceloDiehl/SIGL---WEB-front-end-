package com.sigl.sigl.service;

import com.sigl.sigl.dto.NovaVendaPedido;
import com.sigl.sigl.exception.RegraDeNegocioException;
import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.model.MetodoPagamento;
import com.sigl.sigl.model.Produto;
import com.sigl.sigl.model.StatusVenda;
import com.sigl.sigl.model.Venda;
import com.sigl.sigl.repository.FuncionarioRepository;
import com.sigl.sigl.repository.MetodoPagamentoRepository;
import com.sigl.sigl.repository.ProdutoRepository;
import com.sigl.sigl.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;
    @Mock
    private MetodoPagamentoRepository metodoPagamentoRepository;

    private VendaService vendaService;

    private Produto abbeyRoad;
    private Produto camiseta;

    private static final int PIX = 4;

    @BeforeEach
    void preparar() {
        vendaService = new VendaService(vendaRepository, produtoRepository, funcionarioRepository,
                metodoPagamentoRepository, new ValidatorService());

        Funcionario vendedor = new Funcionario();
        vendedor.setId(3);
        vendedor.setNome("Marcelo");
        vendedor.setSobrenome("Diehl");
        when(funcionarioRepository.findById(3)).thenReturn(Optional.of(vendedor));
        when(metodoPagamentoRepository.findById(MetodoPagamento.DINHEIRO))
                .thenReturn(Optional.of(new MetodoPagamento(MetodoPagamento.DINHEIRO, "Dinheiro")));
        when(metodoPagamentoRepository.findById(PIX)).thenReturn(Optional.of(new MetodoPagamento(PIX, "PIX")));

        abbeyRoad = new Produto("132437", "Abbey Road", "LP", new BigDecimal("150.00"), 10, 1, 1, LocalDate.now());
        camiseta = new Produto("400150", "Camiseta Orion", "M", new BigDecimal("79.90"), 1, 5, 1, LocalDate.now());
        when(produtoRepository.buscarParaAtualizarEstoque("132437")).thenReturn(Optional.of(abbeyRoad));
        when(produtoRepository.buscarParaAtualizarEstoque("400150")).thenReturn(Optional.of(camiseta));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    private static NovaVendaPedido pedido(int metodo, String cpf, BigDecimal recebido, NovaVendaPedido.Item... itens) {
        return new NovaVendaPedido(3, metodo, cpf, recebido, List.of(itens));
    }

    private static NovaVendaPedido.Item item(String codigo, int quantidade) {
        return new NovaVendaPedido.Item(codigo, quantidade);
    }

    private void assertRecusada(NovaVendaPedido pedido, HttpStatus status, String mensagem) {
        RegraDeNegocioException erro = assertThrows(RegraDeNegocioException.class, () -> vendaService.registrar(pedido));
        assertEquals(status, erro.getStatus());
        assertEquals(mensagem, erro.getMessage());
        verify(vendaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Calcula o total com os preços do banco e baixa o estoque")
    void registrar_CalculaTotalEBaixaEstoque() {
        Venda venda = vendaService.registrar(pedido(PIX, null, null, item("132437", 2), item("400150", 1)));

        assertEquals(new BigDecimal("379.90"), venda.getValorTotal());
        assertEquals(8, abbeyRoad.getQuantidade());
        assertEquals(0, camiseta.getQuantidade());
        assertEquals(StatusVenda.CONCLUIDA, venda.getStatus());
        assertNotNull(venda.getDataHora());
        assertEquals(2, venda.getItens().size());
        verify(vendaRepository).save(venda);
    }

    @Test
    @DisplayName("Guarda título e preço do produto no item (fotografia da venda)")
    void registrar_GuardaFotografiaDoProduto() {
        Venda venda = vendaService.registrar(pedido(PIX, null, null, item("132437", 2)));
        abbeyRoad.setValor(new BigDecimal("999.00"));

        var itemVendido = venda.getItens().get(0);
        assertEquals("Abbey Road", itemVendido.getTituloProduto());
        assertEquals(new BigDecimal("150.00"), itemVendido.getValorUnitario());
        assertEquals(new BigDecimal("300.00"), itemVendido.getSubtotal());
    }

    @Test
    @DisplayName("O mesmo código em duas linhas vira um item só")
    void registrar_SomaCodigosRepetidos() {
        Venda venda = vendaService.registrar(pedido(PIX, null, null, item("132437", 1), item("132437", 2)));

        assertEquals(1, venda.getItens().size());
        assertEquals(3, venda.getItens().get(0).getQuantidade());
        assertEquals(7, abbeyRoad.getQuantidade());
    }

    @Test
    @DisplayName("Recusa com 409 quando falta estoque e não grava nada")
    void registrar_SemEstoque_Recusa() {
        assertRecusada(pedido(PIX, null, null, item("132437", 1), item("400150", 2)),
                HttpStatus.CONFLICT, "Só há 1 unidade de Camiseta Orion.");
    }

    @Test
    @DisplayName("Recusa produto inexistente")
    void registrar_ProdutoInexistente_Recusa() {
        when(produtoRepository.buscarParaAtualizarEstoque("999999")).thenReturn(Optional.empty());
        assertRecusada(pedido(PIX, null, null, item("999999", 1)),
                HttpStatus.BAD_REQUEST, "Produto 999999 não encontrado.");
    }

    @Test
    @DisplayName("Recusa vendedor inexistente antes de tocar no estoque")
    void registrar_VendedorInexistente_Recusa() {
        NovaVendaPedido pedido = new NovaVendaPedido(77, PIX, null, null, List.of(item("132437", 1)));
        assertRecusada(pedido, HttpStatus.BAD_REQUEST, "Vendedor nº 77 não encontrado.");
        verify(produtoRepository, never()).buscarParaAtualizarEstoque(any());
    }

    @Test
    @DisplayName("Dinheiro: guarda o valor recebido e calcula o troco")
    void registrar_Dinheiro_CalculaTroco() {
        Venda venda = vendaService.registrar(pedido(MetodoPagamento.DINHEIRO, null, new BigDecimal("200.00"), item("132437", 1)));

        assertEquals(new BigDecimal("200.00"), venda.getValorRecebido());
        assertEquals(new BigDecimal("50.00"), venda.getTroco());
    }

    @Test
    @DisplayName("Dinheiro: recusa valor recebido menor que o total")
    void registrar_DinheiroInsuficiente_Recusa() {
        assertRecusada(pedido(MetodoPagamento.DINHEIRO, null, new BigDecimal("100.00"), item("132437", 1)),
                HttpStatus.BAD_REQUEST, "O valor recebido é menor que o total da venda.");
    }

    @Test
    @DisplayName("CPF da nota é guardado só com dígitos")
    void registrar_CpfDaNota_GuardaSoDigitos() {
        Venda venda = vendaService.registrar(pedido(PIX, "529.982.247-25", null, item("132437", 1)));
        assertEquals("52998224725", venda.getCpfNota());
    }

    @Test
    @DisplayName("Recusa CPF da nota inválido")
    void registrar_CpfDaNotaInvalido_Recusa() {
        assertRecusada(pedido(PIX, "111.111.111-11", null, item("132437", 1)),
                HttpStatus.BAD_REQUEST, "CPF da nota inválido.");
    }

    @Test
    @DisplayName("Cancelar devolve o estoque; cancelar de novo é recusado")
    void cancelar_DevolveEstoque() {
        Venda venda = vendaService.registrar(pedido(PIX, null, null, item("132437", 2)));
        when(vendaRepository.findComItensById(1)).thenReturn(Optional.of(venda));

        vendaService.cancelar(1);
        assertEquals(StatusVenda.CANCELADA, venda.getStatus());
        assertNotNull(venda.getCanceladaEm());
        assertEquals(10, abbeyRoad.getQuantidade());

        RegraDeNegocioException erro = assertThrows(RegraDeNegocioException.class, () -> vendaService.cancelar(1));
        assertEquals(HttpStatus.CONFLICT, erro.getStatus());
        assertEquals(10, abbeyRoad.getQuantidade());
    }
}
