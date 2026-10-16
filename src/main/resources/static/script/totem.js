// Modo totem: Consulta de preço travada em tela cheia, para o cliente bipar os produtos sozinho.
// Só fecha com a senha de um funcionário da loja (conferida no servidor, em /api/totem/encerrar).
// O estado fica na sessão: recarregar a página reabre o totem.
$(function () {
    const elModal = document.getElementById('modal-totem');
    if (!elModal) return;

    const modal = bootstrap.Modal.getOrCreateInstance(elModal, { backdrop: 'static', keyboard: false });
    const $codigo = $('#totem-codigo');
    const $mensagem = $('#totem-mensagem');
    const $produto = $('#totem-produto');
    const $espera = $('#totem-espera');
    const $senha = $('#totem-senha');
    const $campoSenha = $('#totem-senha-campo');

    const tipos = {
        1: 'Disco de vinil', 2: 'CD', 3: 'Papelaria', 4: 'Livros', 5: 'Moda e vestuário',
        6: 'Equipamentos eletrônicos', 7: 'Instrumentos musicais', 8: 'Raridades'
    };
    const condicoes = { 1: 'Novo', 2: 'Usado (ótimo)', 3: 'Usado (bom)', 4: 'Usado (com marcas)' };
    const moeda = (v) => Number(v || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

    const VOLTA_AO_INICIO_MS = 25000;   // depois de 25 s parado, a tela volta ao "Aguardando um produto"
    const MANTER_SESSAO_MS = 4 * 60 * 1000;
    let podeFechar = false;
    let timerInicio = null;
    let timerSessao = null;

    // ---------- abrir / fechar ----------
    function abrir(jaAtivoNoServidor) {
        const ativar = jaAtivoNoServidor ? $.Deferred().resolve().promise() : $.ajax({ url: '/api/totem/ativar', method: 'POST' });
        ativar.done(function () {
            podeFechar = false;
            telaInicial();
            modal.show();
            try { document.documentElement.requestFullscreen && document.documentElement.requestFullscreen().catch(function () {}); } catch (e) { /* opcional */ }
            clearInterval(timerSessao);
            timerSessao = setInterval(function () { $.getJSON('/api/totem/status'); }, MANTER_SESSAO_MS);
        }).fail(function () {
            avisar('Não foi possível ativar o modo totem. Tente novamente.', 'erro');
        });
    }

    function fechar() {
        podeFechar = true;
        clearInterval(timerSessao);
        clearTimeout(timerInicio);
        $senha.prop('hidden', true);
        modal.hide();
        try { document.fullscreenElement && document.exitFullscreen(); } catch (e) { /* opcional */ }
    }

    // Nada fecha o totem sem a senha (nem Esc, nem clique fora)
    elModal.addEventListener('hide.bs.modal', function (e) { if (!podeFechar) e.preventDefault(); });
    elModal.addEventListener('shown.bs.modal', function () { $codigo.trigger('focus'); });

    $(document).on('click', '#abrir-totem, .abrir-totem', function (e) { e.preventDefault(); abrir(false); });
    if (String($(elModal).data('ativo')) === 'true') abrir(true);

    // ---------- consulta ----------
    function telaInicial() {
        clearTimeout(timerInicio);
        $codigo.val('');
        $mensagem.text('').removeClass('erro');
        $produto.prop('hidden', true);
        $espera.prop('hidden', false);
    }

    function agendarVolta() {
        clearTimeout(timerInicio);
        timerInicio = setTimeout(telaInicial, VOLTA_AO_INICIO_MS);
    }

    function mostrar(p) {
        const qtd = Number(p.quantidade || 0);
        const disponibilidade = qtd === 0 ? ['Esgotado', 'esgotado'] : qtd <= 3 ? ['Últimas unidades', 'ultimas'] : ['Disponível', 'disponivel'];
        $('#totem-titulo').text(p.titulo || '');
        $('#totem-descricao').text(p.descricao || '').prop('hidden', !p.descricao);
        $('#totem-preco').text(moeda(p.valor));
        $('#totem-tipo').text(tipos[p.idTipo] || '').prop('hidden', !tipos[p.idTipo]);
        $('#totem-estado').text(condicoes[p.idCondicao] || '').prop('hidden', !condicoes[p.idCondicao]);
        $('#totem-disponivel').text(disponibilidade[0]).attr('class', 'totem-selo totem-' + disponibilidade[1]);
        $mensagem.text('').removeClass('erro');
        $espera.prop('hidden', true);
        $produto.prop('hidden', false).removeClass('surgir');
        void $produto[0].offsetWidth;
        $produto.addClass('surgir');
    }

    function buscar(cod) {
        $.ajax({ url: '/api/produtos/codigo/' + cod, method: 'GET', dataType: 'json' })
            .done(function (p) {
                if (p) mostrar(p);
                else naoEncontrado();
            })
            .fail(naoEncontrado)
            .always(function () {
                $codigo.val('').trigger('focus');   // pronto para o próximo bip
                agendarVolta();
            });
    }

    function naoEncontrado() {
        $produto.prop('hidden', true);
        $espera.prop('hidden', false);
        $mensagem.addClass('erro').text('Produto não encontrado. Confira o código ou chame um atendente.');
    }

    $codigo.on('input', function () {
        const cod = this.value.replace(/\D/g, '').slice(0, 6);
        this.value = cod;
        if (cod.length === 6) buscar(cod);
    });
    $codigo.on('keydown', function (e) {             // leitores de código costumam mandar Enter no fim
        if (e.key === 'Enter') { e.preventDefault(); const cod = this.value.replace(/\D/g, ''); if (cod.length === 6) buscar(cod); }
    });

    // O foco volta sempre para o campo do código (o leitor "digita" nele)
    $(elModal).on('click', function (e) {
        if ($senha.prop('hidden') && !$(e.target).closest('.totem-cadeado').length) $codigo.trigger('focus');
    });

    // Ao destravar, encerra a sessão e volta para a tela de login (mesmo botão "Sair" do menu)
    function sairDoSistema() {
        const botaoSair = document.querySelector('.usuario-sair .btn-sair');
        if (botaoSair) botaoSair.click();
        else window.location.href = '/login';
    }

    // ---------- senha para sair ----------
    $('#totem-cadeado').on('click', function () {
        $senha.prop('hidden', false);
        $campoSenha.val('').removeClass('is-invalid').siblings('.erro-campo').remove();
        setTimeout(function () { $campoSenha.trigger('focus'); }, 50);
    });

    $('#totem-senha-cancelar').on('click', function () {
        $senha.prop('hidden', true);
        $codigo.trigger('focus');
    });

    function erroSenha(texto) {
        $campoSenha.addClass('is-invalid').siblings('.erro-campo').remove();
        $('<div class="invalid-feedback erro-campo" role="alert"></div>').text(texto).insertAfter($campoSenha);
        $campoSenha.val('').trigger('focus');
    }

    $senha.on('submit', function (e) {
        e.preventDefault();
        const senha = $campoSenha.val();
        if (!senha) { erroSenha('Digite a senha.'); return; }
        const $botao = $('#totem-senha-confirmar').prop('disabled', true);
        $.ajax({ url: '/api/totem/encerrar', method: 'POST', contentType: 'application/json', data: JSON.stringify({ senha: senha }) })
            .done(function (r) {
                if (r && r.encerrado) { fechar(); sairDoSistema(); }
                else erroSenha((r && r.mensagem) || 'Senha incorreta.');
            })
            .fail(function () { erroSenha('Não foi possível conferir a senha. Tente de novo.'); })
            .always(function () { $botao.prop('disabled', false); });
    });
    $campoSenha.on('input', function () { $(this).removeClass('is-invalid').siblings('.erro-campo').remove(); });
});
