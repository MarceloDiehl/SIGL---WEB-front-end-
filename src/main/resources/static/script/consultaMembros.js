function inicializarConsultaMembros(containerSelector) {
    const $container = $(containerSelector);
    const $inputCpf = $container.find('#cpf_pesquisa');
    const $tbody = $container.find('#tabela-membros-body');
    const $btnExcluir = $container.find('#excluir_membro');

    let membroSelecionado = null;

    function carregarMembros(cpf = '') {
        $tbody.html('<tr><td colspan="20" class="text-center">Carregando membros...</td></tr>');

        $.getJSON('/api/clientes', { cpf: cpf })
            .done(function(clientes) {
                $.getJSON('/api/funcionarios', { cpf: cpf })
                    .done(function(funcionarios) {
                        $tbody.empty();

                        const todosMembros = [];

                        // Clientes
                        clientes.forEach(c => {
                            todosMembros.push({
                                tipo: 'cliente',
                                id: c.id,
                                nome: c.nome ?? '-',
                                sobrenome: c.sobrenome ?? '-',
                                dataNascimento: c.dataNascimento ?? '-',
                                idCargo: '-',
                                idEscolaridade: '-',
                                especialidade: '-',
                                telefone: c.telefone ?? '-',
                                cpf: c.cpf ?? '-',
                                idGenero: '-',
                                endereco: c.endereco ?? '-',
                                numeroPredial: c.numeroPredial ?? '-',
                                bairro: c.bairro ?? '-',
                                cidade: c.cidade ?? '-',
                                estado: c.estado ?? '-',
                                cep: c.cep ?? '-',
                                contatoEmergencia: '-',
                                dataEntrada: c.clienteDesde ?? '-',
                                rg: c.rg ?? '-',
                                ctpas: '-',
                                email: '-'
                            });
                        });

                        funcionarios.forEach(f => {
                            todosMembros.push({
                                tipo: 'funcionario',
                                id: f.id,
                                nome: f.nome ?? '-',
                                sobrenome: f.sobrenome ?? '-',
                                dataNascimento: f.dataNascimento ?? '-',
                                idCargo: f.idCargo ?? '-',
                                idEscolaridade: f.idEscolaridade ?? '-',
                                especialidade: f.especialidade ?? '-',
                                telefone: f.telefone ?? '-',
                                cpf: f.cpf ?? '-',
                                idGenero: f.idGenero ?? '-',
                                endereco: f.endereco ?? '-',
                                numeroPredial: f.numeroPredial ?? '-',
                                bairro: f.bairro ?? '-',
                                cidade: f.cidade ?? '-',
                                estado: f.estado ?? '-',
                                cep: f.cep ?? '-',
                                contatoEmergencia: f.contatoEmergencia ?? '-',
                                dataEntrada: f.dataEntrada ?? '-',
                                rg: f.rg ?? '-',
                                ctpas: f.ctpas ?? '-',
                                email: f.email ?? '-'
                            });
                        });

                        if (todosMembros.length === 0) {
                            $tbody.append('<tr><td colspan="20" class="text-center">Nenhum membro encontrado</td></tr>');
                            return;
                        }

                        todosMembros.forEach(m => {
                            $tbody.append(`
                                <tr data-tipo="${m.tipo}" data-id="${m.id}">
                                    <td>${m.nome}</td>
                                    <td>${m.sobrenome}</td>
                                    <td>${m.dataNascimento}</td>
                                    <td>${m.idCargo}</td>
                                    <td>${m.idEscolaridade}</td>
                                    <td>${m.especialidade}</td>
                                    <td>${m.telefone}</td>
                                    <td>${m.cpf}</td>
                                    <td>${m.idGenero}</td>
                                    <td>${m.endereco}</td>
                                    <td>${m.numeroPredial}</td>
                                    <td>${m.bairro}</td>
                                    <td>${m.cidade}</td>
                                    <td>${m.estado}</td>
                                    <td>${m.cep}</td>
                                    <td>${m.contatoEmergencia}</td>
                                    <td>${m.dataEntrada}</td>
                                    <td>${m.rg}</td>
                                    <td>${m.ctpas}</td>
                                    <td>${m.email}</td>
                                </tr>
                            `);
                        });

                        $tbody.find('tr').off('click').on('click', function() {
                            $tbody.find('tr').removeClass('table-active');
                            $(this).addClass('table-active');

                            membroSelecionado = {
                                tipo: $(this).data('tipo'),
                                id: $(this).data('id')
                            };

                            $btnExcluir.prop('disabled', false);
                        });
                    })
                    .fail(function() {
                        $tbody.html('<tr><td colspan="20" class="text-danger text-center">Erro ao carregar funcionários</td></tr>');
                    });
            })
            .fail(function() {
                $tbody.html('<tr><td colspan="20" class="text-danger text-center">Erro ao carregar clientes</td></tr>');
            });
    }

    let debounceTimer;
    $inputCpf.on('input', function() {
        clearTimeout(debounceTimer);
        const cpf = $(this).val().trim();
        debounceTimer = setTimeout(() => carregarMembros(cpf), 300);
    });

    carregarMembros();

    $btnExcluir.off('click').on('click', function() {
        if (!membroSelecionado) {
            alert("Selecione um membro antes de excluir.");
            return;
        }

        if (!confirm(`Tem certeza que deseja excluir este membro?`)) {
            return;
        }

        const { tipo, id } = membroSelecionado;
        $.ajax({
            url: `/api/${tipo === 'cliente' ? 'clientes' : 'funcionarios'}/${id}`,
            method: 'DELETE'
        })
        .done(function(msg) {
            alert(msg);
            membroSelecionado = null;
            $btnExcluir.prop('disabled', true);
            carregarMembros();
        })
        .fail(function(xhr) {
            alert("Erro ao excluir: " + xhr.responseText);
        });
    });

    $btnExcluir.prop('disabled', true);
    $inputCpf.mask('000.000.000-00');
}
