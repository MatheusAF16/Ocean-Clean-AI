package fiap.com.br.Ocean.Clean.AI.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fiap.com.br.Ocean.Clean.AI.Models.Drone;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {

    @Query(value = "SELECT * FROM Drone ORDER BY id_drone DESC LIMIT 10", nativeQuery = true)
    List<Drone> findLast10();

    @Query("SELECT c FROM Drone c ORDER BY c.nome ASC")
    List<Drone> findAllOrderedByName();
}