package br.com.socialconnect.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Estrutura padronizada de erro RFC 7807 (Problem Details)")
public record ProblemDetail(
        @Schema(description = "URI identificando o tipo de erro", example = "https://socialconnect.api/errors/validacao")
        String type,

        @Schema(description = "Título legível do erro", example = "Erro de validação")
        String title,

        @Schema(description = "Código de status HTTP", example = "400")
        int status,

        @Schema(description = "Explicação detalhada do erro", example = "Um ou mais campos são inválidos.")
        String detail,

        @Schema(description = "URI da requisição que originou o erro", example = "/api/v1/beneficiarios")
        String instance,

        @Schema(description = "Data e hora em que o erro ocorreu")
        LocalDateTime timestamp,

        @Schema(description = "Lista de erros específicos por campo")
        List<FieldError> errors
) {
    @Schema(description = "Detalhe do erro de um campo específico")
    public record FieldError(
            @Schema(description = "Nome do campo que falhou na validação", example = "cpf")
            String field,

            @Schema(description = "Mensagem descrevendo a falha", example = "CPF inválido")
            String message
    ) {}
}
