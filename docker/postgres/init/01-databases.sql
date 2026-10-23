-- Un database per servizio: ognuno è proprietario dei suoi dati e nessuno legge le tabelle
-- degli altri (si parlano solo via HTTP o RabbitMQ).
-- Postgres esegue questo file solo la prima volta, quando il volume dei dati è vuoto.
CREATE DATABASE bugboard_users;
CREATE DATABASE bugboard_issues;
CREATE DATABASE bugboard_notifications;
