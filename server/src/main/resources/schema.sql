PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
    username            TEXT NOT NULL PRIMARY KEY,
    nickname            TEXT NOT NULL,
    email               TEXT NOT NULL UNIQUE,
    gender              TEXT NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    securityAnswers     TEXT NOT NULL, --json array of answers
    maximumMoneyEarned  INTEGER NOT NULL DEFAULT 0,
    password            TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS audio_files (
    username        TEXT NOT NULL,
    file_name       TEXT NOT NULL,
    audio_data      BLOB,
    PRIMARY KEY(username, file_name)
    FOREIGN KEY username REFERENCES users(username) ON DELETE CASCADE
);
