package com.castores.inventario.repository;

import com.castores.inventario.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {
    List<Movimiento> findAllByOrderByFechaHoraDesc();
    List<Movimiento> findByTipoMovimiento_IdTipoMovimientoOrderByFechaHoraDesc(Integer idTipoMovimiento);
}
