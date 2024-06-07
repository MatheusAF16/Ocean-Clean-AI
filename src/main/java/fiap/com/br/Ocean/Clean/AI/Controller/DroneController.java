package fiap.com.br.Ocean.Clean.AI.Controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fiap.com.br.Ocean.Clean.AI.Models.Drone;
import fiap.com.br.Ocean.Clean.AI.Repository.DroneRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("drones")
@Slf4j
@CacheConfig(cacheNames = "drones")
@Tag(name = "drones")
public class DroneController {

    @Autowired
   
    private DroneRepository droneRepository;
    
    @GetMapping("{id_drone}")
    public Drone show(@PathVariable Long id_drone) {
        var drone = droneRepository.findById(id_drone).orElseThrow(
            () -> new IllegalArgumentException("drone não encontrado")
        );

        Link self = linkTo(methodOn(DroneController.class).show(id_drone)).withSelfRel();
        Link delete = linkTo(methodOn(DroneController.class).destroy(id_drone)).withRel("delete");
        Link contents = linkTo(methodOn(DroneController.class).index()).withRel("contents");

        drone.add(self);
        drone.add(delete);
        drone.add(contents);
 
        return drone;
    }
    
    @GetMapping
    @Operation(
        summary = "Listar todos os drones.",
        description = "Retorna um array com todos os drones no formato do objeto."
    )
    public ResponseEntity<CollectionModel<EntityModel<Drone>>> index() {
        List<EntityModel<Drone>> drones = droneRepository.findAll().stream()
            .map(drone -> EntityModel.of(drone, 
                linkTo(methodOn(DroneController.class).show(drone.getId_drone())).withSelfRel(),
                linkTo(methodOn(DroneController.class).index()).withRel("drones")))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(drones, linkTo(methodOn(DroneController.class).index()).withSelfRel()));
    }
    
    @GetMapping("ultimos")
    @Operation(
        summary = "Listar os últimos 10 drones cadastrados.",
        description = "Retorna um array com todos os últimos 10 drones cadastrados no formato do objeto."
    )
    public List<Drone> getLast10() {
        return droneRepository.findLast10();
    }
    @GetMapping("ordemalfabetica")
    @Operation(
        summary = "Listar todos os drones cadastrados em ordem alfabética.",
        description = "Retorna um array com todos os drones cadastrados em ordem alfabética e no formato do objeto."
    )
    public List<Drone> getOrderedByName() {
        return droneRepository.findAllOrderedByName();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(
        summary = "Registrar um novo drone.",
        description = "Cria um novo drone com os dados enviados no corpo da requisição."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Drone registrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição.")
        }
    )
    public ResponseEntity<EntityModel<Drone>> register(@RequestBody @Valid Drone drone) {
        log.info("Registrando um novo drone {}", drone);
        Drone savedDrone = droneRepository.save(drone);
        EntityModel<Drone> droneModel = EntityModel.of(savedDrone);
        droneModel.add(linkTo(methodOn(DroneController.class).show(savedDrone.getId_drone())).withSelfRel());
        return ResponseEntity.created(droneModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(droneModel);
    }  

    @DeleteMapping("{id_drone}")
    @ResponseStatus(NO_CONTENT)
    @Operation(
        summary = "Deletar um drone pelo ID.",
        description = "Deleta todos os dados de um drone através do ID especificado no parâmetro path."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "Drone apagado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Não existe drone com o `id` informado.")
        }
    )
    public ResponseEntity<Object> destroy(@PathVariable Long id_drone) {
        droneRepository.findById(id_drone).orElseThrow(
            () -> new IllegalArgumentException("drone não encontrado")
        );
        
        droneRepository.deleteById(id_drone);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id_drone}")
    @Operation(
        summary = "Atualizar os dados de um drone pelo ID.",
        description = "Altera os dados do drone especificado no `id`, utilizando as informações enviadas no corpo da requisição."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Drone alterado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição."),
            @ApiResponse(responseCode = "404", description = "Não existe drone com o `id` informado.")
        }
    )
    public ResponseEntity<EntityModel<Drone>> update(@PathVariable Long id_drone, @Valid @RequestBody Drone drone) {
        log.info("Atualizando drone {} para {}", id_drone, drone);

        verificarSeDroneExiste(id_drone);
        drone.setId_drone(id_drone); 

        Drone updatedDrone = droneRepository.save(drone);
        EntityModel<Drone> droneModel = EntityModel.of(updatedDrone);
        droneModel.add(linkTo(methodOn(DroneController.class).show(updatedDrone.getId_drone())).withSelfRel());
        return ResponseEntity.ok(droneModel);
    }

    private void verificarSeDroneExiste(Long id_drone) {
        if (!droneRepository.existsById(id_drone)) {
            throw new ResponseStatusException(NOT_FOUND, "Não existe drone com o id informado");
    }
    }
}