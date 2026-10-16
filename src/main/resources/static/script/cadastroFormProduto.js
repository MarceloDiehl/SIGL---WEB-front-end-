function formularioProduto() {
    console.log("Inicializando formulário de cadastro de produto com jQuery Validation...");

    const $form = $('#formulario-produto');
    const $mensagemFeedback = $('#mensagem-feedback');

    const tipoMap = {
        'disco': 1, 'cd': 2, 'papelaria': 3,
        'livros': 4, 'moda': 5, 'equipamentos': 6,
        'instrumentos': 7, 'raridades': 8
    };

    const condicaoMap = {
        'novo': 1, 'usado-otimo': 2, 'usado-bom': 3, 'usado-marcas-uso': 4
    };

    function limparFormulario() {
        $form[0].reset();
        $mensagemFeedback.empty().removeClass('alert alert-success alert-danger');
        $form.find('.is-invalid').removeClass('is-invalid');
        $form.find('.invalid-feedback').remove();
        $form.validate().resetForm();
        $('#codigo-produto').prop('disabled', false);
        $('#data-produto').val(new Date().toLocaleDateString('sv-SE'));
        atualizarContadorDescricao();
    }

    function atualizarContadorDescricao() {
        $('#descricao-contador').text($('#descricao-produto').val().length);
    }
    $('#descricao-produto').off('input.contador').on('input.contador', atualizarContadorDescricao);

    $('#limpar-cadastro-produto').off('click').on('click', function() {
        limparFormulario();
    });

    const urlParams = new URLSearchParams(window.location.search);
    const codigoParam = urlParams.get("codigo");
    if (codigoParam) {
        $.ajax({
            url: `/api/produtos/codigo/${codigoParam}`,
            method: 'GET',
            dataType: 'json'
        })
        .done(function(produto) {
            if (produto) {
                $('#codigo-produto').val(produto.codProduto).prop('disabled', true);
                $('#titulo-produto').val(produto.titulo);
                $('#descricao-produto').val(produto.descricao);
                $('#valor-produto').val(produto.valor);
                $('#quantidade-produto').val(produto.quantidade);
                $('#tipo-produto').val(Object.keys(tipoMap).find(key => tipoMap[key] === produto.idTipo));
                $('#condicao-produto').val(Object.keys(condicaoMap).find(key => condicaoMap[key] === produto.idCondicao));
                $('#data-produto').val(produto.entrada);
            }
        })
        .fail(function() {
            avisar("Erro ao carregar produto para atualização.", "erro");
        });
    }

    $form.validate({
        rules: {
            'titulo-produto': { required: true },
            'descricao-produto': { required: true, minlength: 10 },
            'valor-produto': { required: true, number: true, min: 0.01 },
            'quantidade-produto': { required: true, digits: true, min: 0 },
            'tipo': { required: true },
            'condicao': { required: true },
            'data-produto': { required: true, dateISO: true },
            'codigo-produto': { required: true, minlength: 6, maxlength: 6 },
        },
        messages: {
            'titulo-produto': { required: "Informe o título do produto." },
            'descricao-produto': { required: "Forneça uma descrição detalhada do produto.", minlength: "A descrição deve ter no mínimo 10 caracteres." },
            'valor-produto': { required: "Informe o valor unitário do produto.", number: "Digite um valor numérico válido.", min: "O valor unitário deve ser maior que zero." },
            'quantidade-produto': { required: "Informe a quantidade em estoque.", digits: "A quantidade deve ser um número inteiro.", min: "A quantidade não pode ser negativa." },
            'tipo': { required: "Selecione o tipo de produto." },
            'condicao': { required: "Selecione a condição do produto." },
            'data-produto': { required: "Informe a data de inclusão.", dateISO: "Data inválida (AAAA-MM-DD)." },
            'codigo-produto': { required: "Informe o código do produto.", minlength: "O código deve ter 6 dígitos.", maxlength: "O código deve ter 6 dígitos." },
        },
        errorElement: 'div',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            if (element.is('select')) error.insertAfter(element);
            else element.closest('[class*="col-"]').append(error);
        },
        highlight: function (element) { $(element).addClass('is-invalid').removeClass('is-valid'); },
        unhighlight: function (element) { $(element).removeClass('is-invalid').addClass('is-valid'); },

        submitHandler: function (form, event) {
            event.preventDefault();
            $mensagemFeedback.empty().removeClass('alert alert-success alert-danger');

            const produtoData = {
                codProduto: $('#codigo-produto').val().trim(),
                titulo: $('#titulo-produto').val().trim(),
                descricao: $('#descricao-produto').val().trim(),
                valor: parseFloat($('#valor-produto').val().replace(',', '.')),
                quantidade: parseInt($('#quantidade-produto').val()),
                entrada: $('#data-produto').val(),
                idTipo: tipoMap[$('#tipo-produto').val()],
                idCondicao: condicaoMap[$('#condicao-produto').val()]
            };

            console.log("Dados do produto a serem enviados:", produtoData);

            const ajaxOptions = {
                url: codigoParam ? `/api/produtos/${codigoParam}` : '/api/produtos',
                method: codigoParam ? 'PUT' : 'POST',
                contentType: 'application/json',
                data: JSON.stringify(produtoData)
            };

            $.ajax(ajaxOptions)
            .done(function(response) {
                let msg = codigoParam ? "Produto atualizado com sucesso!" : "Produto cadastrado com sucesso!";
                avisar(msg, "sucesso");
                $mensagemFeedback.addClass('alert alert-success').text(msg + (response.id ? ' ID: ' + response.id : ''));
                if (!codigoParam) limparFormulario(); // Limpa apenas se for cadastro
            })
            .fail(function(xhr, status, error) {
                let errorMessage = 'Erro ao salvar produto. Verifique os dados e tente novamente.';
                if (xhr.status === 409) {
                    errorMessage = xhr.responseText || 'Já existe um produto com este código.';
                    $form.validate().showErrors({ 'codigo-produto': errorMessage });
                } else if (xhr.status === 400 && xhr.responseJSON && xhr.responseJSON.errors) {
                    let backendErrors = {};
                    xhr.responseJSON.errors.forEach(err => {
                        const fieldName = err.field;
                        let htmlFieldName = '';
                        if (fieldName === 'codProduto') htmlFieldName = 'codigo-produto';
                        else if (fieldName === 'titulo') htmlFieldName = 'titulo-produto';
                        else if (fieldName === 'descricao') htmlFieldName = 'descricao-produto';
                        else if (fieldName === 'valor') htmlFieldName = 'valor-produto';
                        else if (fieldName === 'quantidade') htmlFieldName = 'quantidade-produto';
                        else if (fieldName === 'entrada') htmlFieldName = 'data-produto';
                        else if (fieldName === 'idTipo') htmlFieldName = 'tipo';
                        else if (fieldName === 'idCondicao') htmlFieldName = 'condicao';
                        else htmlFieldName = fieldName;
                        backendErrors[htmlFieldName] = err.defaultMessage;
                    });
                    $form.validate().showErrors(backendErrors);
                    errorMessage = 'Por favor, corrija os erros indicados no formulário.';
                } else if (xhr.responseText) {
                    errorMessage = xhr.responseText;
                }
                $mensagemFeedback.addClass('alert alert-danger').text(errorMessage);
            });
        }
    });

    limparFormulario();
}
