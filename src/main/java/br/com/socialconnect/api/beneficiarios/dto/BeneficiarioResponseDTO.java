package br.com.socialconnect.api.beneficiarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record BeneficiarioResponseDTO(

        @Schema(description = "Identificador único do beneficiário", example = "1")
        Long idBeneficiario,

        @Schema(description = "Nome completo do beneficiário", example = "Maria da Silva")
        String nome,

        @Schema(description = "CPF do beneficiário", example = "52998224725")
        String cpf,

        @Schema(description = "Telefone para contato", example = "11999999999")
        String telefone,

        @Schema(description = "Endereço completo", example = "Rua das Flores, 123")
        String endereco,

        @Schema(description = "Situação de vulnerabilidade informada", example = "Renda familiar baixa")
        String situacaoVulnerabilidade,

        @Schema(description = "Data em que o cadastro foi realizado", example = "2026-09-18")
        LocalDate dataCadastro
) {}
