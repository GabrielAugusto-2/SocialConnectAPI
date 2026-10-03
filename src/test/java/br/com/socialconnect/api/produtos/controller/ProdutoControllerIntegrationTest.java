package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ProdutoControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:17"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProdutoRepository produtoRepository;

    @AfterEach
    void tearDown() {
        produtoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar produto quando dados válidos")
    void deveCriarProdutoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o payload válido
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Arroz Branco 5kg",
                CategoriaProduto.ALIMENTO,
                50,
                10,
                "pacote"
        );

        // ==========================================
        // ACT: Executar requisição POST para /api/v1/produtos
        // ==========================================
        ResponseEntity<ProdutoResponseDTO> response = restTemplate.postForEntity(
                "/api/v1/produtos",
                dto,
                ProdutoResponseDTO.class
        );

        // ==========================================
        // ASSERT: Verificar status 201 Created e atributos retornados
        // ==========================================
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Status deve ser 201 Created");
        Assertions.assertNotNull(response.getHeaders().getLocation(), "Header Location deve estar presente");
        Assertions.assertNotNull(response.getBody(), "Corpo da resposta não deve ser nulo");
        Assertions.assertNotNull(response.getBody().idProduto(), "ID do produto deve ser gerado");
        Assertions.assertEquals("Arroz Branco 5kg", response.getBody().nome());
        Assertions.assertEquals(CategoriaProduto.ALIMENTO, response.getBody().categoria());
        Assertions.assertEquals(50, response.getBody().estoqueAtual());
        Assertions.assertEquals(10, response.getBody().estoqueMinimo());
        Assertions.assertFalse(response.getBody().estoqueBaixo(), "Estoque baixo deve ser false (50 >= 10)");
    }

    @Test
    @DisplayName("Deve retornar 409 quando nome duplicado")
    void deveRetornar409QuandoNomeDuplicado() {
        // ==========================================
        // ARRANGE: Salvar previamente um produto no banco com o mesmo nome
        // ==========================================
        produtoRepository.save(
                Produto.builder()
                        .nome("Óleo de Soja 900ml")
                        .categoria(CategoriaProduto.ALIMENTO)
                        .estoqueAtual(20)
                        .estoqueMinimo(5)
                        .unidadeMedida("unidade")
                        .dataCadastro(LocalDate.now())
                        .build()
        );

        ProdutoRequestDTO dtoDuplicado = new ProdutoRequestDTO(
                "Óleo de Soja 900ml",
                CategoriaProduto.ALIMENTO,
                15,
                5,
                "unidade"
        );

        // ==========================================
        // ACT: Tentar criar outro produto com mesmo nome
        // ==========================================
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/produtos",
                dtoDuplicado,
                String.class
        );

        // ==========================================
        // ASSERT: Verificar status 409 Conflict
        // ==========================================
        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Status deve ser 409 Conflict");
        Assertions.assertNotNull(response.getBody(), "Corpo da resposta de erro não deve ser nulo");
        Assertions.assertTrue(
                response.getBody().contains("já cadastrado"),
                "Mensagem deve conter informação de duplicidade: " + response.getBody()
        );
    }

    @Test
    @DisplayName("Deve retornar 422 quando estoque negativo")
    void deveRetornar422QuandoEstoqueNegativo() {
        // ==========================================
        // ARRANGE: Preparar payload com estoqueAtual negativo (-5)
        // ==========================================
        ProdutoRequestDTO dtoEstoqueNegativo = new ProdutoRequestDTO(
                "Feijão Carioca 1kg",
                CategoriaProduto.ALIMENTO,
                -5,
                10,
                "pacote"
        );

        // ==========================================
        // ACT: Executar POST para /api/v1/produtos
        // ==========================================
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/produtos",
                dtoEstoqueNegativo,
                String.class
        );

        // ==========================================
        // ASSERT: Verificar status 422 Unprocessable Entity
        // ==========================================
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode(), "Status deve ser 422 Unprocessable Entity");
        Assertions.assertNotNull(response.getBody(), "Corpo da resposta não deve ser nulo");
        Assertions.assertTrue(
                response.getBody().contains("negativo"),
                "Mensagem deve indicar rejeição por estoque negativo: " + response.getBody()
        );
    }
}
