function inicializarConsultaVendas() {
    const $input = $('#cod-pesquisa-venda');
    const $tbody = $('#tabela-vendas-body');

    function carregarVendas(codigo = '') {
        $tbody.html('<tr><td colspan="10" class="text-center">Carregando...</td></tr>');

        $.getJSON('/api/vendas', { codProduto: codigo })
            .done(function (data) {
                $tbody.empty();

                if (!data || data.length === 0) {
                    $tbody.append('<tr><td colspan="10" class="text-center">Nenhuma venda encontrada</td></tr>');
                    return;
                }

                data.forEach(v => {
                    $tbody.append(`
                        <tr>
                            <td>${v.id}</td>
                            <td>${v.codProduto}</td>
                            <td>${v.tituloProduto}</td>
                            <td>R$ ${v.valorUnitario.toFixed(2)}</td>
                            <td>${v.quantidade}</td>
                            <td>R$ ${v.valorTotal.toFixed(2)}</td>
                            <td>${v.dataVenda}</td>
                            <td>${v.idVendedor}</td>
                            <td>${v.cpfNota ?? ''}</td>
                            <td>${v.idMetodoPagamento}</td>
                        </tr>
                    `);
                });
            })
            .fail(function () {
                $tbody.html('<tr><td colspan="10" class="text-danger text-center">Erro ao carregar vendas</td></tr>');
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
