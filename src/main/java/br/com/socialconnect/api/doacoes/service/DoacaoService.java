package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final DoadorRepository doadorRepository;

    public DoacaoService(DoacaoRepository doacaoRepository, DoadorRepository doadorRepository) {
        this.doacaoRepository = doacaoRepository;
        this.doadorRepository = doadorRepository;
    }

    public DoacaoResponseDTO criar(DoacaoRequestDTO dto) {
        doadorRepository.findById(dto.idDoador())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doador não encontrado com o ID: " + dto.idDoador()));

        Doacao entity = Doacao.builder()
                .idDoador(dto.idDoador())
                .dataDoacao(dto.dataDoacao())
                .valor(dto.valor())
                .tipo(dto.tipo())
                .descricao(dto.descricao())
                .build();

        Doacao salva = doacaoRepository.save(entity);
        return toDTO(salva);
    }

    public Page<DoacaoResponseDTO> listar(
            LocalDate dataInicio, LocalDate dataFim, TipoDoacao tipo, Pageable pageable) {

        Page<Doacao> page;

        if (dataInicio != null && dataFim != null && tipo != null) {
            page = doacaoRepository.findByDataDoacaoBetweenAndTipo(dataInicio, dataFim, tipo, pageable);
        } else {
            page = doacaoRepository.findAll(pageable);
        }

        return page.map(this::toDTO);
    }

    private DoacaoResponseDTO toDTO(Doacao entity) {
        return new DoacaoResponseDTO(
                entity.getIdDoacao(),
                entity.getIdDoador(),
                entity.getDataDoacao(),
                entity.getValor(),
                entity.getTipo()
        );
    }
}
