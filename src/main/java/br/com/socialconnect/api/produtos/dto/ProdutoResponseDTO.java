package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO de resposta detalhando os dados do produto e alerta de estoque")
public record ProdutoResponseDTO(

        @Schema(description = "Identificador único do produto", example = "1")
        Long idProduto,

        @Schema(description = "Nome do produto", example = "Arroz 5kg")
        String nome,

        @Schema(description = "Categoria do produto", example = "ALIMENTO")
        CategoriaProduto categoria,

        @Schema(description = "Quantidade atual em estoque", example = "3")
        Integer estoqueAtual,

        @Schema(description = "Quantidade mínima aceitável", example = "10")
        Integer estoqueMinimo,

        @Schema(description = "Unidade de medida", example = "unidade")
        String unidadeMedida,

        @Schema(description = "Data de cadastro do produto", example = "2026-10-02")
        LocalDate dataCadastro,

        @Schema(description = "Indica se o estoque está abaixo do mínimo (estoqueAtual < estoqueMinimo)", example = "true")
        boolean estoqueBaixo
) {

    public static ProdutoResponseDTO fromEntity(Produto p) {
        boolean baixo = p.getEstoqueAtual() < p.getEstoqueMinimo();
        return new ProdutoResponseDTO(
                p.getIdProduto(),
                p.getNome(),
                p.getCategoria(),
                p.getEstoqueAtual(),
                p.getEstoqueMinimo(),
                p.getUnidadeMedida(),
                p.getDataCadastro(),
                baixo
        );
    }
}