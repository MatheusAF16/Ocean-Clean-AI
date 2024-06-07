package fiap.com.br.Ocean.Clean.AI.Models;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Drone extends RepresentationModel<Drone>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_drone;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Min(0)
    private int coordenadaX;

    @Min(0)
    private int coordenadaY;

    private int residuosColetados;

    @Min(0)
    @Max(100)
    private int nivelBateria;
    
    @ManyToOne
    @JoinColumn(name = "id_operador")
    private Operador operador;
}

