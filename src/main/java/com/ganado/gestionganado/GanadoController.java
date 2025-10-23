package com.ganado.gestionganado;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

@Controller
public class GanadoController {

    @Autowired
    private GanadoRepository ganadoRepository;

    // ==============================
    // 1️⃣ Listar todas las vacas
    // ==============================
    @GetMapping("/ganado")
    public String listarGanado(Model model) {
        List<Ganado> listaGanado = ganadoRepository.findAll();
        model.addAttribute("ganado", listaGanado);
        return "lista-ganado"; // vista HTML donde se muestran las vacas
    }

    // ==============================
    // 2️⃣ Mostrar formulario para registrar nuevo ganado
    // ==============================
    @GetMapping("/ganado/nuevo")

    public String mostrarFormulario(Model model) {
        model.addAttribute("ganado", new Ganado());
        // Lista de todos los animales (para poder elegir padres)
        List<Ganado> listaGanado = ganadoRepository.findAll();
        model.addAttribute("listaGanado", listaGanado);
        return "form-ganado";
    }

    // ==============================
    // 3️⃣ Guardar datos (con foto)
    // ==============================
    @PostMapping("/ganado/guardar")
    public String guardarGanado(
            @ModelAttribute Ganado ganado,
            @RequestParam("foto") MultipartFile foto) throws IOException {

        if (!foto.isEmpty()) {
            // 1. Definimos carpeta donde se guardarán las imágenes
            String carpetaUploads = "uploads/";
            Path rutaCarpeta = Paths.get(carpetaUploads);

            // 2. Si no existe la carpeta, la creamos
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }

            // 3. Obtenemos el nombre original del archivo
            String nombreArchivo = foto.getOriginalFilename();

            // 4. Construimos la ruta completa (carpeta + archivo)
            Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);

            // 5. Guardamos físicamente el archivo
            Files.copy(foto.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // 6. Guardamos la ruta relativa en la base de datos
            ganado.setFotoUrl("/uploads/" + nombreArchivo);
        }

        // 7. Guardamos el registro del ganado en la base de datos
        ganadoRepository.save(ganado);

        // 8. Redirigimos al listado
        return "redirect:/ganado";
    }

    // ==============================
    // 4️⃣ Editar ganado existente (opcional, futuro paso)
    // ==============================
    @GetMapping("/ganado/editar/{id}")
    public String editarGanado(@PathVariable Long id, Model model) {
        Ganado ganado = ganadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("ganado", ganado);
        return "form-ganado"; // reutilizamos el mismo formulario
    }

    // ==============================
    // 5️⃣ Eliminar ganado
    // ==============================
    @GetMapping("/ganado/eliminar/{id}")
    public String eliminarGanado(@PathVariable Long id) {
        ganadoRepository.deleteById(id);
        return "redirect:/ganado";
    }

    @GetMapping(value = "/ganado/{id}/hijos", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> obtenerHijos(@PathVariable Long id) {
        List<Ganado> hijosPadre = ganadoRepository.findByPadreId(id);
        List<Ganado> hijosMadre = ganadoRepository.findByMadreId(id);

        List<Map<String, Object>> resultado = new ArrayList<>();

        // unir listas (evita duplicados si por alguna razón aparece el mismo registro)
        Set<Long> vistos = new HashSet<>();
        Stream.concat(hijosPadre.stream(), hijosMadre.stream())
                .forEach(h -> {
                    if (h != null && h.getId() != null && vistos.add(h.getId())) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", h.getId());
                        m.put("nombre", h.getNombre());
                        m.put("raza", h.getRaza());
                        m.put("sexo", h.getSexo());
                        // puedes agregar más campos si quieres (fechaNacimiento, etc.)
                        resultado.add(m);
                    }
                });

        return resultado;
    }
}
