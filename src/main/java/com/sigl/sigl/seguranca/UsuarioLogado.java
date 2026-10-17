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

    // Papel pelo id do cargo: renomear o cargo no banco não muda permissões
    public String getPapel() {
        return switch (idCargo == null ? 0 : idCargo) {
            case Cargo.VENDEDOR -> RegrasDeAcesso.VENDEDOR;
            case Cargo.GERENTE -> RegrasDeAcesso.GERENTE;
            case Cargo.ESTOQUISTA -> RegrasDeAcesso.ESTOQUISTA;
            default -> "FUNCIONARIO";
        };
    }

    // No formato do Spring Security: ROLE_GERENTE etc. (usado por hasRole nas RegrasDeAcesso)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getPapel()));
    }

    // Para as telas esconderem o que o cargo não pode usar (o servidor confere de novo)
    public boolean isPodeVender() {
        return RegrasDeAcesso.VENDER.contains(getPapel());
    }

    public boolean isPodeEditarProdutos() {
        return RegrasDeAcesso.EDITAR_PRODUTOS.contains(getPapel());
    }

    public boolean isPodeAtenderClientes() {
        return RegrasDeAcesso.ATENDER_CLIENTES.contains(getPapel());
    }

    public boolean isPodeGerir() {
        return RegrasDeAcesso.GERIR.contains(getPapel());
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
