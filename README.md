# SocialConnect API

> API RESTful de gestão para instituições sociais (ONGs, bancos de alimentos,
> CRAS, abrigos). Conecta doadores, voluntários e beneficiários.

**Disciplina:** Tópicos Especiais em Sistemas para Internet III
**Stack:** Java 21 · Spring Boot 4.1.1 · JPA · H2 (dev) · PostgreSQL (prod)

---

## Como Rodar

### Pré-requisitos

- JDK 21 LTS ([Adoptium](https://adoptium.net/))
- Maven 3.9+ (ou use o wrapper: `./mvnw`)
- IDE: IntelliJ IDEA (recomendado) ou VS Code

### Passos

```bash
# 1. Clone o repositório
git clone <url-do-repo>
cd socialconnect-api

# 2. Compile o projeto
mvn clean compile

# 3. Rode a aplicação
mvn spring-boot:run
```

---

## Documentação Interativa (Swagger/OpenAPI)

Com a aplicação em execução, acesse a interface interativa do Swagger UI:
- **URL:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Principais Endpoints

### 📦 Módulo de Produtos (`/api/v1/produtos`)

| Método | Endpoint | Descrição | Status Sucesso |
|---|---|---|---|
| `GET` | `/api/v1/produtos` | Lista produtos paginados com filtros opcionais (`nome`, `categoria`) | `200 OK` |
| `GET` | `/api/v1/produtos/{id}` | Busca detalhes de um produto por ID | `200 OK` |
| `POST` | `/api/v1/produtos` | Cadastra um novo produto (cabeçalho `Location` incluído) | `201 Created` |
| `PUT` | `/api/v1/produtos/{id}` | Atualização completa dos dados do produto | `200 OK` |
| `DELETE` | `/api/v1/produtos/{id}` | Exclui o produto especificado | `204 No Content` |