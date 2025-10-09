$(document).ready(function () {
    const conteudoDinamico = $('#conteudo-dinamico');

    function carregarConteudo(url) {
        conteudoDinamico.html(
            '<div class="text-center my-5">' +
                '<div class="spinner-border text-primary" role="status">' +
                    '<span class="visually-hidden">Carregando...</span>' +
                '</div>' +
                '<p class="mt-2">Carregando...</p>' +
            '</div>'
        );

        conteudoDinamico.load(url, function (response, status, xhr) {
            if (status == "error") {
                $(this).html(
                    "<p class='text-center text-danger'>Erro ao carregar a página: " +
                    xhr.status + " " + xhr.statusText + "</p>"
                );
            } else {
                if (url.includes('/cadastro-membro')) {
                    formularioParceiros();
                }

                if (url.includes('/cadastro-produto')) {
                    formularioProduto();
                }

                if (url.includes('/venda')) {
                    formularioVenda();
                }

                if (url.includes('/pesquisa-venda')) {
                    $.getScript('/script/consultaVendas.js')
                        .done(function () {
                            if (typeof inicializarConsultaVendas === 'function') {
                                inicializarConsultaVendas();
                            }
                        })
                        .fail(function () {
                            console.error('Falha ao carregar consultaVendas.js');
                        });
                }

                if (url.includes('/pesquisa-avancada')) {
                    $.getScript('/script/consultaAvancadaProdutos.js')
                        .done(function () {
                            if (typeof inicializarConsultaAvancadaProdutos === 'function') {
                                inicializarConsultaAvancadaProdutos();
                            }
                        })
                        .fail(function () {
                            console.error('Falha ao carregar consultaAvancadaProdutos.js');
                        });
                }

                if (url.includes('/pesquisa-produto')) {
                    $.getScript('/script/pesquisaProduto.js')
                        .done(function () {
                            if (typeof inicializarPesquisarProduto === 'function') {
                                inicializarPesquisarProduto();
                            }
                        })
                        .fail(function () {
                            console.error('Falha ao carregar pesquisarProduto.js');
                        });
                }
                if (url.includes('/pesquisa-membro')) {
                    $.getScript('/script/consultaMembros.js')
                        .done(function () {
                            if (typeof inicializarConsultaMembros === 'function') {
                                inicializarConsultaMembros('#conteudo-dinamico'); // passa o container
                            }
                        })
                        .fail(function () {
                            console.error('Falha ao carregar consultaMembros.js');
                        });
                }
            }
        });
    }

    $('.navbar-nav').on('click', 'a.nav-link, a.dropdown-item', function (event) {
            const url = $(this).attr('href');
            if (!url || url === '#') return;

            if (url.startsWith('/')) {
                event.preventDefault();
                if (url === '/' || url === '/login' || url.endsWith('.html')) {
                    window.location.href = url;
                } else {
                    carregarConteudo(url);
                }
            } else {
                event.preventDefault();
                carregarConteudo(url);
            }
        });

    conteudoDinamico.on('click', 'a.btn, button.btn', function (event) {
        const url = $(this).attr('href');
        if (url && url !== '#') {
            event.preventDefault();
            carregarConteudo(url);
        }
    });

    conteudoDinamico.on('change', 'input[name="parceiro"]', alternarCamposParceiro);
});
