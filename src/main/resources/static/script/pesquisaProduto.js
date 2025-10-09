function inicializarPesquisarProduto() {
    console.log("Inicializando pesquisa de produto...");

    const $codigoInput = $('#codigo-produto');
    const $tituloInput = $('#titulo-produto');
    const $descricaoTextarea = $('#observacoes');
    const $quantidadeInput = $('#estoque-produto');
    const $valorSpan = $('#valor-unitario');

    function limparCampos() {
        $tituloInput.val('');
        $descricaoTextarea.val('');
        $quantidadeInput.val('');
        $valorSpan.html('<strong>Valor do Produto: R$0,00</strong>');
    }

    limparCampos();

    $codigoInput.off('input').on('input', function () {
        const codProduto = $(this).val().trim();

        if (codProduto.length !== 6) {
            limparCampos();
            return;
        }

        console.log(`Buscando produto com código: ${codProduto}`);
        $.ajax({
            url: `/api/produtos/codigo/${codProduto}`,
            method: 'GET',
            dataType: 'json'
        })
        .done(function (produto) {
            if (produto) {
                console.log("Produto encontrado:", produto);
                $tituloInput.val(produto.titulo ?? '');
                $descricaoTextarea.val(produto.descricao ?? '');
                $quantidadeInput.val(produto.quantidade ?? '');
                const valorFormatado = (produto.valor !== null && produto.valor !== undefined)
                    ? parseFloat(produto.valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
                    : 'R$ 0,00';
                $valorSpan.html(`<strong>Valor do Produto: ${valorFormatado}</strong>`);
            } else {
                console.log("Nenhum produto encontrado para o código:", codProduto);
                limparCampos();
            }
        })
        .fail(function (xhr, status, error) {
            console.error('Erro na busca do produto:', status, error, xhr);
            limparCampos();
        });
    });

    $codigoInput.off('blur').on('blur', function() {
        if ($(this).val().trim().length !== 6) {
            limparCampos();
        }
    });
}