ALTER TABLE lotes
    ADD COLUMN IF NOT EXISTS caminho_arquivo VARCHAR(500);

UPDATE lotes
SET caminho_arquivo = nome_arquivo
WHERE caminho_arquivo IS NULL;

ALTER TABLE lotes
    ALTER COLUMN caminho_arquivo SET NOT NULL;
