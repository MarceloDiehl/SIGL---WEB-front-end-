package com.sigl.sigl.controller;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    private String hashMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam("login-email") String email, @RequestParam("login-senha") String senha, RedirectAttributes redirectAttributes) {

        String senhaCriptografada = hashMD5(senha);
        Optional<Funcionario> funcionarioOptional = funcionarioRepository.findByEmailAndSenha(email, senhaCriptografada);

        if (funcionarioOptional.isPresent()) {
            Funcionario funcionario = funcionarioOptional.get();
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("loginError", "E-mail ou senha inválidos.");
            return "redirect:/login";
        }
    }
}