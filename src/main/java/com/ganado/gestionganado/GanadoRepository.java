package com.ganado.gestionganado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GanadoRepository extends JpaRepository<Ganado, Long> {
    // Buscar hijos donde este animal es padre
    List<Ganado> findByPadreId(Long padreId);

    // Buscar hijos donde este animal es madre
    List<Ganado> findByMadreId(Long madreId);
}
