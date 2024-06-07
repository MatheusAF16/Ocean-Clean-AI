package fiap.com.br.Ocean.Clean.AI.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fiap.com.br.Ocean.Clean.AI.Models.Operador;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface OperadorRepository extends JpaRepository<Operador, Long> {
    
    @Query(value = "SELECT * FROM Operador ORDER BY id_operador DESC LIMIT 10", nativeQuery = true)
    List<Operador> findLast10();

    @Query("SELECT c FROM Operador c ORDER BY c.nome ASC")
    List<Operador> findAllOrderedByName();

}
