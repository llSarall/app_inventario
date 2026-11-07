package com.castores.inventario.controller;

import com.castores.inventario.exception.ReglaNegocioException;
import com.castores.inventario.model.Producto;
import com.castores.inventario.service.InventarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/inventario")
    public String verInventario(@RequestParam(defaultValue = "todos") String filtro, Model model) {
        model.addAttribute("productos", inventarioService.listarProductos(filtro));
        model.addAttribute("filtro", filtro);
        return "inventario";
    }

    @PostMapping("/inventario/productos")
    public String agregarProducto(@RequestParam String nombre,
                                  @RequestParam(required = false) String descripcion,
                                  @RequestParam(required = false) BigDecimal precio,
                                  RedirectAttributes flash) {
        try {
            Producto p = inventarioService.registrarProducto(nombre, descripcion, precio);
            flash.addFlashAttribute("exito", "Producto " + p.getNombre() + " registrado con existencia 0.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventario";
    }

    @PostMapping("/inventario/{id}/entrada")
    public String entrada(@PathVariable Integer id,
                          @RequestParam(required = false) Integer cantidad,
                          Authentication auth,
                          RedirectAttributes flash) {
        try {
            Producto p = inventarioService.registrarEntrada(id, cantidad, auth.getName());
            flash.addFlashAttribute("exito", "Entrada registrada: +" + cantidad + " de "
                    + p.getNombre() + ". Nueva existencia: " + p.getCantidad() + ".");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventario";
    }

    @PostMapping("/inventario/{id}/baja")
    public String baja(@PathVariable Integer id, RedirectAttributes flash) {
        Producto p = inventarioService.cambiarEstatus(id, false);
        flash.addFlashAttribute("exito", "Producto " + p.getNombre() + " dado de baja.");
        return "redirect:/inventario";
    }

    @PostMapping("/inventario/{id}/reactivar")
    public String reactivar(@PathVariable Integer id, RedirectAttributes flash) {
        Producto p = inventarioService.cambiarEstatus(id, true);
        flash.addFlashAttribute("exito", "Producto " + p.getNombre() + " reactivado.");
        return "redirect:/inventario";
    }
}