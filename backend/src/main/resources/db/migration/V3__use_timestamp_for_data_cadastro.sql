ALTER TABLE leads
    ALTER COLUMN data_cadastro TYPE TIMESTAMP
    USING data_cadastro::timestamp;

ALTER TABLE lote_itens
    ALTER COLUMN data_cadastro TYPE TIMESTAMP
    USING data_cadastro::timestamp;
