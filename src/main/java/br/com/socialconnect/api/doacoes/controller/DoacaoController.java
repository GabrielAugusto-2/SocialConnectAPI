package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.service.DoacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/doacoes")
@Tag(name = "Doações", description = "API para consulta e gestão de doações")
public class DoacaoController {

    private final DoacaoService service;

    public DoacaoController(DoacaoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista doações com filtros avançados", description = "Retorna lista paginada de doações com filtros opcionais por período e tipo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de doações retornada com sucesso")
    })
    public ResponseEntity<Page<DoacaoResponseDTO>> listar(
            @Parameter(description = "Data inicial do período de doação", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @Parameter(description = "Data final do período de doação", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

            @Parameter(description = "Tipo da doação realizada", example = "ALIMENTO")
            @RequestParam(required = false) TipoDoacao tipo,

            @PageableDefault(size = 10, sort = "dataDoacao", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(service.listar(dataInicio, dataFim, tipo, pageable));
    }

    @PostMapping
    @Operation(summary = "Registra uma nova doação", description = "Cadastra uma doação vinculada a um doador existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Doação registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Doador não encontrado")
    })
    public ResponseEntity<DoacaoResponseDTO> criar(
            @Valid @RequestBody DoacaoRequestDTO dto) {
        DoacaoResponseDTO salva = service.criar(dto);
        URI location = URI.create("/api/v1/doacoes/" + salva.idDoacao());
        return ResponseEntity.created(location).body(salva);
    }
}
