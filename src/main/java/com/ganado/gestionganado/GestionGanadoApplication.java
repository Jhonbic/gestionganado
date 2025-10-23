package com.ganado.gestionganado;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDate;

@SpringBootApplication
public class GestionGanadoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionGanadoApplication.class, args);
    }

    // Este método se ejecuta automáticamente al iniciar la app
    /*@Bean
    CommandLineRunner initDatabase(GanadoRepository ganadoRepository) {
        return args -> {
            /* Creamos un nuevo registro de ganado
            Ganado vaca = new Ganado();
            vaca.setNombre("Luna");
            vaca.setRaza("Holstein");
            vaca.setSexo("Hembra");
            vaca.setFechaNacimiento(LocalDate.of(2022, 5, 10));
            vaca.setNotas("Primera vaca registrada de prueba");

            // Guardamos en la base de datos
            ganadoRepository.save(vaca);

            System.out.println("✅ Vaca registrada correctamente en la base de datos");
        };
    }*/
}
