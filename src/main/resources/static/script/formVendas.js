let listaItensVenda = [];
let valorTotalVenda = 0;
let vendaEmAndamento = {};

const formatarMoeda = (valor) => {
    return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
};

function atualizarTabelaItensVenda() {
    const $tbody = $('#tabela-itens-venda-body');
    $tbody.empty();

    if (listaItensVenda.length === 0) {
        $tbody.append('<tr><td colspan="6" class="text-center">Nenhum item na lista</td></tr>');
        $('#itens-lista-compras').val(0);
        $('#btn-avancar-venda').prop('disabled', true);
        valorTotalVenda = 0;
    } else {
        listaItensVenda.forEach((item, index) => {
            const subtotal = item.produto.valor * item.quantidade;
            $tbody.append(`
                <tr data-index="${index}">
                    <td>${index + 1}</td>
                    <td>${item.produto.codProduto}</td>
                    <td>${item.produto.titulo}</td>
                    <td>${formatarMoeda(item.produto.valor)}</td>
                    <td>${item.quantidade}</td>
                    <td>${formatarMoeda(subtotal)}</td>
                </tr>
            `);
        });
        $('#itens-lista-compras').val(listaItensVenda.length);
        $('#btn-avancar-venda').prop('disabled', false);
    }
    calcularValorTotal();
}

function calcularValorTotal() {
    valorTotalVenda = listaItensVenda.reduce((total, item) => {
        return total + (item.produto.valor * item.quantidade);
    }, 0);
    $('#valor-total-display').html(`<strong>Valor Total: ${formatarMoeda(valorTotalVenda)}</strong>`);
}

function formularioVenda() {


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

            const codProduto = $('#cod-produto-venda').val();
            const quantidadeDesejada = parseInt($('#qtd-produto-venda').val());

            $.getJSON(`/api/produtos/codigo/${codProduto}`)
                .done(function (produto) {
                    if (!produto) {
                        alert("Produto não encontrado.");
                        return;
                    }

                    if (produto.quantidade < quantidadeDesejada) {
                        alert(`Quantidade insuficiente em estoque para o produto ${produto.titulo}. Disponível: ${produto.quantidade}.`);
                        return;
                    }

                    const itemExistenteIndex = listaItensVenda.findIndex(item => item.produto.codProduto === codProduto);

                    if (itemExistenteIndex !== -1) {
                        const novoTotalQtd = listaItensVenda[itemExistenteIndex].quantidade + quantidadeDesejada;
                        if (produto.quantidade < novoTotalQtd) {
                             alert(`Quantidade total desejada (${novoTotalQtd}) excede o estoque disponível (${produto.quantidade}) para o produto ${produto.titulo}.`);
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

                    $('#cod-produto-venda').val('');
                    $('#qtd-produto-venda').val('');

                })
                .fail(function (xhr, status, error) {
                    const errorMsg = xhr.responseJSON ? (xhr.responseJSON.detail || xhr.responseJSON.message || JSON.stringify(xhr.responseJSON)) : xhr.responseText;
                    alert("Erro ao buscar produto: " + errorMsg);
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
            alert("Selecione um item da lista para remover.");
            return;
        }

        const indexToRemove = parseInt($selectedRow.data('index'));
        if (confirm(`Tem certeza que deseja remover o item na posição ${indexToRemove + 1} da lista?`)) {
            listaItensVenda.splice(indexToRemove, 1);
            atualizarTabelaItensVenda();
        }
    });

    $('#tabela-itens-venda-body').on('click', 'tr', function () {
        $('#tabela-itens-venda-body tr').removeClass('table-active');
        $(this).addClass('table-active');
    });

    $('#limpar-lista-venda').on('click', function() {
        if (confirm('Tem certeza que deseja limpar todos os itens da lista de compras?')) {
            listaItensVenda = [];
            atualizarTabelaItensVenda();
            $('#codigo-produto-display').val('');
            $('#descricao-produto-display').val('');
            alert('Lista de compras limpa!');
        }
    });

    $('#modal-concluir-venda').on('show.bs.modal', function () {
        if (listaItensVenda.length === 0) {
            alert("Não é possível avançar. A lista de compras está vazia.");
            return false;
        }
        $('#valor-total-conclusao-venda').val(formatarMoeda(valorTotalVenda));
        $('#data-conclusao').val(new Date().toISOString().split('T')[0]);
    });

    $('#btn-avancar-pagamento').on('click', function () {
        const cpfCliente = $('#cpf-cliente-conclusao').val().replace(/\D/g, '');
        const codVendedor = $('#cod-vendedor-conclusao').val();
        const dataVenda = $('#data-conclusao').val();

        if (!codVendedor) {
            alert("O Código do Vendedor é obrigatório.");
            $('#cod-vendedor-conclusao').focus();
            return;
        }
        if (!dataVenda) {
            alert("A Data da Venda é obrigatória.");
            $('#data-conclusao').focus();
            return;
        }
        vendaEmAndamento.cpfNota = cpfCliente;
        vendaEmAndamento.idVendedor = parseInt(codVendedor);
        vendaEmAndamento.dataVenda = dataVenda;
        vendaEmAndamento.valorTotal = valorTotalVenda;

        $('#valor-total-pagamento').val(formatarMoeda(valorTotalVenda));
    });
    $('#btn-concluir-venda').on('click', function () {
        const metodoPagamento = $('input[name="forma-pagamento"]:checked').val();
        if (!metodoPagamento) {
            alert("Selecione uma forma de pagamento.");
            return;
        }
        vendaEmAndamento.idMetodoPagamento = parseInt(metodoPagamento);

        const promises = [];
        let estoqueAtualizadoComSucesso = true;

        for (const item of listaItensVenda) {
            const promiseAtualizarEstoque = $.ajax({
                url: `/api/produtos/${item.produto.codProduto}/removerQuantidade?quantidade=${item.quantidade}`,
                method: 'PATCH',
                contentType: 'application/json'
            }).fail(function(xhr) {
                estoqueAtualizadoComSucesso = false;
                console.error(`Falha ao dar baixa no estoque do produto ${item.produto.titulo}:`, xhr.responseText);
                alert(`Erro ao dar baixa no estoque do produto ${item.produto.titulo}. Venda não concluída.`);
            });
            promises.push(promiseAtualizarEstoque);

            const vendaItem = {
                codProduto: item.produto.codProduto,
                tituloProduto: item.produto.titulo,
                valorUnitario: item.produto.valor,
                quantidade: item.quantidade,
                valorTotal: item.produto.valor * item.quantidade,
                dataVenda: vendaEmAndamento.dataVenda,
                idVendedor: vendaEmAndamento.idVendedor,
                cpfNota: vendaEmAndamento.cpfNota,
                idMetodoPagamento: vendaEmAndamento.idMetodoPagamento
            };
            const promiseRegistrarVenda = $.ajax({
                url: '/api/vendas',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(vendaItem)
            }).fail(function(xhr) {
                console.error(`Falha ao registrar venda do produto ${item.produto.titulo}:`, xhr.responseText);
            });
            promises.push(promiseRegistrarVenda);
        }
        Promise.all(promises)
            .then(() => {
                if (estoqueAtualizadoComSucesso) {

                    listaItensVenda = [];
                    valorTotalVenda = 0;
                    vendaEmAndamento = {};
                    atualizarTabelaItensVenda();
                    $('#formulario-venda-produto')[0].reset();
                    $('#codigo-produto-display').val('');
                    $('#descricao-produto-display').val('');
                } else {
                    alert("Algumas operações falharam. Verifique o console para detalhes.");
                }
            })
            .catch((error) => {
                console.error("Erro geral durante a conclusão da venda:", error);
                alert("Ocorreu um erro inesperado ao concluir a venda.");
            });
    });
    atualizarTabelaItensVenda();
}