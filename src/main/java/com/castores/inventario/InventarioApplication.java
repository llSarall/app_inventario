package com.castores.inventario;


import com.castores.inventario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventarioApplication.class, args);
    }

    // TEMPORAL: prueba de relaciones
    @Bean
    CommandLineRunner prueba(UsuarioRepository repo) {
        return args -> repo.findAll().forEach(u ->
                System.out.println(u.getNombre() + " - " + u.getCorreo() + " - " + u.getRol().getNombre()));
    }

}
