package com.castores.inventario.repository;

import com.castores.inventario.model.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository  extends JpaRepository<Producto, Integer> {
    boolean existsByNombreIgnoreCase(String nombre);
    List<Producto> findAllByOrderByNombreAsc();
    List<Producto> findByEstatusOrderByNombreAsc(Integer estatus);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.idProducto = :id")
    Optional<Producto> buscarParaActualizar(@Param("id") Integer id);
}
