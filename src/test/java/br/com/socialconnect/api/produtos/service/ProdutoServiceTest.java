package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueInvalidoException;
import br.com.socialconnect.api.exception.NomeProdutoDuplicadoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoServiceImpl service;

    @Test
    @DisplayName("Deve criar produto quando dados válidos")
    void deveCriarProdutoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Feijão Carioca 1kg",
                CategoriaProduto.ALIMENTO,
                20,
                10,
                "kg"
        );

        Mockito.when(repository.existsByNomeIgnoreCase("Feijão Carioca 1kg")).thenReturn(false);

        Produto produtoSalvo = Produto.builder()
                .idProduto(1L)
                .nome("Feijão Carioca 1kg")
                .categoria(CategoriaProduto.ALIMENTO)
                .estoqueAtual(20)
                .estoqueMinimo(10)
                .unidadeMedida("kg")
                .dataCadastro(LocalDate.now())
                .build();

        Mockito.when(repository.save(Mockito.any(Produto.class))).thenReturn(produtoSalvo);

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ProdutoResponseDTO resultado = service.criar(dto);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertNotNull(resultado.idProduto(), "ID do produto deve ser gerado");
        Assertions.assertEquals("Feijão Carioca 1kg", resultado.nome());
        Assertions.assertEquals(20, resultado.estoqueAtual());
        Assertions.assertFalse(resultado.estoqueBaixo(), "Estoque baixo deve ser false quando estoqueAtual >= estoqueMinimo");
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque negativo")
    void deveLancarExcecaoQuandoEstoqueNegativo() {
        // ==========================================
        // ARRANGE: Preparar cenário com estoque inválido (< 0)
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Sabonete Neutro",
                CategoriaProduto.HIGIENE,
                -5,
                10,
                "unidade"
        );

        // ==========================================
        // ACT & ASSERT: Executar e verificar exceção
        // ==========================================
        Assertions.assertThrows(EstoqueInvalidoException.class, () -> service.criar(dto));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome duplicado")
    void deveLancarExcecaoQuandoNomeDuplicado() {
        // ==========================================
        // ARRANGE: Simular produto já existente com o mesmo nome
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Arroz 5kg",
                CategoriaProduto.ALIMENTO,
                15,
                5,
                "unidade"
        );

        Mockito.when(repository.existsByNomeIgnoreCase("Arroz 5kg")).thenReturn(true);

        // ==========================================
        // ACT & ASSERT: Executar e verificar exceção
        // ==========================================
        Assertions.assertThrows(NomeProdutoDuplicadoException.class, () -> service.criar(dto));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Produto.class));
    }
}
