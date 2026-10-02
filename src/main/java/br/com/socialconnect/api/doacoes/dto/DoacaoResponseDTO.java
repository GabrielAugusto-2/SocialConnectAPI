package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "DTO de resposta contendo os dados de uma doação")
public record DoacaoResponseDTO(

        @Schema(description = "Identificador único da doação", example = "1")
        Long idDoacao,

        @Schema(description = "Identificador do doador", example = "10")
        Long idDoador,

        @Schema(description = "Data em que a doação foi realizada", example = "2026-09-18")
        LocalDate dataDoacao,

        @Schema(description = "Valor financeiro da doação", example = "150.00")
        BigDecimal valor,

        @Schema(description = "Tipo da doação realizada", example = "ALIMENTO")
        TipoDoacao tipo
) {}
