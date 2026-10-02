package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class DoacaoControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:17"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DoadorRepository doadorRepository;

    @Test
    @DisplayName("Deve criar doação quando dados válidos")
    void deveCriarDoacaoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        Doador doador = doadorRepository.save(
                Doador.builder()
                        .nome("Empresa Amiga")
                        .tipo("PESSOA_JURIDICA")
                        .build()
        );

        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                doador.getIdDoador(),
                LocalDate.now(),
                new BigDecimal("250.00"),
                TipoDoacao.FINANCEIRA,
                "Doação mensal para alimentação"
        );

        // ==========================================
        // ACT: Executar a requisição POST
        // ==========================================
        ResponseEntity<DoacaoResponseDTO> response = restTemplate.postForEntity(
                "/api/v1/doacoes",
                dto,
                DoacaoResponseDTO.class
        );

        // ==========================================
        // ASSERT: Verificar status 201 e dados retornados
        // ==========================================
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Status deve ser 201 Created");
        Assertions.assertNotNull(response.getBody(), "Corpo da resposta não deve ser nulo");
        Assertions.assertNotNull(response.getBody().idDoacao(), "ID da doação deve ser gerado");
        Assertions.assertEquals(doador.getIdDoador(), response.getBody().idDoador());
        Assertions.assertEquals(new BigDecimal("250.00"), response.getBody().valor());
    }

    @Test
    @DisplayName("Deve retornar 400 quando data da doação for no futuro")
    void deveRetornar400QuandoDataFutura() {
        // ==========================================
        // ARRANGE: Preparar o cenário com data futura
        // ==========================================
        Doador doador = doadorRepository.save(
                Doador.builder()
                        .nome("Doador Comum")
                        .tipo("PESSOA_FISICA")
                        .build()
        );

        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                doador.getIdDoador(),
                LocalDate.now().plusDays(10), // Data no futuro (inválida)
                new BigDecimal("100.00"),
                TipoDoacao.ALIMENTO,
                "Doação agendada"
        );

        // ==========================================
        // ACT: Executar a requisição POST
        // ==========================================
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/doacoes",
                dto,
                String.class
        );

        // ==========================================
        // ASSERT: Verificar status 400 e mensagem contendo "futuro"
        // ==========================================
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status deve ser 400 Bad Request");
        Assertions.assertNotNull(response.getBody(), "Corpo da resposta não deve ser nulo");
        Assertions.assertTrue(
                response.getBody().toLowerCase().contains("futuro"),
                "Mensagem de erro deve conter menção a data futura. Resposta recebida: " + response.getBody()
        );
    }
}
