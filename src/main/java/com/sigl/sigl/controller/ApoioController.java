package com.sigl.sigl.controller;

import com.sigl.sigl.dto.ApoioResposta;
import com.sigl.sigl.service.ApoioService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/apoio")
public class ApoioController {

    private final ApoioService apoioService;

    public ApoioController(ApoioService apoioService) {
        this.apoioService = apoioService;
    }

    // As listas quase nunca mudam: o navegador pode reaproveitar a resposta por alguns minutos
    @GetMapping
    public ResponseEntity<ApoioResposta> listar() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(10)).cachePrivate())
                .body(apoioService.listarTudo());
    }
}
