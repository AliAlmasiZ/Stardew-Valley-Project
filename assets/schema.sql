CREATE TABLE IF NOT EXISTS users (
    username            TEXT NOT NULL PRIMARY KEY,
    nickname            TEXT NOT NULL,
    email               TEXT NOT NULL UNIQUE,
    gender              TEXT NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    securityAnswers     TEXT NOT NULL, --json array of answers
    maximumMoneyEarned  INTEGER NOT NULL DEFAULT 0,
    password            TEXT NOT NULL
);

