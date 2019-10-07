CREATE DATABASE notepad;

CREATE SCHEMA project;

CREATE TABLE project.user(email TEXT PRIMARY KEY, name TEXT NOT NULL, surname TEXT NOT NULL, password TEXT NOT NULL, "check" BOOLEAN NOT NULL);

CREATE TABLE project.note(id TEXT PRIMARY KEY, user_email TEXT, subject TEXT, text TEXT, priority SMALLINT, reminder TIMESTAMP DEFAULT NULL, coordinate TEXT[] DEFAULT NULL, image BYTEA);

CREATE TABLE project.log(date TIMESTAMP, ip TEXT, subject TEXT, message TEXT);