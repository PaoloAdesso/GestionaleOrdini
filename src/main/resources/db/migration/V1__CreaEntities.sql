-- =========================
-- Sequence
-- =========================
CREATE SEQUENCE tavoli_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE prodotti_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ordini_id_seq START WITH 1 INCREMENT BY 1;

-- Definisco tipo ENUM per lo stato del tavolo
CREATE TYPE stato_tavolo_enum AS ENUM ('LIBERO', 'OCCUPATO', 'RISERVATO');

-- Definisco tipo ENUM per lo stato dell'ordine
CREATE TYPE stato_ordine_enum AS ENUM ('IN ATTESA', 'IN PREPARAZIONE', 'SERVITO', 'CHIUSO');

-- Definisco tipo ENUM per lo stato di pagamento
CREATE TYPE stato_pagato_enum AS ENUM ('PAGATO', 'NON PAGATO');

-- =========================
-- Tabella Tavoli
-- =========================
CREATE TABLE tavoli (
                         id_tavolo BIGINT PRIMARY KEY DEFAULT nextval('tavoli_id_seq'),
                         numero_nome_tavolo VARCHAR(255) NOT NULL UNIQUE,
                         stato stato_tavolo_enum NOT NULL DEFAULT 'LIBERO'
);
-- =========================
-- Tabella Prodotti
-- =========================
CREATE TABLE prodotti (
                              id_prodotto BIGINT PRIMARY KEY DEFAULT nextval('prodotti_id_seq'),
                              nome_prodotto VARCHAR(255) NOT NULL,
                              categoria_prodotto VARCHAR(255) NOT NULL,
                              prezzo NUMERIC(10,2) NOT NULL
);

-- =========================
-- Tabella Ordini
-- =========================
CREATE TABLE ordini (
                          id_ordine BIGINT PRIMARY KEY DEFAULT nextval('ordini_id_seq'),
                          id_tavolo BIGINT NOT NULL,
                          data_ordine DATE NOT NULL,
                          stato_ordine stato_ordine_enum NOT NULL default 'IN ATTESA',
                          CONSTRAINT fk_tavoli FOREIGN KEY (id_tavolo) REFERENCES tavoli(id_tavolo) ON DELETE CASCADE
);
-- =========================
-- Tabella Ponte Ordini - Prodotti (Many-to-Many)
-- =========================
CREATE TABLE ordini_prodotti (
                                           id_ordine BIGINT NOT NULL,
                                           id_prodotto BIGINT NOT NULL,
                                           quantita_prodotto BIGINT NOT NULL,
                                           stato_pagato stato_pagato_enum NOT NULL DEFAULT 'NON PAGATO',
                                           PRIMARY KEY (id_ordine, id_prodotto),
                                           CONSTRAINT fk_ordini FOREIGN KEY (id_ordine) REFERENCES ordini(id_ordine) ON DELETE CASCADE,
                                           CONSTRAINT fk_prodotti FOREIGN KEY (id_prodotto) REFERENCES prodotti(id_prodotto) ON DELETE CASCADE
);