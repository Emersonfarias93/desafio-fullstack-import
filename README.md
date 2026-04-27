# Desafio Fullstack - Importação de Leads

Aplicação fullstack para importar arquivos CSV de leads, processar os dados em lote, acompanhar o andamento em tempo real e disponibilizar auditoria das linhas importadas.

## Stack

- Java 17
- Spring Boot 4
- Spring Data JPA
- Kafka
- WebSocket
- PostgreSQL
- React + Vite
- Docker Compose

## Arquitetura

O projeto foi estruturado como uma aplicação fullstack em camadas, separando interface, orquestração, regras de negócio, persistência e comunicação assíncrona.

### Frontend

O frontend foi construído como uma SPA em React com Vite. A composição principal fica em `frontend/src/App.jsx`, responsável por definir a tela ativa, montar o layout global da aplicação e conectar o estado compartilhado às páginas.

A orquestração da interface fica centralizada no hook `frontend/src/hooks/useImportWorkspace.js`. Esse hook concentra:

- carregamento de dashboard, lotes, itens e leads
- controle de estado global da operação
- integração com a API REST
- atualização em tempo real via WebSocket
- feedback visual para o usuário

As telas foram separadas por contexto de uso em `frontend/src/pages`, com áreas específicas para dashboard, importação, processamento, mensageria, auditoria, arquitetura e consulta de leads. Os elementos reutilizáveis ficam em `frontend/src/components`, como layout, dialog, toast, tabelas, paginação, painel de status e seletor de lotes.

Em termos arquiteturais, o frontend segue um modelo de composição por páginas com estado compartilhado centralizado em hook customizado, integração com backend via REST e atualização em tempo real por WebSocket.

### Backend

O backend foi implementado como um monólito Spring Boot organizado em camadas:

- `controller`: entrada HTTP e exposição dos endpoints
- `service`: regras de negócio e orquestração dos casos de uso
- `repository`: acesso a dados com Spring Data JPA
- `model/dto`: contratos de entrada e saída da API
- `messaging` e `websocket`: processamento assíncrono e notificação em tempo real

O fluxo principal de importação começa em `LoteController`, que recebe o upload do CSV. A regra de negócio segue para `CsvImportService`, responsável por:

1. validar arquivo, cabeçalho, codificação e formato
2. salvar o arquivo no disco
3. fazer a pré-validação das linhas
4. persistir os itens do lote
5. recalcular totais iniciais
6. iniciar o processamento do lote

Depois da pré-validação, o processamento pesado sai do request HTTP e passa para um fluxo assíncrono orientado a eventos. O backend usa Kafka para publicar e consumir eventos como início do lote, conclusão de chunk e finalização do processamento. Isso desacopla a requisição web do trabalho intensivo e permite acompanhar o andamento sem bloquear o usuário.

O status consolidado do lote é enviado ao frontend em tempo real por WebSocket. Assim, a interface consegue atualizar progresso, totais e situação do lote sem recarregar a página.

### Visão geral da solução

O desenho da aplicação segue este fluxo:

1. o frontend envia a ação via REST
2. o backend recebe, valida e persiste os dados iniciais
3. o processamento pesado é delegado para fluxo assíncrono
4. os resultados parciais e finais atualizam o estado do lote
5. o frontend recebe o status em tempo real por WebSocket

Essa arquitetura foi adotada para atender três objetivos principais:

- separar responsabilidades de forma clara entre interface, negócio e persistência
- manter a experiência do usuário responsiva mesmo com arquivos grandes
- permitir evolução do processamento concorrente sem acoplar tudo ao ciclo de uma única requisição HTTP

### Ponto de evolução

O principal ponto de evolução no estado atual do projeto está no frontend: o hook `useImportWorkspace` já concentra bastante responsabilidade e, em uma próxima iteração, pode ser dividido em hooks menores por domínio, como lotes, leads, dashboard e notificações. Mesmo assim, a estrutura atual já oferece boa clareza para manutenção e apresentação técnica.

## Fluxo implementado

1. O usuário envia um CSV pelo frontend.
2. O backend valida arquivo obrigatório, extensão, content type, tamanho, UTF-8 e cabeçalho esperado.
3. O arquivo é salvo em disco no diretório configurado por `IMPORT_UPLOAD_DIR`.
4. As linhas são pré-validadas e gravadas em `lote_itens`.
5. O backend marca o lote como `PROCESSANDO` e publica `lote.iniciado` automaticamente.
6. O consumer interno particiona os itens válidos em chunks de `IMPORT_CHUNK_SIZE`.
7. Cada chunk roda no pool configurado por `IMPORT_THREAD_POOL_SIZE`.
8. A inserção de leads usa `ON CONFLICT (email) DO NOTHING`, garantindo deduplicação mesmo com processamentos simultâneos.
9. Cada chunk publica `lote.chunk.concluido`.
10. O consumer recalcula totais do lote e envia status via WebSocket.
11. Ao final, `lote.finalizado` atualiza o lote e notifica o frontend.

## CSV esperado

```csv
nome,email,telefone,origem,data_cadastro
Maria Silva,maria@email.com,+55 (83) 99999-9999,Parceiro A,17/04/2026 05:13:48
```

Colunas obrigatórias:

- `nome`
- `email`
- `telefone`
- `origem`
- `data_cadastro`

Formatos aceitos para `data_cadastro`:

- `dd/MM/yyyy HH:mm:ss`
- `dd/MM/yyyy`
- ISO local date time
- ISO local date

## Como executar com Docker

Na raiz do projeto:

1. Crie o arquivo `.env` a partir do modelo `.env.example`.

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

No Linux ou macOS:

```bash
cp .env.example .env
```

2. Revise os valores do arquivo `.env` se precisar ajustar portas, banco ou Kafka.
3. Suba tudo com um único comando:

```bash
docker compose up --build
```

Serviços:

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Documentação Scalar: http://localhost:8080/scalar
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- PostgreSQL externo para IDE/host: `localhost:5432`
- PostgreSQL interno para containers: `postgres:5432`
- Kafka externo para IDE/host: `localhost:29092`
- Kafka interno para containers: `kafka:9092`

Variáveis principais:

```env
POSTGRES_DB_NAME=leads_db
POSTGRES_DB_USER=postgres
POSTGRES_DB_PASSWORD=postgres
POSTGRES_PORT=5432

BACKEND_PORT=8080
FRONTEND_PORT=3000
KAFKA_EXTERNAL_PORT=29092

DB_MAX_POOL_SIZE=20
DB_MIN_IDLE=5
DB_CONNECTION_TIMEOUT=30000

APP_KAFKA_ENABLED=true
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
KAFKA_CONSUMER_GROUP_ID=backend-lote-consumer

IMPORT_UPLOAD_DIR=/app/uploads
IMPORT_CHUNK_SIZE=2000
IMPORT_THREAD_POOL_SIZE=8
```

## Como executar localmente

Suba PostgreSQL e Kafka, depois execute:

```bash
cd backend
./mvnw spring-boot:run
```

Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

Se o frontend estiver em outra origem, ajuste:

```env
VITE_API_URL=http://localhost:8080
```

### Debug local do backend

Se quiser depurar endpoints usando sua base local/PostgreSQL, rode o backend normalmente e, se precisar isolar o Kafka, desative-o por propriedade:

```bash
cd backend
./mvnw spring-boot:run -Dapp.kafka.enabled=false
```

Assim você continua usando o banco real, mas evita travamento de boot por tentativa de conexão com o broker durante o debug.

Se o Kafka estiver subido via `docker compose`, a aplicação rodando na IDE deve usar `localhost:29092`.
Se o PostgreSQL estiver subido via `docker compose`, a aplicação rodando na IDE deve usar `localhost:5432`.

## Endpoints principais

- `POST /api/lotes` - upload multipart no campo `arquivo`
- `GET /api/lotes` - lista lotes paginados
- `GET /api/lotes/{id}/status` - status consolidado do lote
- `GET /api/lotes/{id}/itens` - linhas do lote com filtro opcional por `status`
- `PATCH /api/lotes/{id}/itens/{itemId}` - corrige ou ignora uma linha
- `GET /api/leads` - busca paginada por `nome`, `email` e `origem`
- `GET /api/dashboard` - total de leads, lotes e taxa de erro
- `WS /ws/lotes` - notificações de status em tempo real

## Documentação da API

O backend disponibiliza documentação interativa com Scalar a partir do OpenAPI gerado automaticamente pela aplicação.

- Scalar UI: `http://localhost:8080/scalar`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Decisões técnicas

- O upload valida e dispara o processamento imediatamente, sem etapa manual de confirmação.
- A deduplicação forte fica no banco, por email único, e a aplicação usa insert idempotente com `ON CONFLICT DO NOTHING`.
- O processamento é separado em eventos Kafka para demonstrar mensageria real: início, chunk concluído e lote finalizado.
- O frontend usa WebSocket nativo, sem dependência extra, e mantém polling leve como fallback enquanto o lote está processando.
- As migrations são versionadas em `backend/src/main/resources/db/migration`.
