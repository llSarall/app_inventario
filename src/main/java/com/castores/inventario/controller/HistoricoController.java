package com.castores.inventario.controller;

import com.castores.inventario.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HistoricoController {

    private final InventarioService inventarioService;

    public HistoricoController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/historico")
    public String ver(@RequestParam(defaultValue = "todos") String tipo, Model model) {
        model.addAttribute("movimientos", inventarioService.listarMovimientos(tipo));
        model.addAttribute("tipo", tipo);
        return "historico";
    }
}