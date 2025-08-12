-- Setup users and database.
-- PostgresML does not use environment variables like the standard Postgres image does.
CREATE USER adaptionuser WITH PASSWORD 'insecurepassword';
CREATE DATABASE cats OWNER adaptionuser;