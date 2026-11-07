package com.castores.inventario.service;

import com.castores.inventario.exception.ReglaNegocioException;
import com.castores.inventario.model.Movimiento;
import com.castores.inventario.model.Producto;
import com.castores.inventario.model.TipoMovimiento;
import com.castores.inventario.model.Usuario;
import com.castores.inventario.repository.MovimientoRepository;
import com.castores.inventario.repository.ProductoRepository;
import com.castores.inventario.repository.TipoMovimientoRepository;
import com.castores.inventario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;
    private final UsuarioRepository usuarioRepository;

    public InventarioService(ProductoRepository productoRepository,
                             MovimientoRepository movimientoRepository,
                             TipoMovimientoRepository tipoMovimientoRepository,
                             UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.tipoMovimientoRepository = tipoMovimientoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Producto> listarProductos(String filtro) {
        if ("activos".equals(filtro)) {
            return productoRepository.findByEstatusOrderByNombreAsc(1);
        }
        if ("inactivos".equals(filtro)) {
            return productoRepository.findByEstatusOrderByNombreAsc(0);
        }
        return productoRepository.findAllByOrderByNombreAsc();
    }

    @Transactional
    public Producto registrarProducto(String nombre, String descripcion, BigDecimal precio) {

        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre del producto es obligatorio.");
        }

        String nombreLimpio = nombre.trim().toUpperCase();

        if (productoRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new ReglaNegocioException("Ya existe un producto con el nombre " + nombreLimpio + ".");
        }

        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El precio debe ser mayor o igual a 0.");
        }

        Producto producto = new Producto();
        producto.setNombre(nombreLimpio);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setCantidad(0);
        producto.setEstatus(1);
        producto.setFechaRegistro(LocalDateTime.now());

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto registrarEntrada(Integer idProducto, Integer cantidad, String correoUsuario) {

        if (cantidad == null || cantidad <= 0) {
            throw new ReglaNegocioException(
                    "No se puede disminuir el inventario desde la entrada de productos. La cantidad debe ser mayor a 0.");
        }

        Producto producto = productoRepository.buscarParaActualizar(idProducto)
                .orElseThrow(() -> new ReglaNegocioException("El producto no existe."));

        if (producto.getEstatus() != 1) {
            throw new ReglaNegocioException("No se puede agregar inventario a un producto dado de baja.");
        }

        producto.setCantidad(producto.getCantidad() + cantidad);
        guardarMovimiento(producto, TipoMovimiento.ENTRADA, cantidad, correoUsuario);
        return producto;
    }

    @Transactional
    public Producto cambiarEstatus(Integer idProducto, boolean activar) {
        Producto producto = buscarProducto(idProducto);
        producto.setEstatus(activar ? 1 : 0);
        return producto;
    }

    private Producto buscarProducto(Integer idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new ReglaNegocioException("El producto no existe."));
    }

    @Transactional
    public Producto registrarSalida(Integer idProducto, Integer cantidad, String correoUsuario) {

        if (idProducto == null) {
            throw new ReglaNegocioException("Selecciona un producto.");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad a sacar debe ser mayor a 0.");
        }

        Producto producto = productoRepository.buscarParaActualizar(idProducto)
                .orElseThrow(() -> new ReglaNegocioException("El producto no existe."));

        // Regla: solo productos activos
        if (producto.getEstatus() != 1) {
            throw new ReglaNegocioException("Solo se puede sacar inventario de productos activos.");
        }

        // Regla: no sacar más de lo que hay
        if (cantidad > producto.getCantidad()) {
            throw new ReglaNegocioException("No hay suficiente inventario de " + producto.getNombre()
                    + ". Intentaste sacar " + cantidad + " y solo hay " + producto.getCantidad() + " en existencia.");
        }

        producto.setCantidad(producto.getCantidad() - cantidad);
        guardarMovimiento(producto, TipoMovimiento.SALIDA, cantidad, correoUsuario);
        return producto;
    }

    public List<Movimiento> listarMovimientos(String tipo) {
        if ("entrada".equals(tipo)) {
            return movimientoRepository.findByTipoMovimiento_IdTipoMovimientoOrderByFechaHoraDesc(TipoMovimiento.ENTRADA);
        }
        if ("salida".equals(tipo)) {
            return movimientoRepository.findByTipoMovimiento_IdTipoMovimientoOrderByFechaHoraDesc(TipoMovimiento.SALIDA);
        }
        return movimientoRepository.findAllByOrderByFechaHoraDesc();
    }

    private void guardarMovimiento(Producto producto, int idTipo, int cantidad, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el usuario de la sesión."));
        TipoMovimiento tipo = tipoMovimientoRepository.findById(idTipo)
                .orElseThrow(() -> new IllegalStateException("Falta el catálogo de tipos de movimiento."));

        Movimiento movimiento = new Movimiento();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setUsuario(usuario);
        movimiento.setCantidad(cantidad);
        movimiento.setFechaHora(LocalDateTime.now());
        movimientoRepository.save(movimiento);
    }
}