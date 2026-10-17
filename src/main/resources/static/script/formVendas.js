let listaItensVenda = [];
let valorTotalVenda = 0;
let vendaEmAndamento = {};

const formatarMoeda = (valor) => {
    return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
};

function atualizarTabelaItensVenda() {
    const $tbody = $('#tabela-itens-venda-body');
    $tbody.empty();
    $('#btn-remover-item').prop('disabled', true);
    $('#tabela-itens-venda').toggleClass('vazia', listaItensVenda.length === 0);

    if (listaItensVenda.length === 0) {
        $tbody.append('<tr><td colspan="6"><span class="vazio-disco"><i class="bi bi-cart3"></i>Carrinho vazio. Adicione um produto pelo código.</span></td></tr>');
        $('#itens-lista-compras').val(0);
        $('#btn-avancar-venda').prop('disabled', true);
        $('#limpar-lista-venda').prop('disabled', true).attr('title', 'O carrinho está vazio');
        valorTotalVenda = 0;
    } else {
        listaItensVenda.forEach((item, index) => {
            const subtotal = item.produto.valor * item.quantidade;
            $tbody.append(`
                <tr data-index="${index}">
                    <td class="texto-suave">${index + 1}</td>
                    <td class="codigo">${escaparHtml(item.produto.codProduto)}</td>
                    <td class="titulo-celula">${escaparHtml(item.produto.titulo)}</td>
                    <td class="text-end">${formatarMoeda(item.produto.valor)}</td>
                    <td class="text-end">${item.quantidade}</td>
                    <td class="text-end fw-semibold">${formatarMoeda(subtotal)}</td>
                </tr>
            `);
        });
        $('#itens-lista-compras').val(listaItensVenda.length);
        $('#btn-avancar-venda').prop('disabled', false);
        $('#limpar-lista-venda').prop('disabled', false).removeAttr('title');
    }
    calcularValorTotal();
}

function calcularValorTotal() {
    valorTotalVenda = listaItensVenda.reduce((total, item) => {
        return total + (item.produto.valor * item.quantidade);
    }, 0);
    $('#valor-total-display').html(`<span class="texto-suave fw-normal fs-6 me-2">Total</span><strong>${formatarMoeda(valorTotalVenda)}</strong>`);
}

// Modal de confirmação do carrinho (no lugar de confirm())
function confirmarNoCarrinho({ titulo, mensagem, resumo, botao, aoConfirmar }) {
    const $modal = $('#modal-confirmar-carrinho');
    $('#confirmar-carrinho-titulo').text(titulo);
    $('#confirmar-carrinho-mensagem').text(mensagem);
    $('#confirmar-carrinho-resumo').html(resumo || '').toggle(!!resumo);
    $('#btn-confirmar-carrinho span').text(botao);
    $('#btn-confirmar-carrinho').off('click').on('click', function () {
        aoConfirmar();
        bootstrap.Modal.getOrCreateInstance($modal[0]).hide();
    });
    bootstrap.Modal.getOrCreateInstance($modal[0]).show();
}

// Mensagem de erro logo abaixo do campo (no lugar de alert)
function caixaDoCampo($campo) {          // campo numérico fica dentro de .campo-numero (setas próprias)
    return $campo.parent('.campo-numero').length ? $campo.parent() : $campo;
}

function mostrarErroCampo($campo, mensagem) {
    limparErroCampo($campo);
    $campo.addClass('is-invalid');
    $('<div class="invalid-feedback erro-campo" role="alert"></div>').text(mensagem).insertAfter(caixaDoCampo($campo));
    $campo.trigger('focus');
}

function limparErroCampo($campo) {
    $campo.removeClass('is-invalid');
    caixaDoCampo($campo).siblings('.erro-campo').remove();
}

function formularioVenda() {

    // o erro some assim que a pessoa corrige o campo
    $(document).on('input change', '#cod-produto-venda, #qtd-produto-venda, #cod-vendedor-conclusao', function () {
        limparErroCampo($(this));
    });
    $(document).on('change', 'input[name="forma-pagamento"]', function () {
        $('.opcoes-cartao').siblings('.erro-campo').remove();
    });


    $('#cpf-cliente-conclusao').mask('000.000.000-00');
    $('#cod-vendedor-conclusao').mask('00000');


    $('#formulario-venda-produto').validate({
        rules: {
            'cod-produto-estoque': { required: true, minlength: 6, maxlength: 6 },
            'qtd-produto-estoque': { required: true, digits: true, min: 1 },
        },
        messages: {
            'cod-produto-estoque': {
                required: "Informe o código do produto.",
                minlength: "O código possui 6 caracteres",
                maxlength: "O código possui 6 caracteres"
            },
            'qtd-produto-estoque': {
                required: "Informe a quantidade do produto.",
                digits: "A quantidade deve ser um número inteiro.",
                min: "A quantidade deve ser maior ou igual a 1."
            },
        },
        submitHandler: function (form, event) {
            event.preventDefault();

            const $codigo = $('#cod-produto-venda');
            const $quantidade = $('#qtd-produto-venda');
            const codProduto = $codigo.val();
            const quantidadeDesejada = parseInt($quantidade.val());
            limparErroCampo($codigo);
            limparErroCampo($quantidade);

            $.getJSON(`/api/produtos/codigo/${codProduto}`)
                .done(function (produto) {
                    if (!produto) {
                        mostrarErroCampo($codigo, "Produto não encontrado.");
                        return;
                    }

                    if (produto.quantidade < quantidadeDesejada) {
                        mostrarErroCampo($quantidade, produto.quantidade > 0
                            ? `Só há ${produto.quantidade} ${produto.quantidade === 1 ? 'unidade' : 'unidades'} em estoque.`
                            : 'Produto sem estoque.');
                        return;
                    }

                    const itemExistenteIndex = listaItensVenda.findIndex(item => item.produto.codProduto === codProduto);

                    if (itemExistenteIndex !== -1) {
                        const novoTotalQtd = listaItensVenda[itemExistenteIndex].quantidade + quantidadeDesejada;
                        if (produto.quantidade < novoTotalQtd) {
                             const noCarrinho = listaItensVenda[itemExistenteIndex].quantidade;
                             const restante = Math.max(produto.quantidade - noCarrinho, 0);
                             mostrarErroCampo($quantidade, restante === 0
                                 ? `Todo o estoque deste produto (${produto.quantidade}) já está no carrinho.`
                                 : `Já há ${noCarrinho} no carrinho; dá para adicionar no máximo ${restante}.`);
                             return;
                        }
                        listaItensVenda[itemExistenteIndex].quantidade = novoTotalQtd;
                    } else {
                        listaItensVenda.push({
                            produto: produto,
                            quantidade: quantidadeDesejada
                        });
                    }

                    $('#codigo-produto-display').val(produto.codProduto);
                    $('#descricao-produto-display').val(produto.titulo);

                    atualizarTabelaItensVenda();

                    $('#cod-produto-venda').val('').trigger('focus');
                    $('#qtd-produto-venda').val(1);

                })
                .fail(function (xhr, status, error) {
                    const errorMsg = xhr.responseJSON ? (xhr.responseJSON.detail || xhr.responseJSON.message || JSON.stringify(xhr.responseJSON)) : xhr.responseText;
                    mostrarErroCampo($codigo, xhr.status === 404
                        ? "Produto não encontrado."
                        : "Não foi possível buscar o produto. " + (errorMsg || ''));
                });
        },
        errorElement: 'div',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            element.closest('.mb-3').append(error);
        },
        highlight: function (element) {
            $(element).addClass('is-invalid');
        },
        unhighlight: function (element) {
            $(element).removeClass('is-invalid');
        }
    });

    $('#btn-remover-item').on('click', function () {
        const $selectedRow = $('#tabela-itens-venda-body tr.table-active');
        if ($selectedRow.length === 0) {
            return; // o botão só fica ativo com um item selecionado
        }

        const indexToRemove = parseInt($selectedRow.data('index'));
        const item = listaItensVenda[indexToRemove];
        const $resumo = $('<div>');
        $('<strong>').text(item.produto.titulo).appendTo($resumo);
        $('<span class="contador">').text(`Código ${item.produto.codProduto} · ${item.quantidade} ${item.quantidade === 1 ? 'unidade' : 'unidades'} · ${formatarMoeda(item.produto.valor * item.quantidade)}`).appendTo($resumo);
        confirmarNoCarrinho({
            titulo: 'Remover item do carrinho?',
            mensagem: 'O produto sai do carrinho desta venda. Você pode adicioná-lo de novo pelo código.',
            resumo: $resumo.html(),
            botao: 'Remover item',
            aoConfirmar: function () {
                listaItensVenda.splice(indexToRemove, 1);
                atualizarTabelaItensVenda();
            }
        });
    });

    $('#tabela-itens-venda-body').on('click', 'tr[data-index]', function () {
        $('#tabela-itens-venda-body tr').removeClass('table-active');
        $(this).addClass('table-active');
        $('#btn-remover-item').prop('disabled', false);
    });

    $('#limpar-lista-venda').on('click', function () {
        if (listaItensVenda.length === 0) return;
        const unidades = listaItensVenda.reduce((t, item) => t + item.quantidade, 0);
        const $resumo = $('<div>');
        $('<strong>').text(`${listaItensVenda.length} ${listaItensVenda.length === 1 ? 'produto' : 'produtos'} · ${unidades} ${unidades === 1 ? 'unidade' : 'unidades'}`).appendTo($resumo);
        $('<span class="contador">').text(`Total ${formatarMoeda(valorTotalVenda)}`).appendTo($resumo);
        confirmarNoCarrinho({
            titulo: 'Limpar o carrinho?',
            mensagem: 'Todos os itens desta venda serão removidos. Esta ação não pode ser desfeita.',
            resumo: $resumo.html(),
            botao: 'Limpar carrinho',
            aoConfirmar: function () {
                listaItensVenda = [];
                atualizarTabelaItensVenda();
                $('#codigo-produto-display').val('');
                $('#descricao-produto-display').val('');
                $('#cod-produto-venda').trigger('focus');
            }
        });
    });

    $('#modal-concluir-venda').on('show.bs.modal', function () {
        if (listaItensVenda.length === 0) {
            avisar("Adicione pelo menos um produto ao carrinho para continuar.", "info");
            return false;
        }
        $('#valor-total-conclusao-venda').val(formatarMoeda(valorTotalVenda));
        const idUsuario = $('meta[name="usuario-id"]').attr('content');
        if (idUsuario && !$('#cod-vendedor-conclusao').val()) {
            $('#cod-vendedor-conclusao').val(idUsuario);
        }
    });

    $('#btn-avancar-pagamento').on('click', function () {
        const $vendedor = $('#cod-vendedor-conclusao');
        limparErroCampo($vendedor);
        if (!$vendedor.val()) {
            mostrarErroCampo($vendedor, "Informe o código do vendedor.");
            return;
        }
        vendaEmAndamento.cpfNota = $('#cpf-cliente-conclusao').val().replace(/\D/g, '');
        vendaEmAndamento.idVendedor = parseInt($vendedor.val());

        // só abre o pagamento depois de validar os dados
        bootstrap.Modal.getOrCreateInstance('#modal-concluir-venda').hide();
        bootstrap.Modal.getOrCreateInstance('#modal-pagamento-venda').show();
    });

    // Valor digitado em reais ("1.200,50", "1200,50" ou "1200.50") -> número; vazio ou inválido -> null
    function lerValorEmReais(texto) {
        let limpo = String(texto || '').trim().replace(/\s|R\$/g, '');
        if (limpo.includes(',')) limpo = limpo.replace(/\./g, '').replace(',', '.');
        const valor = Number(limpo);
        return limpo === '' || isNaN(valor) ? null : Math.round(valor * 100) / 100;
    }

    const ehDinheiro = () => $('input[name="forma-pagamento"]:checked').val() === '1';

    // o troco mostrado aqui é só uma prévia: o valor oficial é calculado pelo servidor
    function atualizarTroco() {
        const recebido = lerValorEmReais($('#valor-recebido').val());
        const troco = recebido === null ? null : recebido - valorTotalVenda;
        $('#valor-troco').html(`<strong>${troco === null || troco < 0 ? '—' : formatarMoeda(troco)}</strong>`);
    }

    function mostrarCamposDoPagamento() {
        $('#pagamento-dinheiro-campos').prop('hidden', !ehDinheiro());
        $('#erro-venda').prop('hidden', true);
    }

    // o total a receber é sempre preenchido ao abrir o pagamento (inclusive ao voltar e avançar de novo)
    $('#modal-pagamento-venda').on('show.bs.modal', function () {
        $('#valor-total-pagamento').val(formatarMoeda(valorTotalVenda));
        $('#valor-recebido').val('');
        limparErroCampo($('#valor-recebido'));
        atualizarTroco();
        mostrarCamposDoPagamento();
    });
    $('input[name="forma-pagamento"]').on('change', mostrarCamposDoPagamento);
    $('#valor-recebido').on('input', function () {
        limparErroCampo($(this));
        atualizarTroco();
    });

    function limparVenda() {
        listaItensVenda = [];
        valorTotalVenda = 0;
        vendaEmAndamento = {};
        atualizarTabelaItensVenda();
        $('#formulario-venda-produto')[0].reset();
        $('#codigo-produto-display').val('');
        $('#descricao-produto-display').val('');
        $('#cpf-cliente-conclusao').val('');
    }

    // Um único POST com código e quantidade de cada item. Preço, total, troco, data e baixa de
    // estoque ficam com o servidor, numa transação: ou a venda inteira é registrada, ou nada.
    $('#btn-concluir-venda').on('click', function () {
        const $botao = $(this);
        const $recebido = $('#valor-recebido');
        limparErroCampo($recebido);
        $('#erro-venda').prop('hidden', true);

        const pedido = {
            idVendedor: vendaEmAndamento.idVendedor,
            idMetodoPagamento: parseInt($('input[name="forma-pagamento"]:checked').val()),
            cpfNota: vendaEmAndamento.cpfNota || null,
            itens: listaItensVenda.map(item => ({ codProduto: item.produto.codProduto, quantidade: item.quantidade }))
        };
        if (ehDinheiro()) {
            const recebido = lerValorEmReais($recebido.val());
            if (recebido === null) {
                mostrarErroCampo($recebido, 'Informe o valor recebido.');
                return;
            }
            if (recebido < valorTotalVenda) {
                mostrarErroCampo($recebido, 'O valor recebido é menor que o total.');
                return;
            }
            pedido.valorRecebido = recebido;
        }

        $botao.prop('disabled', true).find('span').text('Registrando...');
        $.ajax({
            url: '/api/vendas',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(pedido)
        })
            .done(function (venda) {
                $('#venda-finalizada-numero').text(venda.id);
                $('#venda-finalizada-total').text(formatarMoeda(Number(venda.valorTotal)));
                $('#venda-finalizada-troco')
                    .text(venda.troco != null ? 'Troco: ' + formatarMoeda(Number(venda.troco)) : '')
                    .prop('hidden', venda.troco == null);
                limparVenda();
                // o modal de sucesso só abre depois que o de pagamento terminou de fechar
                $('#modal-pagamento-venda').one('hidden.bs.modal', function () {
                    bootstrap.Modal.getOrCreateInstance('#modal-pagamento-finalizado').show();
                });
                bootstrap.Modal.getOrCreateInstance('#modal-pagamento-venda').hide();
            })
            .fail(function (xhr) {
                $('#erro-venda').text(mensagemDoServidor(xhr, 'Não foi possível registrar a venda.')).prop('hidden', false);
            })
            .always(function () {
                $botao.prop('disabled', false).find('span').text('Concluir venda');
            });
    });
    atualizarTabelaItensVenda();
}
