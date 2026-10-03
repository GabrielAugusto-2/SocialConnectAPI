package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.validation.EstoqueNaoNegativo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para criação e atualização de produtos")
public record ProdutoRequestDTO(

        @Schema(description = "Nome do produto", example = "Arroz 5kg")
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @Schema(description = "Categoria do produto", example = "ALIMENTO")
        @NotNull(message = "Categoria é obrigatória")
        CategoriaProduto categoria,

        @Schema(description = "Quantidade em estoque no momento", example = "10")
        @NotNull(message = "Estoque atual é obrigatório")
        Integer estoqueAtual,

        @Schema(description = "Quantidade mínima de alerta de reposição", example = "15")
        @NotNull(message = "Estoque mínimo é obrigatório")
        @EstoqueNaoNegativo
        Integer estoqueMinimo,

        @Schema(description = "Unidade de medida do produto", example = "unidade")
        @NotBlank(message = "Unidade de medida é obrigatória")
        @Size(max = 20, message = "Unidade de medida deve ter no máximo 20 caracteres")
        String unidadeMedida
) {}