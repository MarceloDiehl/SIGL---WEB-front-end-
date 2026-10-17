function inicializarConsultaVendas() {
    const $input = $('#cod-pesquisa-venda');
    const $tbody = $('#tabela-vendas-body');
    const $resumo = $('#resumo-vendas');
    const $detalhes = $('#detalhes-venda');

    const moeda = (v) => Number(v ?? 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    // "2026-10-16T21:07:21" -> "16/10/2026 21:07"
    const dataHoraBR = (iso) => {
        if (!iso) return '';
        const [data, hora = ''] = String(iso).split('T');
        return data.split('-').reverse().join('/') + ' ' + hora.slice(0, 5);
    };
    const cpfBR = (c) => {
        if (String(c ?? '').includes('*')) return c;   // já veio mascarado do servidor (cargo sem acesso ao CPF)
        const d = String(c ?? '').replace(/\D/g, '');
        return d.length === 11 ? d.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4') : d;
    };
    const unidades = (n) => `${n} ${n === 1 ? 'unidade' : 'unidades'}`;
    const seloSituacao = (v) => v.status === 'CANCELADA'
        ? '<span class="selo selo-perigo">Cancelada</span>'
        : '<span class="selo selo-ok">Concluída</span>';

    const detalhesVazios = $detalhes.html();
    let vendas = [];
    let vendaSelecionada = null;

    function mostrarDetalhes(v) {
        vendaSelecionada = v ? v.id : null;
        if (!v) { $detalhes.html(detalhesVazios); return; }

        const campos = [
            ['Data e hora', dataHoraBR(v.dataHora)],
            ['Vendedor', `${v.vendedor} (#${v.idVendedor})`],
            ['CPF cliente', cpfBR(v.cpfNota) || '—'],
            ['Pagamento', v.metodoPagamento],
            ['Total', moeda(v.valorTotal)]
        ];
        if (v.valorRecebido != null) campos.push(['Recebido', moeda(v.valorRecebido)], ['Troco', moeda(v.troco)]);
        if (v.canceladaEm) campos.push(['Cancelada em', dataHoraBR(v.canceladaEm)]);

        const linhasItens = v.itens.map(i => `
            <tr>
                <td class="codigo">${escaparHtml(i.codProduto)}</td>
                <td class="titulo-celula">${escaparHtml(i.tituloProduto)}</td>
                <td class="text-end">${moeda(i.valorUnitario)}</td>
                <td class="text-end">${escaparHtml(i.quantidade)}</td>
                <td class="text-end fw-semibold">${moeda(i.subtotal)}</td>
            </tr>`).join('');

        $detalhes.html(`
            <div class="painel-cabecalho">
                <h2 class="painel-titulo">Venda nº ${escaparHtml(v.id)}</h2>
                <div class="d-flex align-items-center gap-2">
                    ${seloSituacao(v)}
                    ${v.status === 'CONCLUIDA' && usuarioPodeGerir()
                        ? '<button type="button" class="btn btn-perigo" id="cancelar-venda"><i class="bi bi-x-circle"></i>Cancelar venda</button>'
                        : ''}
                </div>
            </div>
            <div class="painel-corpo">
                <dl>${campos.map(([rotulo, valor]) => `<div><dt>${rotulo}</dt><dd>${escaparHtml(valor)}</dd></div>`).join('')}</dl>
                <div class="tabela-wrap">
                    <table class="table tabela">
                        <thead>
                        <tr>
                            <th>Código</th>
                            <th>Produto</th>
                            <th class="text-end">Valor unit.</th>
                            <th class="text-end">Qtd</th>
                            <th class="text-end">Subtotal</th>
                        </tr>
                        </thead>
                        <tbody class="num">${linhasItens}</tbody>
                    </table>
                </div>
            </div>
        `);
    }

    // Com ?codigo=, o servidor devolve só as vendas que têm um produto com esse código
    function carregarVendas(codigo = '', manterSelecao = false) {
        const selecionadaAntes = manterSelecao ? vendaSelecionada : null;
        $tbody.html('<tr><td colspan="8">Carregando...</td></tr>');
        $resumo.text('');

        $.getJSON('/api/vendas', codigo ? { codigo: codigo } : {})
            .done(function (data) {
                vendas = data || [];
                $tbody.empty();
                mostrarDetalhes(null);

                if (vendas.length === 0) {
                    $tbody.append('<tr><td colspan="8"><span class="vazio-disco"><i class="bi bi-vinyl"></i>Nenhuma venda encontrada</span></td></tr>');
                    return;
                }

                vendas.forEach((v, i) => {
                    $tbody.append(`
                        <tr data-indice="${i}" class="${v.status === 'CANCELADA' ? 'venda-cancelada' : ''}">
                            <td class="codigo">${escaparHtml(v.id)}</td>
                            <td>${dataHoraBR(v.dataHora)}</td>
                            <td>${escaparHtml(v.vendedor)}</td>
                            <td class="codigo">${escaparHtml(cpfBR(v.cpfNota)) || '<span class="texto-suave">—</span>'}</td>
                            <td><span class="selo">${escaparHtml(v.metodoPagamento)}</span></td>
                            <td class="text-end">${escaparHtml(v.quantidadeDeUnidades)}</td>
                            <td class="text-end fw-semibold valor-venda">${moeda(v.valorTotal)}</td>
                            <td>${seloSituacao(v)}</td>
                        </tr>
                    `);
                });

                const concluidas = vendas.filter(v => v.status === 'CONCLUIDA');
                const canceladas = vendas.length - concluidas.length;
                const soma = concluidas.reduce((t, v) => t + Number(v.valorTotal ?? 0), 0);
                $resumo.html(`${concluidas.length} ${concluidas.length === 1 ? 'venda' : 'vendas'} · <strong>${moeda(soma)}</strong>`
                    + (canceladas ? ` · ${canceladas} ${canceladas === 1 ? 'cancelada' : 'canceladas'}` : ''));

                if (selecionadaAntes != null) {
                    const i = vendas.findIndex(v => v.id === selecionadaAntes);
                    if (i >= 0) $tbody.find(`tr[data-indice="${i}"]`).trigger('click');
                }
            })
            .fail(function (xhr) {
                $tbody.html(`<tr><td colspan="8" class="text-danger">${escaparHtml(mensagemDoServidor(xhr, 'Erro ao carregar vendas'))}</td></tr>`);
            });
    }

    $tbody.on('click', 'tr[data-indice]', function () {
        $tbody.find('tr').removeClass('table-active');
        $(this).addClass('table-active');
        mostrarDetalhes(vendas[$(this).data('indice')]);
    });

    // Cancelar: devolve os itens ao estoque e mantém a venda no histórico como "Cancelada"
    $detalhes.on('click', '#cancelar-venda', function () {
        const v = vendas.find(x => x.id === vendaSelecionada);
        if (!v) return;
        const n = v.quantidadeDeUnidades;
        confirmarAcao({
            titulo: `Cancelar a venda nº ${v.id}?`,
            mensagem: `${unidades(n)} ${n === 1 ? 'volta' : 'voltam'} ao estoque e a venda fica registrada como cancelada. Esta ação não pode ser desfeita.`,
            botao: 'Confirmar cancelamento'
        }).then(function (sim) {
            if (!sim) return;
            $.ajax({ url: `/api/vendas/${v.id}/cancelar`, method: 'POST' })
                .done(function () {
                    avisar(`Venda nº ${v.id} cancelada. Os itens voltaram ao estoque.`, 'sucesso');
                    carregarVendas($input.val().trim(), true);
                })
                .fail(function (xhr) {
                    avisar(mensagemDoServidor(xhr, 'Não foi possível cancelar a venda.'), 'erro');
                });
        });
    });

    carregarVendas();

    let debounceTimer;
    $input.on('input', function () {
        clearTimeout(debounceTimer);
        const codigo = $(this).val().trim();
        debounceTimer = setTimeout(() => carregarVendas(codigo), 300);
    });
}
