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

import fiap.com.br.Ocean.Clean.AI.Models.Missao;
import fiap.com.br.Ocean.Clean.AI.Repository.MissaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("missoes")
@Slf4j
@CacheConfig(cacheNames = "missoes")
@Tag(name = "missoes")
public class MissaoController {

    @Autowired
    private MissaoRepository missaoRepository;

    @GetMapping("{id_missao}")
    public Missao show(@PathVariable Long id_missao) {
        var missao = missaoRepository.findById(id_missao).orElseThrow(
            () -> new IllegalArgumentException("Missão não encontrada")
        );

        Link self = linkTo(methodOn(MissaoController.class).show(id_missao)).withSelfRel();
        Link delete = linkTo(methodOn(MissaoController.class).destroy(id_missao)).withRel("delete");
        Link contents = linkTo(methodOn(MissaoController.class).index()).withRel("contents");

        missao.add(self);
        missao.add(delete);
        missao.add(contents);

        return missao;
    }

    @GetMapping
    @Operation(
        summary = "Listar todas as missões.",
        description = "Retorna um array com todas as missões no formato do objeto."
    )
    public ResponseEntity<CollectionModel<EntityModel<Missao>>> index() {
        List<EntityModel<Missao>> missoes = missaoRepository.findAll().stream()
            .map(missao -> EntityModel.of(missao, 
                linkTo(methodOn(MissaoController.class).show(missao.getId_missao())).withSelfRel(),
                linkTo(methodOn(MissaoController.class).index()).withRel("missoes")))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(missoes, linkTo(methodOn(MissaoController.class).index()).withSelfRel()));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(
        summary = "Registrar uma nova missão.",
        description = "Cria uma nova missão com os dados enviados no corpo da requisição."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Missão registrada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição.")
        }
    )
    public ResponseEntity<EntityModel<Missao>> register(@RequestBody @Valid Missao missao) {
        log.info("Registrando uma nova missão {}", missao);
        Missao savedMissao = missaoRepository.save(missao);
        EntityModel<Missao> missaoModel = EntityModel.of(savedMissao);
        missaoModel.add(linkTo(methodOn(MissaoController.class).show(savedMissao.getId_missao())).withSelfRel());
        return ResponseEntity.created(missaoModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(missaoModel);
    }

    @DeleteMapping("{id_missao}")
    @ResponseStatus(NO_CONTENT)
    @Operation(
        summary = "Deletar uma missão pelo ID.",
        description = "Deleta todos os dados de uma missão através do ID especificado no parâmetro path."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "Missão apagada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Não existe missão com o `id` informado.")
        }
    )
    public ResponseEntity<Object> destroy(@PathVariable Long id_missao) {
        missaoRepository.findById(id_missao).orElseThrow(
            () -> new IllegalArgumentException("Missão não encontrada")
        );

        missaoRepository.deleteById(id_missao);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id_missao}")
    @Operation(
        summary = "Atualizar os dados de uma missão pelo ID.",
        description = "Altera os dados da missão especificada no `id`, utilizando as informações enviadas no corpo da requisição."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Missão alterada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição."),
            @ApiResponse(responseCode = "404", description = "Não existe missão com o `id` informado.")
        }
    )
    public ResponseEntity<EntityModel<Missao>> update(@PathVariable Long id_missao, @Valid @RequestBody Missao missao) {
        log.info("Atualizando missão {} para {}", id_missao, missao);

        verificarSeMissaoExiste(id_missao);
        missao.setId_missao(id_missao);

        Missao updatedMissao = missaoRepository.save(missao);
        EntityModel<Missao> missaoModel = EntityModel.of(updatedMissao);
        missaoModel.add(linkTo(methodOn(MissaoController.class).show(updatedMissao.getId_missao())).withSelfRel());
        return ResponseEntity.ok(missaoModel);
    }

    private void verificarSeMissaoExiste(Long id_missao) {
        if (!missaoRepository.existsById(id_missao)) {
            throw new ResponseStatusException(NOT_FOUND, "Não existe missão com o id informado");
        }
    }
}
