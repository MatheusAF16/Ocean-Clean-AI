package fiap.com.br.Ocean.Clean.AI.Models;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Operador extends RepresentationModel<Operador> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_operador;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @OneToMany(mappedBy = "operador", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("operador")  
    private List<Missao> missoes;

    @OneToMany(mappedBy = "operador", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("operador")
    private List<Drone> drones;
}
