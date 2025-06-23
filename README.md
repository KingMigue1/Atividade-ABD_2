# Atividade-ABD_2 - Sistema de Conta Bancária com Testes de Concorrência

## Identificação do Trabalho
**Alunos:** [Nome dos Alunos]  
**Disciplina:** Arquitetura e Banco de Dados  
**Professor:** [Nome do Professor]  
**Data:** [Data da Entrega]

## Descrição do Projeto

Este projeto implementa um sistema de conta bancária em Spring Boot com duas abordagens diferentes para controle de concorrência:

1. **ContaBancaria** - Implementação básica sem controle de versão
2. **ContaBancariaVersionada** - Implementação com controle de versão otimista usando `@Version`

### Funcionalidades Implementadas

- **CRUD completo** para ambas as entidades
- **Operações de negócio**: depósito e retirada
- **Testes de concorrência** com múltiplas threads
- **Testes de carga** com JMeter
- **API REST** para todas as operações

## Estrutura do Projeto

```
Atividade-ABD_2/
├── src/
│   ├── main/
│   │   ├── java/com/example/contabancaria/
│   │   │   ├── ContaBancariaApplication.java
│   │   │   ├── config/
│   │   │   │   └── DataInitializer.java
│   │   │   ├── controller/
│   │   │   │   ├── ContaBancariaController.java
│   │   │   │   └── ContaBancariaVersionadaController.java
│   │   │   ├── model/
│   │   │   │   ├── ContaBancaria.java
│   │   │   │   └── ContaBancariaVersionada.java
│   │   │   ├── repository/
│   │   │   │   ├── ContaBancariaRepository.java
│   │   │   │   └── ContaBancariaVersionadaRepository.java
│   │   │   └── service/
│   │   │       ├── ContaBancariaService.java
│   │   │       └── ContaBancariaVersionadaService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/example/contabancaria/
│       │   ├── ContaBancariaConcurrencyTest.java
│       │   └── ContaBancariaVersionadaConcurrencyTest.java
│       └── resources/
│           └── application-test.properties
├── jmeter-tests/
│   └── ContaBancaria_Load_Test.jmx
├── pom.xml
└── README.md
```

## Tecnologias Utilizadas

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Web**
- **H2 Database** (banco em memória)
- **JUnit 5** (testes unitários)
- **Apache JMeter** (testes de carga)
- **Java 17**

## Como Executar o Projeto

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior
- Apache JMeter (opcional, para testes de carga)

### 1. Clone o repositório
```bash
git clone [URL_DO_REPOSITORIO]
cd Atividade-ABD_2
```

### 2. Execute o projeto
```bash
mvn spring-boot:run
```

### 3. Acesse a aplicação
- **Aplicação:** http://localhost:8080
- **Console H2:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Usuário: `sa`
  - Senha: `password`

## APIs Disponíveis

### Conta Bancária (sem versionamento)

#### CRUD Básico
- `GET /api/contas` - Listar todas as contas
- `GET /api/contas/{id}` - Buscar conta por ID
- `POST /api/contas` - Criar nova conta
- `PUT /api/contas/{id}` - Atualizar conta
- `DELETE /api/contas/{id}` - Deletar conta

#### Operações de Negócio
- `POST /api/contas/{id}/deposita?valor=100` - Fazer depósito
- `POST /api/contas/{id}/retirada?valor=50` - Fazer retirada

### Conta Bancária Versionada

#### CRUD Básico
- `GET /api/contas-versionadas` - Listar todas as contas versionadas
- `GET /api/contas-versionadas/{id}` - Buscar conta versionada por ID
- `POST /api/contas-versionadas` - Criar nova conta versionada
- `PUT /api/contas-versionadas/{id}` - Atualizar conta versionada
- `DELETE /api/contas-versionadas/{id}` - Deletar conta versionada

#### Operações de Negócio
- `POST /api/contas-versionadas/{id}/deposita?valor=100` - Fazer depósito
- `POST /api/contas-versionadas/{id}/retirada?valor=50` - Fazer retirada

## Exemplos de Uso

### Criar uma conta
```bash
curl -X POST http://localhost:8080/api/contas \
  -H "Content-Type: application/json" \
  -d '{"nomeCliente": "João Silva", "saldo": 1000.0}'
```

### Fazer um depósito
```bash
curl -X POST "http://localhost:8080/api/contas/1/deposita?valor=100"
```

### Fazer uma retirada
```bash
curl -X POST "http://localhost:8080/api/contas/1/retirada?valor=50"
```

## Executando os Testes

### Testes Unitários
```bash
mvn test
```

### Testes de Concorrência
Os testes de concorrência estão implementados em:
- `ContaBancariaConcurrencyTest.java` - Testa operações concorrentes sem versionamento
- `ContaBancariaVersionadaConcurrencyTest.java` - Testa operações concorrentes com versionamento

### Testes de Carga com JMeter

1. Abra o Apache JMeter
2. Abra o arquivo `jmeter-tests/ContaBancaria_Load_Test.jmx`
3. Configure as variáveis se necessário:
   - `baseUrl`: URL da aplicação (padrão: http://localhost:8080)
   - `contaId`: ID da conta para testar (padrão: 1)
4. Execute o teste

## Análise de Concorrência

### Problemas Identificados

#### Sem Versionamento (ContaBancaria)
- **Race Conditions**: Múltiplas threads podem ler o mesmo valor de saldo
- **Lost Updates**: Atualizações podem ser perdidas quando threads executam simultaneamente
- **Inconsistência de Dados**: O saldo final pode não refletir todas as operações

#### Com Versionamento (ContaBancariaVersionada)
- **Controle de Versão Otimista**: Usa `@Version` para detectar conflitos
- **Detecção de Conflitos**: Lança exceções quando detecta modificações concorrentes
- **Integridade de Dados**: Garante que todas as operações sejam aplicadas corretamente

### Resultados Esperados

1. **ContaBancaria**: Pode apresentar perda de dados em cenários de alta concorrência
2. **ContaBancariaVersionada**: Mantém a integridade dos dados, mas pode ter mais falhas por conflitos de versão

## Configurações do Banco de Dados

O projeto usa H2 Database em memória com as seguintes configurações:
- **URL**: `jdbc:h2:mem:testdb`
- **Usuário**: `sa`
- **Senha**: `password`
- **DDL**: `create-drop` (recria as tabelas a cada execução)

## Logs e Monitoramento

O projeto inclui logs detalhados para:
- Operações de transação
- Conflitos de versão otimista
- Operações de concorrência
- Performance das APIs

## Contribuição

Para contribuir com o projeto:
1. Faça um fork do repositório
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## Licença

Este projeto é parte de uma atividade acadêmica e está disponível para fins educacionais.

## Contato

Para dúvidas ou sugestões, entre em contato com os autores do projeto.
