package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.model.Auditoria;
import com.donmanuelito.minimarket.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public List<Auditoria> listar() {
        return auditoriaService.listar();
    }
}
