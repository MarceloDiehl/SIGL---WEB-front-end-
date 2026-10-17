package com.sigl.sigl.seguranca;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.repository.FuncionarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ponte entre o Spring Security e a tabela de funcionários.
 * - loadUserByUsername: no login, busca o funcionário pelo e-mail.
 * - updatePassword: chamado pelo Spring logo depois de um login certo com senha em formato antigo
 *   (MD5); grava a mesma senha já em BCrypt. Assim as senhas migram sem ninguém precisar trocá-las.
 */
@Service
public class UsuarioService implements UserDetailsService, UserDetailsPasswordService {

    private final FuncionarioRepository funcionarioRepository;

    public UsuarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Funcionario funcionario = funcionarioRepository.findByEmail(email.trim())
                // a mensagem não chega à tela: e-mail inexistente e senha errada dão o mesmo erro
                .orElseThrow(() -> new UsernameNotFoundException("E-mail não cadastrado."));
        return UsuarioLogado.de(funcionario);
    }

    @Override
    @Transactional
    public UserDetails updatePassword(UserDetails usuario, String novoHash) {
        UsuarioLogado logado = (UsuarioLogado) usuario;
        Funcionario funcionario = funcionarioRepository.findById(logado.getId()).orElseThrow();
        funcionario.setSenha(novoHash);
        funcionarioRepository.save(funcionario);
        return logado.comSenha(novoHash);
    }
}
