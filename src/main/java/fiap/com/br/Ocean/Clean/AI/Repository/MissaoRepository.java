package fiap.com.br.Ocean.Clean.AI.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fiap.com.br.Ocean.Clean.AI.Models.Missao;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface MissaoRepository extends JpaRepository<Missao, Long> {
    
    @Query(value = "SELECT * FROM Missao ORDER BY id_missao DESC LIMIT 10", nativeQuery = true)
    List<Missao> findLast10();

    @Query("SELECT c FROM Missao c ORDER BY c.nome ASC")
    List<Missao> findAllOrderedByName();
}

