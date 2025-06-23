# Instruções de Execução - Sistema de Conta Bancária

## Pré-requisitos

1. **Java 17 ou superior**
   - Verifique com: `java -version`
   - Baixe em: https://adoptium.net/

2. **Maven (opcional)**
   - O projeto inclui o Maven Wrapper, então não é necessário instalar o Maven
   - Se preferir instalar: https://maven.apache.org/download.cgi

## Execução Rápida

### 1. Clone o repositório
```bash
git clone [URL_DO_REPOSITORIO]
cd Atividade-ABD_2
```

### 2. Execute o projeto
```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### 3. Acesse a aplicação
- **Console H2:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Usuário: `sa`
  - Senha: `password`

## Testando as APIs

### 1. Listar todas as contas
```bash
curl http://localhost:8080/api/contas
```

### 2. Buscar uma conta específica
```bash
curl http://localhost:8080/api/contas/1
```

### 3. Criar uma nova conta
```bash
curl -X POST http://localhost:8080/api/contas \
  -H "Content-Type: application/json" \
  -d '{"nomeCliente": "Novo Cliente", "saldo": 500.0}'
```

### 4. Fazer um depósito
```bash
curl -X POST "http://localhost:8080/api/contas/1/deposita?valor=100"
```

### 5. Fazer uma retirada
```bash
curl -X POST "http://localhost:8080/api/contas/1/retirada?valor=50"
```

## Executando os Testes

### Testes Unitários
```bash
# Windows
mvnw.cmd test

# Linux/Mac
./mvnw test
```

### Testes de Concorrência
Os testes de concorrência estão implementados e serão executados automaticamente com `mvn test`.

## Testes com JMeter

### 1. Instale o Apache JMeter
- Baixe em: https://jmeter.apache.org/download_jmeter.cgi
- Extraia o arquivo

### 2. Execute o JMeter
```bash
# Windows
jmeter.bat

# Linux/Mac
./jmeter
```

### 3. Abra o arquivo de teste
- Abra o arquivo: `jmeter-tests/ContaBancaria_Load_Test.jmx`
- Configure as variáveis se necessário:
  - `baseUrl`: http://localhost:8080
  - `contaId`: 1

### 4. Execute o teste
- Clique no botão "Start" (▶️)

## Análise dos Resultados

### Sem Versionamento (ContaBancaria)
- Pode apresentar perda de dados em cenários de alta concorrência
- Race conditions podem ocorrer
- O saldo final pode não refletir todas as operações

### Com Versionamento (ContaBancariaVersionada)
- Mantém a integridade dos dados
- Pode ter mais falhas por conflitos de versão otimista
- Garante que todas as operações sejam aplicadas corretamente

## Solução de Problemas

### Erro: "JAVA_HOME not found"
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### Erro: "Port 8080 already in use"
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID [PID] /F

# Linux/Mac
lsof -i :8080
kill -9 [PID]
```

### Erro: "Maven not found"
O projeto usa Maven Wrapper, então não é necessário instalar o Maven. Use:
```bash
# Windows
mvnw.cmd

# Linux/Mac
./mvnw
```

## Estrutura dos Dados

### Tabela: contas_bancarias
- `id` (Long, Primary Key)
- `nome_cliente` (String, Not Null)
- `saldo` (Float, Not Null)

### Tabela: contas_bancarias_versionadas
- `id` (Long, Primary Key)
- `nome_cliente` (String, Not Null)
- `saldo` (Float, Not Null)
- `data_movimento` (LocalDateTime, Version)

## Logs e Monitoramento

Os logs incluem:
- Operações de transação
- Conflitos de versão otimista
- Operações de concorrência
- Performance das APIs

Para ver logs detalhados, execute:
```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.example.contabancaria=DEBUG"
```

## Comandos Úteis

### Compilar o projeto
```bash
mvnw.cmd clean compile
```

### Executar testes específicos
```bash
mvnw.cmd test -Dtest=ContaBancariaConcurrencyTest
```

### Gerar relatório de testes
```bash
mvnw.cmd test jacoco:report
```

### Limpar e reinstalar dependências
```bash
mvnw.cmd clean install
```

## Contato

Para dúvidas ou problemas, consulte o README.md principal ou entre em contato com os autores do projeto. 