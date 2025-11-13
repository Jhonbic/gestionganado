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
            @RequestParam(value = "padre", required = false) Long padreId,
            @RequestParam(value = "madre", required = false) Long madreId,
            @RequestParam("foto") MultipartFile foto) throws IOException {

        // 1) Si existe foto, la guardamos (igual que antes)
        if (!foto.isEmpty()) {
            String carpetaUploads = "uploads/";
            Path rutaCarpeta = Paths.get(carpetaUploads);
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }
            String nombreArchivo = foto.getOriginalFilename();
            Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);
            Files.copy(foto.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
            // Nota: guardamos la URL relativa
            ganado.setFotoUrl("/uploads/" + nombreArchivo);
        }

        // 2) Si viene id => edición, buscamos y actualizamos; si no => nuevo
        Ganado toSave;
        if (ganado.getId() != null) {
            toSave = ganadoRepository.findById(ganado.getId()).orElse(new Ganado());
            // copiar campos editables
            toSave.setNombre(ganado.getNombre());
            toSave.setRaza(ganado.getRaza());
            toSave.setSexo(ganado.getSexo());
            toSave.setFechaNacimiento(ganado.getFechaNacimiento());
            toSave.setNotas(ganado.getNotas());
            // Si foto fue subida, ya seteamos ganado.setFotoUrl arriba; preferimos mantenerla:
            if (ganado.getFotoUrl() != null) {
                toSave.setFotoUrl(ganado.getFotoUrl());
            }
        } else {
            // nuevo registro — usamos el objeto ganado tal cual
            toSave = ganado;
        }

        // 3) Asignar padre/madre por id (si fueron enviados)
        if (padreId != null) {
            Ganado padre = ganadoRepository.findById(padreId).orElse(null);
            toSave.setPadre(padre);
        } else {
            toSave.setPadre(null);
        }

        if (madreId != null) {
            Ganado madre = ganadoRepository.findById(madreId).orElse(null);
            toSave.setMadre(madre);
        } else {
            toSave.setMadre(null);
        }

        // 4) Guardar en BD (insert o update según corresponda)
        ganadoRepository.save(toSave);

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

        // <- AÑADIDO: lista para los selects de padre/madre
        List<Ganado> listaGanado = ganadoRepository.findAll();
        model.addAttribute("listaGanado", listaGanado);

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
