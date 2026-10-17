package com.sigl.sigl.seguranca;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.model.apoio.Cargo;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Funcionário autenticado, guardado na sessão pelo Spring Security.
 * Nas controllers, chega por @AuthenticationPrincipal UsuarioLogado usuario.
 */
public class UsuarioLogado implements UserDetails, CredentialsContainer {

    private final Integer id;
    private final String email;
    private final String nome;
    private final String sobrenome;
    private final Integer idCargo;
    private final String cargo;
    private String senha;   // hash; apagado depois do login (eraseCredentials)

    public UsuarioLogado(Integer id, String email, String senha, String nome, String sobrenome,
                         Integer idCargo, String cargo) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.idCargo = idCargo;
        this.cargo = cargo;
    }

    public static UsuarioLogado de(Funcionario funcionario) {
        Cargo cargo = funcionario.getCargo();
        return new UsuarioLogado(funcionario.getId(), funcionario.getEmail(), funcionario.getSenha(),
                funcionario.getNome(), funcionario.getSobrenome(), funcionario.getIdCargo(),
                cargo != null ? cargo.getDescricao() : "Funcionário");
    }

    /** Mesmo usuário com outro hash de senha (usado quando a senha é convertida para BCrypt). */
    UsuarioLogado comSenha(String novoHash) {
        return new UsuarioLogado(id, email, novoHash, nome, sobrenome, idCargo, cargo);
    }

    // Papel no formato do Spring Security (ROLE_GERENTE...), pelo id do cargo: renomear o cargo no
    // banco não muda permissões. Base para a matriz de permissões por cargo (item 10); por enquanto
    // todo funcionário logado acessa tudo.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String papel = switch (idCargo == null ? 0 : idCargo) {
            case Cargo.VENDEDOR -> "VENDEDOR";
            case Cargo.GERENTE -> "GERENTE";
            case Cargo.ESTOQUISTA -> "ESTOQUISTA";
            default -> "FUNCIONARIO";
        };
        return List.of(new SimpleGrantedAuthority("ROLE_" + papel));
    }

    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }

    // "Marcelo Diehl" -> "MD", para o avatar do menu
    public String getIniciais() {
        String primeira = nome == null || nome.isBlank() ? "" : nome.substring(0, 1);
        String segunda = sobrenome == null || sobrenome.isBlank() ? "" : sobrenome.substring(0, 1);
        return (primeira + segunda).toUpperCase();
    }

    public boolean isGerente() {
        return idCargo != null && idCargo == Cargo.GERENTE;
    }

    @Override
    public void eraseCredentials() {
        this.senha = null;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCargo() {
        return cargo;
    }
}
