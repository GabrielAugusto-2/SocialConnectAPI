package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "API para gestão de produtos e controle de estoque de doações físicas")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista produtos com paginação e filtros opcionais", description = "Retorna uma página de produtos filtrados por nome e/ou categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<ProdutoResponseDTO>> listar(
            @Parameter(description = "Nome para busca parcial", example = "Arroz")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Categoria do produto", example = "ALIMENTO")
            @RequestParam(required = false) CategoriaProduto categoria,

            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        return ResponseEntity.ok(service.listar(nome, categoria, pageable));
    }

    @GetMapping("/{id_produto}")
    @Operation(summary = "Busca produto por ID", description = "Retorna os detalhes de um produto específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @Parameter(description = "ID do produto", example = "1")
            @PathVariable("id_produto") Long idProduto) {

        return ResponseEntity.ok(service.buscarPorId(idProduto));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo produto", description = "Cria um novo produto com validações de unicidade e estoque não negativo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Produto com o mesmo nome já cadastrado"),
            @ApiResponse(responseCode = "422", description = "Estoque informado é inválido ou negativo")
    })
    public ResponseEntity<ProdutoResponseDTO> criar(
            @Valid @RequestBody ProdutoRequestDTO dto) {

        ProdutoResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/produtos/" + salvo.idProduto());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{id_produto}")
    @Operation(summary = "Atualiza totalmente um produto", description = "Substitui todos os dados de um produto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Nome já utilizado por outro produto"),
            @ApiResponse(responseCode = "422", description = "Estoque informado é inválido ou negativo")
    })
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @Parameter(description = "ID do produto a ser atualizado", example = "1")
            @PathVariable("id_produto") Long idProduto,
            @Valid @RequestBody ProdutoRequestDTO dto) {

        return ResponseEntity.ok(service.atualizar(idProduto, dto));
    }

    @DeleteMapping("/{id_produto}")
    @Operation(summary = "Remove um produto", description = "Exclui um produto do estoque pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do produto a ser removido", example = "1")
            @PathVariable("id_produto") Long idProduto) {

        service.deletar(idProduto);
        return ResponseEntity.noContent().build();
    }
}
