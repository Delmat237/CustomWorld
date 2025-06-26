INSERT INTO categories (name) VALUES ('Souris'), ('Vêtements'), ('Sacs');

-- Initialisation des utilisateurs (mot de passe : "password" haché avec BCrypt)
INSERT INTO users (email, password, role, name, address) VALUES
                                                             ('client@example.com', '$2a$10$1Jq0z7X9Z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6', 'CUSTOMER', 'Client Test', '123 Rue Exemple'),
                                                             ('vendor@example.com', '$2a$10$1Jq0z7X9Z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6', 'VENDOR', 'Vendeur Test', '456 Rue Exemple'),
                                                             ('deliverer@example.com', '$2a$10$1Jq0z7X9Z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6', 'DELIVERER', 'Livreur Test', '789 Rue Exemple'),
                                                             ('admin@example.com', '$2a$10$1Jq0z7X9Z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6z9z6', 'ADMIN', 'Admin Test', '101 Rue Exemple');