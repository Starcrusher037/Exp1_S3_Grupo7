package duoc.sumativa.transportes.repository;

import duoc.sumativa.transportes.model.GuiaDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GuiaRepository extends JpaRepository<GuiaDespacho, Long> {
    // busca por transportista y un rango de fecha/hora
    List<GuiaDespacho> findByTransportistaAndFechaEmisionBetween(String transportista, LocalDateTime inicio, LocalDateTime fin);
}