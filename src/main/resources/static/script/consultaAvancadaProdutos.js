function inicializarConsultaAvancadaProdutos() {
    const $input = $('#cod_pesquisa');
    const $tbody = $('#tabela-produtos-body');
    const $btnExcluir = $('#excluir_produto');
    const $btnAtualizar = $('#atualizar_produto');

    let produtoSelecionado = null;

    function carregarProdutos(codigo = '') {
        $tbody.html('<tr><td colspan="8" class="text-center">Carregando produtos...</td></tr>');

        const url = codigo ? '/api/produtos/busca' : '/api/produtos';
        const params = codigo ? { codigo: codigo } : {};

        $.getJSON(url, params)
            .done(function (data) {
                $tbody.empty();

                if (!data || data.length === 0) {
                    $tbody.append('<tr><td colspan="8" class="text-center">Nenhum produto encontrado</td></tr>');
                    return;
                }

                data.forEach(p => {
                    $tbody.append(`
                        <tr data-codigo="${p.codProduto}">
                            <td>${p.codProduto}</td>
                            <td>${p.descricao ?? ''}</td>
                            <td>${p.quantidade ?? ''}</td>
                            <td>R$ ${p.valor?.toFixed(2) ?? '0,00'}</td>
                            <td>${p.idTipo ?? ''}</td>
                            <td>${p.idCondicao ?? ''}</td>
                            <td>${p.titulo ?? ''}</td>
                            <td>${p.entrada ?? ''}</td>
                        </tr>
                    `);
                });

                $tbody.find('tr').off('click').on('click', function () {
                    $tbody.find('tr').removeClass('table-active');
                    $(this).addClass('table-active');
                    produtoSelecionado = $(this).data('codigo');

                    $btnExcluir.prop('disabled', false);
                    $btnAtualizar.prop('disabled', false);
                });
            })
            .fail(function () {
                $tbody.html('<tr><td colspan="8" class="text-danger text-center">Erro ao carregar produtos</td></tr>');
            });
    }

    carregarProdutos();

    let debounceTimer;
    $input.on('input', function () {
        clearTimeout(debounceTimer);
        const codigo = $(this).val().trim();
        debounceTimer = setTimeout(() => carregarProdutos(codigo), 300);
    });

    $btnExcluir.prop('disabled', true);
    $btnAtualizar.prop('disabled', true);

    $btnExcluir.off('click').on('click', function () {
        if (!produtoSelecionado) return alert("Selecione um produto antes de excluir.");
        if (!confirm(`Tem certeza que deseja excluir o produto código ${produtoSelecionado}?`)) return;

        $.ajax({
            url: `/api/produtos/${produtoSelecionado}`,
            method: 'DELETE'
        })
        .done(function (msg) {
            alert(msg);
            carregarProdutos();
            $btnExcluir.prop('disabled', true);
            $btnAtualizar.prop('disabled', true);
            produtoSelecionado = null;
        })
        .fail(function (xhr) {
            alert("Erro ao excluir produto: " + xhr.responseText);
        });
    });

    $btnAtualizar.off('click').on('click', function () {
        if (!produtoSelecionado) return alert("Selecione um produto antes de atualizar.");

        $.getJSON(`/api/produtos/codigo/${produtoSelecionado}`)
            .done(function (produto) {
                // Preenche modal
                $('#codigo-produto-modal').val(produto.codProduto);
                $('#quantidade-modal').val(produto.quantidade);
                $('#valor-modal').val(produto.valor);
                $('#modalAtualizarProduto').modal('show');
            })
            .fail(function () {
                alert("Erro ao buscar produto para atualização.");
            });
    });

    $('#formAtualizarProduto').off('submit').on('submit', function(e) {
        e.preventDefault();

        const produtoAtualizado = {
            quantidade: parseInt($('#quantidade-modal').val()),
            valor: parseFloat($('#valor-modal').val().replace(',', '.'))
        };

        $.ajax({
            url: `/api/produtos/${produtoSelecionado}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(produtoAtualizado)
        })
        .done(function() {
            alert("Produto atualizado com sucesso!");
            $('#modalAtualizarProduto').modal('hide');
            carregarProdutos();
        })
        .fail(function(xhr) {
            alert("Erro ao atualizar produto: " + xhr.responseText);
        });
    });

    function cadastrarProduto(e) {
        e.preventDefault();

        const novoProduto = {
            codProduto: $('#cod_produto').val(),
            descricao: $('#descricao').val(),
            quantidade: $('#quantidade').val(),
            valor: parseFloat($('#valor').val()),
            idTipo: $('#id_tipo').val(),
            idCondicao: $('#id_condicao').val(),
            titulo: $('#titulo').val(),
            entrada: $('#entrada').val()
        };

        $.ajax({
            url: '/api/produtos',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(novoProduto)
        })
        .done(function () {
            alert("Produto cadastrado com sucesso!");
            $('#form_produto')[0].reset();
            carregarProdutos();
        })
        .fail(function (xhr) {
            alert("Erro ao cadastrar produto: " + xhr.responseText);
        });
    }

    // Bind inicial
    $('#btn_cadastrar').off('click').on('click', cadastrarProduto);
}
