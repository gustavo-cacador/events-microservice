INSERT INTO categories (name) VALUES ('Tecnologia');
INSERT INTO categories (name) VALUES ('Saúde');

INSERT INTO events (max_participants, registered_participants, date, title, description) VALUES (20, 0, '2025-11-02', 'TESTE', 'Evento para Teste');
INSERT INTO events (max_participants, registered_participants, date, title, description) VALUES (20, 0, '2025-11-03', 'TESTE2', 'Evento para Teste2');

INSERT INTO events_categories(events_id, categoria_id) VALUES (1, 1);
INSERT INTO events_categories(events_id, categoria_id) VALUES (2, 2);
