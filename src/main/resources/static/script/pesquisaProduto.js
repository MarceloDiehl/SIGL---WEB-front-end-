function inicializarPesquisarProduto() {
    console.log("Inicializando pesquisa de produto...");

    const $codigoInput = $('#codigo-produto');
    const $tituloInput = $('#titulo-produto');
    const $descricaoTextarea = $('#observacoes');
    const $quantidadeInput = $('#estoque-produto');
    const $valorSpan = $('#valor-unitario');

    const $tipo = $('#tipo-produto-consulta');
    const $condicao = $('#condicao-produto-consulta');

    // Nomes do tipo e da condição vêm do banco (apoio()); o ícone do tipo, de iconeDoTipo()
    function mostrarTipo(idTipo, idCondicao) {
        apoio().then(function (listas) {
            const temProduto = idTipo != null;
            $tipo.text(temProduto ? descricaoDe(listas.tipos, idTipo) : '—').toggleClass('sem-valor', !temProduto);
            $condicao.text(idCondicao != null ? descricaoDe(listas.condicoes, idCondicao) : '—')
                .toggleClass('sem-valor', idCondicao == null);
            $('.consulta-linha').toggleClass('com-produto', temProduto);

            // Grafismo de fundo: lupa sem produto, ícone do tipo com produto (com animação ao trocar)
            const classe = temProduto ? iconeDoTipo(idTipo) : 'bi-search';
            const $marca = $('#consulta-marca-dagua');
            if (!$marca.find('i').hasClass(classe)) {
                $marca.removeClass('surgir').html(`<i class="bi ${classe}"></i>`);
                void $marca[0].offsetWidth;   // reinicia a animação
                $marca.addClass('surgir');
            }
        });
    }

    function mostrarEstoque(qtd) {
        const $ficha = $('#ficha-estoque').removeClass('estoque-ok estoque-baixo estoque-zerado');
        const $status = $('#estoque-status').attr('class', 'ficha-status').text('');
        if (qtd === null || qtd === undefined || qtd === '') return;
        if (qtd === 0) { $ficha.addClass('estoque-zerado'); $status.addClass('selo selo-perigo').text('Esgotado'); }
        else if (qtd <= 3) { $ficha.addClass('estoque-baixo'); $status.addClass('selo selo-alerta').text('Estoque baixo'); }
        else { $ficha.addClass('estoque-ok'); $status.addClass('selo selo-ok').text('Disponível'); }
    }

    function limparCampos() {
        mostrarEstoque(null);
        mostrarTipo(null, null);
        $tituloInput.val('');
        $descricaoTextarea.val('');
        $quantidadeInput.val('');
        $valorSpan.html('<strong>—</strong>');
    }

    limparCampos();

    $('#limpar-pesquisa-produto').off('click').on('click', function () {
        $codigoInput.val('').trigger('focus');
        limparCampos();
    });

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
                mostrarTipo(produto.idTipo, produto.idCondicao);
                $descricaoTextarea.val(produto.descricao ?? '');
                $quantidadeInput.val(produto.quantidade ?? '');
                mostrarEstoque(produto.quantidade);
                const valorFormatado = (produto.valor !== null && produto.valor !== undefined)
                    ? parseFloat(produto.valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
                    : 'R$ 0,00';
                $valorSpan.html(`<strong>${valorFormatado}</strong>`);
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
