package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.DashboardResponse;
import com.donmanuelito.minimarket.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Resumen operativo para la pantalla principal. */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse resumen() {
        return dashboardService.resumen();
    }
}
