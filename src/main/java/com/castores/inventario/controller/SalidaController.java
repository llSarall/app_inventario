package com.castores.inventario.controller;

import com.castores.inventario.exception.ReglaNegocioException;
import com.castores.inventario.model.Producto;
import com.castores.inventario.service.InventarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/salidas")
public class SalidaController {

    private final InventarioService inventarioService;

    public SalidaController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public String ver(Model model) {
        model.addAttribute("productos", inventarioService.listarProductos("activos"));
        return "salidas";
    }

    @PostMapping
    public String sacar(@RequestParam(required = false) Integer idProducto,
                        @RequestParam(required = false) Integer cantidad,
                        Authentication auth,
                        RedirectAttributes flash) {
        try {
            Producto p = inventarioService.registrarSalida(idProducto, cantidad, auth.getName());
            flash.addFlashAttribute("exito", "Salida registrada: -" + cantidad + " de "
                    + p.getNombre() + ". Existencia restante: " + p.getCantidad() + ".");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/salidas";
    }
}