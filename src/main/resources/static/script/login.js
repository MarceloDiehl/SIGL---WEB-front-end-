$(document).ready(function () {
    $('#formulario-login').validate({
        rules: {
            'login-email': {
                required: true,
                email: true
            },
            'login-senha': {
                required: true,
                minlength: 5
            }
        },
        messages: {
            'login-email': {
                required: "O e-mail é obrigatório.",
                email: "Por favor, insira um e-mail válido."
            },
            'login-senha': {
                required: "A senha é obrigatória.",
                minlength: "A senha deve ter no mínimo 5 caracteres."
            }
        },
        submitHandler: function (form) {
            form.submit();
        },
        errorElement: 'div',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            element.closest('.mb-3').append(error);
        },
        highlight: function (element) {
            $(element).addClass('is-invalid');
            $(element).removeClass('is-valid');
        },
        unhighlight: function (element) {
            $(element).removeClass('is-invalid');
            $(element).addClass('is-valid');
        }
    });
});
