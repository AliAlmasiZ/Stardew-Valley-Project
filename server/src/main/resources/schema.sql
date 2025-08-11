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

CREATE TABLE IF NOT EXISTS games (
    game_id INTEGER PRIMARY KEY AUTOINCREMENT,
    save_path TEXT NOT NULL,
    game_date TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS user_game (
    game_id     INTEGER NOT NULL,
    username    TEXT NOT NULL,
    farm TEXT NOT NULL,
    gold INTEGER NOT NULL,
    PRIMARY KEY (game_id, username),
    FOREIGN KEY (game_id) REFERENCES games(game_id) ON DELETE CASCADE,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audio_files (
    username        TEXT NOT NULL,
    file_name       TEXT NOT NULL,
    audio_data      BLOB,
    PRIMARY KEY(username, file_name)
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);
