package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueInvalidoException;
import br.com.socialconnect.api.exception.NomeProdutoDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoServiceImpl(ProdutoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
        Page<Produto> page;

        if (nome != null && !nome.isBlank() && categoria != null) {
            page = repository.findByNomeContainingIgnoreCaseAndCategoria(nome, categoria, pageable);
        } else if (nome != null && !nome.isBlank()) {
            page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else if (categoria != null) {
            page = repository.findByCategoria(categoria, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        return page.map(ProdutoResponseDTO::fromEntity);
    }

    @Override
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com o ID: " + id));
        return ProdutoResponseDTO.fromEntity(produto);
    }

    @Override
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        // Regra 1: Estoque não negativo (422 Unprocessable Entity)
        validarEstoque(dto.estoqueAtual());

        // Regra 2: Nome único (409 Conflict)
        if (repository.existsByNomeIgnoreCase(dto.nome())) {
            throw new NomeProdutoDuplicadoException(dto.nome());
        }

        Produto produto = Produto.builder()
                .nome(dto.nome())
                .categoria(dto.categoria())
                .estoqueAtual(dto.estoqueAtual())
                .estoqueMinimo(dto.estoqueMinimo())
                .unidadeMedida(dto.unidadeMedida())
                .dataCadastro(LocalDate.now())
                .build();

        return ProdutoResponseDTO.fromEntity(repository.save(produto));
    }

    @Override
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com o ID: " + id));

        // Regra 1: Estoque não negativo (422 Unprocessable Entity)
        validarEstoque(dto.estoqueAtual());

        // Regra 2: Nome único para outros produtos (409 Conflict)
        if (repository.existsByNomeIgnoreCaseAndIdProdutoNot(dto.nome(), id)) {
            throw new NomeProdutoDuplicadoException(dto.nome());
        }

        produto.setNome(dto.nome());
        produto.setCategoria(dto.categoria());
        produto.setEstoqueAtual(dto.estoqueAtual());
        produto.setEstoqueMinimo(dto.estoqueMinimo());
        produto.setUnidadeMedida(dto.unidadeMedida());

        return ProdutoResponseDTO.fromEntity(repository.save(produto));
    }

    @Override
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }

    private void validarEstoque(Integer estoqueAtual) {
        if (estoqueAtual != null && estoqueAtual < 0) {
            throw new EstoqueInvalidoException("O estoque atual não pode ser negativo. Informado: " + estoqueAtual);
        }
    }
}
