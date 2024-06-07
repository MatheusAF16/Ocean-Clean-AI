package fiap.com.br.Ocean.Clean.AI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fiap.com.br.Ocean.Clean.AI.Models.ResiduoPlastico;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface ResiduoPlasticoRepository extends JpaRepository<ResiduoPlastico, Long> {
}
