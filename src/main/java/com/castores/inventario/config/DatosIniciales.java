package com.castores.inventario.config;

import com.castores.inventario.model.Usuario;
import com.castores.inventario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosIniciales(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (!usuario.getContrasena().startsWith("$2")) {
                usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
                System.out.println("Contraseña cifrada para " + usuario.getCorreo());
            }
        }
    }
}