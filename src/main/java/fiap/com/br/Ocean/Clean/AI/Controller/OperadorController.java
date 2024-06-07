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

import fiap.com.br.Ocean.Clean.AI.Models.Operador;
import fiap.com.br.Ocean.Clean.AI.Repository.OperadorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("operadores")
@Slf4j
@CacheConfig(cacheNames = "operadores")
@Tag(name = "operadores")
public class OperadorController {

    @Autowired
    private OperadorRepository operadorRepository;

    @GetMapping("{id_operador}")
    public Operador show(@PathVariable Long id_operador) {
        var operador = operadorRepository.findById(id_operador).orElseThrow(
            () -> new IllegalArgumentException("Operador não encontrado")
        );

        Link self = linkTo(methodOn(OperadorController.class).show(id_operador)).withSelfRel();
        Link delete = linkTo(methodOn(OperadorController.class).destroy(id_operador)).withRel("delete");
        Link contents = linkTo(methodOn(OperadorController.class).index()).withRel("contents");

        operador.add(self);
        operador.add(delete);
        operador.add(contents);

        return operador;
    }

    @GetMapping
    @Operation(
        summary = "Listar todos os operadores.",
        description = "Retorna um array com todos os operadores no formato do objeto."
    )
    public ResponseEntity<CollectionModel<EntityModel<Operador>>> index() {
        List<EntityModel<Operador>> operadores = operadorRepository.findAll().stream()
            .map(operador -> EntityModel.of(operador, 
                linkTo(methodOn(OperadorController.class).show(operador.getId_operador())).withSelfRel(),
                linkTo(methodOn(OperadorController.class).index()).withRel("operadores")))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(operadores, linkTo(methodOn(OperadorController.class).index()).withSelfRel()));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(
        summary = "Registrar um novo operador.",
        description = "Cria um novo operador com os dados enviados no corpo da requisição."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Operador registrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição.")
        }
    )
    public ResponseEntity<EntityModel<Operador>> register(@RequestBody @Valid Operador operador) {
        log.info("Registrando um novo operador {}", operador);
        Operador savedOperador = operadorRepository.save(operador);
        EntityModel<Operador> operadorModel = EntityModel.of(savedOperador);
        operadorModel.add(linkTo(methodOn(OperadorController.class).show(savedOperador.getId_operador())).withSelfRel());
        return ResponseEntity.created(operadorModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(operadorModel);
    }

    @DeleteMapping("{id_operador}")
    @ResponseStatus(NO_CONTENT)
    @Operation(
        summary = "Deletar um operador pelo ID.",
        description = "Deleta todos os dados de um operador através do ID especificado no parâmetro path."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "Operador apagado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Não existe operador com o `id` informado.")
        }
    )
    public ResponseEntity<Object> destroy(@PathVariable Long id_operador) {
        operadorRepository.findById(id_operador).orElseThrow(
            () -> new IllegalArgumentException("Operador não encontrado")
        );

        operadorRepository.deleteById(id_operador);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id_operador}")
    @Operation(
        summary = "Atualizar os dados de um operador pelo ID.",
        description = "Altera os dados do operador especificado no `id`, utilizando as informações enviadas no corpo da requisição."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Operador alterado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição."),
            @ApiResponse(responseCode = "404", description = "Não existe operador com o `id` informado.")
        }
    )
    public ResponseEntity<EntityModel<Operador>> update(@PathVariable Long id_operador, @Valid @RequestBody Operador operador) {
        log.info("Atualizando operador {} para {}", id_operador, operador);

        verificarSeOperadorExiste(id_operador);
        operador.setId_operador(id_operador);

        Operador updatedOperador = operadorRepository.save(operador);
        EntityModel<Operador> operadorModel = EntityModel.of(updatedOperador);
        operadorModel.add(linkTo(methodOn(OperadorController.class).show(updatedOperador.getId_operador())).withSelfRel());
        return ResponseEntity.ok(operadorModel);
    }

    private void verificarSeOperadorExiste(Long id_operador) {
        if (!operadorRepository.existsById(id_operador)) {
            throw new ResponseStatusException(NOT_FOUND, "Não existe operador com o id informado");
        }
    }
}
