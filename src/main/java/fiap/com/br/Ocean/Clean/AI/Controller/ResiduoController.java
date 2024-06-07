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

import fiap.com.br.Ocean.Clean.AI.Models.ResiduoPlastico;
import fiap.com.br.Ocean.Clean.AI.Repository.ResiduoPlasticoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("residuos-plasticos")
@Slf4j
@CacheConfig(cacheNames = "residuosPlasticos")
@Tag(name = "residuosPlasticos")
public class ResiduoController {

    @Autowired
    private ResiduoPlasticoRepository residuoPlasticoRepository;

    @GetMapping("{id_residuo}")
    public ResiduoPlastico show(@PathVariable Long id_residuo) {
        var residuo = residuoPlasticoRepository.findById(id_residuo).orElseThrow(
            () -> new IllegalArgumentException("Resíduo plástico não encontrado")
        );

        Link self = linkTo(methodOn(ResiduoController.class).show(id_residuo)).withSelfRel();
        Link delete = linkTo(methodOn(ResiduoController.class).destroy(id_residuo)).withRel("delete");
        Link contents = linkTo(methodOn(ResiduoController.class).index()).withRel("contents");

        residuo.add(self);
        residuo.add(delete);
        residuo.add(contents);

        return residuo;
    }

    @GetMapping
    @Operation(
        summary = "Listar todos os resíduos plásticos.",
        description = "Retorna um array com todos os resíduos plásticos no formato do objeto."
    )
    public ResponseEntity<CollectionModel<EntityModel<ResiduoPlastico>>> index() {
        List<EntityModel<ResiduoPlastico>> residuos = residuoPlasticoRepository.findAll().stream()
            .map(residuo -> EntityModel.of(residuo, 
                linkTo(methodOn(ResiduoController.class).show(residuo.getId_residuo())).withSelfRel(),
                linkTo(methodOn(ResiduoController.class).index()).withRel("residuosPlasticos")))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(residuos, linkTo(methodOn(ResiduoController.class).index()).withSelfRel()));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(
        summary = "Registrar um novo resíduo plástico.",
        description = "Cria um novo resíduo plástico com os dados enviados no corpo da requisição."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Resíduo plástico registrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição.")
        }
    )
    public ResponseEntity<EntityModel<ResiduoPlastico>> register(@RequestBody @Valid ResiduoPlastico residuo) {
        log.info("Registrando um novo resíduo plástico {}", residuo);
        ResiduoPlastico savedResiduo = residuoPlasticoRepository.save(residuo);
        EntityModel<ResiduoPlastico> residuoModel = EntityModel.of(savedResiduo);
        residuoModel.add(linkTo(methodOn(ResiduoController.class).show(savedResiduo.getId_residuo())).withSelfRel());
        return ResponseEntity.created(residuoModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(residuoModel);
    }

    @DeleteMapping("{id_residuo}")
    @ResponseStatus(NO_CONTENT)
    @Operation(
        summary = "Deletar um resíduo plástico pelo ID.",
        description = "Deleta todos os dados de um resíduo plástico através do ID especificado no parâmetro path."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "Resíduo plástico apagado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Não existe resíduo plástico com o `id` informado.")
        }
    )
    public ResponseEntity<Object> destroy(@PathVariable Long id_residuo) {
        residuoPlasticoRepository.findById(id_residuo).orElseThrow(
            () -> new IllegalArgumentException("Resíduo plástico não encontrado")
        );

        residuoPlasticoRepository.deleteById(id_residuo);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id_residuo}")
    @Operation(
        summary = "Atualizar os dados de um resíduo plástico pelo ID.",
        description = "Altera os dados do resíduo plástico especificado no `id`, utilizando as informações enviadas no corpo da requisição."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Resíduo plástico alterado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição."),
            @ApiResponse(responseCode = "404", description = "Não existe resíduo plástico com o `id` informado.")
        }
    )
    public ResponseEntity<EntityModel<ResiduoPlastico>> update(@PathVariable Long id_residuo, @Valid @RequestBody ResiduoPlastico residuo) {
        log.info("Atualizando resíduo plástico {} para {}", id_residuo, residuo);

        verificarSeResiduoExiste(id_residuo);
        residuo.setId_residuo(id_residuo);

        ResiduoPlastico updatedResiduo = residuoPlasticoRepository.save(residuo);
        EntityModel<ResiduoPlastico> residuoModel = EntityModel.of(updatedResiduo);
        residuoModel.add(linkTo(methodOn(ResiduoController.class).show(updatedResiduo.getId_residuo())).withSelfRel());
        return ResponseEntity.ok(residuoModel);
    }

    private void verificarSeResiduoExiste(Long id_residuo) {
        if (!residuoPlasticoRepository.existsById(id_residuo)) {
            throw new ResponseStatusException(NOT_FOUND, "Não existe resíduo plástico com o id informado");
        }
    }
}
