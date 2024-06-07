package fiap.com.br.Ocean.Clean.AI.Models;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class ResiduoPlastico extends RepresentationModel<ResiduoPlastico> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_residuo;

    @Min(0)
    private int coordenadaX;

    @Min(0)
    private int coordenadaY;

    @ManyToOne
    @JoinColumn(name = "id_missao")
    private Missao missao;

}
