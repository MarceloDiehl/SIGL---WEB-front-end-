function inicializarConsultaVendas() {
    const $input = $('#cod-pesquisa-venda');
    const $tbody = $('#tabela-vendas-body');
    const $resumo = $('#resumo-vendas');

    const metodosPagamento = { 1: 'Dinheiro', 2: 'Crédito', 3: 'Débito', 4: 'PIX' };

    const moeda = (v) => Number(v ?? 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    const dataBR = (iso) => iso ? iso.split('-').reverse().join('/') : '';
    const cpfBR = (c) => {
        const d = String(c ?? '').replace(/\D/g, '');
        return d.length === 11 ? d.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4') : d;
    };

    function carregarVendas(codigo = '') {
        $tbody.html('<tr><td colspan="10">Carregando...</td></tr>');
        $resumo.text('');

        // GET /api/vendas lista tudo; GET /api/vendas/busca?codigo= filtra por código do produto
        const requisicao = codigo
            ? $.getJSON('/api/vendas/busca', { codigo: codigo })
            : $.getJSON('/api/vendas');

        requisicao
            .done(function (data) {
                $tbody.empty();

                if (!data || data.length === 0) {
                    $tbody.append('<tr><td colspan="10"><span class="vazio-disco"><i class="bi bi-vinyl"></i>Nenhuma venda encontrada</span></td></tr>');
                    return;
                }

                let soma = 0;
                data.forEach(v => {
                    soma += Number(v.valorTotal ?? 0);
                    $tbody.append(`
                        <tr>
                            <td class="codigo">${v.id}</td>
                            <td class="codigo">${v.codProduto}</td>
                            <td class="titulo-celula">${v.tituloProduto ?? ''}</td>
                            <td class="text-end">${moeda(v.valorUnitario)}</td>
                            <td class="text-end">${v.quantidade}</td>
                            <td class="text-end fw-semibold">${moeda(v.valorTotal)}</td>
                            <td>${dataBR(v.dataVenda)}</td>
                            <td>#${v.idVendedor}</td>
                            <td class="codigo">${cpfBR(v.cpfNota) || '<span class="texto-suave">—</span>'}</td>
                            <td><span class="selo">${metodosPagamento[v.idMetodoPagamento] ?? v.idMetodoPagamento}</span></td>
                        </tr>
                    `);
                });

                $resumo.html(`${data.length} ${data.length === 1 ? 'item vendido' : 'itens vendidos'} · <strong>${moeda(soma)}</strong>`);
            })
            .fail(function () {
                $tbody.html('<tr><td colspan="10" class="text-danger">Erro ao carregar vendas</td></tr>');
            });
    }

    carregarVendas();

    let debounceTimer;
    $input.on('input', function () {
        clearTimeout(debounceTimer);
        const codigo = $(this).val().trim();
        debounceTimer = setTimeout(() => carregarVendas(codigo), 300);
    });
}
