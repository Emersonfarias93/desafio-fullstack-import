CREATE TABLE lotes (
   id UUID PRIMARY KEY,
   nome_arquivo VARCHAR(255) NOT NULL,
   status VARCHAR(50) NOT NULL,
   total_linhas INTEGER NOT NULL DEFAULT 0,
   total_validas INTEGER NOT NULL DEFAULT 0,
   total_invalidas INTEGER NOT NULL DEFAULT 0,
   total_novas INTEGER NOT NULL DEFAULT 0,
   total_duplicadas INTEGER NOT NULL DEFAULT 0,
   total_possiveis_duplicadas INTEGER NOT NULL DEFAULT 0,
   total_importadas INTEGER NOT NULL DEFAULT 0,
   total_ignoradas INTEGER NOT NULL DEFAULT 0,
   total_erros INTEGER NOT NULL DEFAULT 0,
   mensagem_erro TEXT,
   criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   iniciado_em TIMESTAMP,
   finalizado_em TIMESTAMP
);

CREATE TABLE leads (
   id UUID PRIMARY KEY,
   nome VARCHAR(255) NOT NULL,
   email VARCHAR(255) NOT NULL,
   telefone VARCHAR(30),
   origem VARCHAR(100),
   data_cadastro DATE,
   criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   atualizado_em TIMESTAMP
);

CREATE TABLE lote_itens (
    id UUID PRIMARY KEY,
    lote_id UUID NOT NULL,
    linha_csv INTEGER NOT NULL,
    nome VARCHAR(255),
    email VARCHAR(255),
    telefone VARCHAR(30),
    origem VARCHAR(100),
    data_cadastro DATE,
    status VARCHAR(50) NOT NULL,
    motivo TEXT,
    lead_existente_id UUID,
    decisao_usuario VARCHAR(50),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    CONSTRAINT fk_lote_itens_lote
        FOREIGN KEY (lote_id)
            REFERENCES lotes (id),

    CONSTRAINT fk_lote_itens_lead_existente
        FOREIGN KEY (lead_existente_id)
            REFERENCES leads (id)
);

CREATE TABLE lote_processamentos (
     id UUID PRIMARY KEY,
     lote_id UUID NOT NULL,
     numero_chunk INTEGER NOT NULL,
     status VARCHAR(50) NOT NULL,
     total_linhas INTEGER NOT NULL DEFAULT 0,
     total_sucesso INTEGER NOT NULL DEFAULT 0,
     total_erros INTEGER NOT NULL DEFAULT 0,
     tempo_ms BIGINT NOT NULL DEFAULT 0,
     mensagem_erro TEXT,
     iniciado_em TIMESTAMP,
     finalizado_em TIMESTAMP,
     CONSTRAINT fk_lote_processamentos_lote
         FOREIGN KEY (lote_id)
             REFERENCES lotes (id)
);

CREATE UNIQUE INDEX uk_leads_email
ON leads (email);

CREATE INDEX idx_leads_nome
ON leads (nome);

CREATE INDEX idx_leads_origem
ON leads (origem);

CREATE INDEX idx_lotes_status
ON lotes (status);

CREATE INDEX idx_lote_itens_lote_id
ON lote_itens (lote_id);

CREATE INDEX idx_lote_itens_status
ON lote_itens (status);

CREATE INDEX idx_lote_itens_email
ON lote_itens (email);

CREATE INDEX idx_lote_itens_nome
ON lote_itens (nome);

CREATE INDEX idx_lote_processamentos_lote_id
ON lote_processamentos (lote_id);