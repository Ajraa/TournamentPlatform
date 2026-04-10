-- ==========================================
-- ČÍSELNÍKY
-- ==========================================

CREATE TABLE user_role (
    id          SERIAL       PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE team_type (
    id          SERIAL       PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE payment_type (
    id          SERIAL       PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- ==========================================
-- HLAVNÍ ENTITY
-- ==========================================

CREATE TABLE users (
    user_id       BIGSERIAL    PRIMARY KEY,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    nickname      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rating        INTEGER      NOT NULL DEFAULT 0,
    winrate       REAL         NOT NULL DEFAULT 0.0,
    street        VARCHAR(100),
    city          VARCHAR(100),
    postcode      VARCHAR(20),
    country       VARCHAR(100),
    house_number  VARCHAR(20),
    bank_number   VARCHAR(50)
);

CREATE TABLE team (
    team_id    BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    tag        VARCHAR(10)  NOT NULL UNIQUE,
    rating     INTEGER      NOT NULL DEFAULT 0,
    type_id    INTEGER      NOT NULL REFERENCES team_type(id),
    captain_id BIGINT       NOT NULL REFERENCES users(user_id)
);

CREATE TABLE tournament (
    tournament_id       BIGSERIAL      PRIMARY KEY,
    name                VARCHAR(255)   NOT NULL,
    start_time          TIMESTAMP      NOT NULL,
    prize               DECIMAL(12, 2),
    price               DECIMAL(12, 2),
    minimal_rating      INTEGER,
    maximal_rating      INTEGER,
    players_per_team    INTEGER        NOT NULL,
    minimal_team_amount INTEGER        NOT NULL,
    maximal_team_amount INTEGER        NOT NULL,
    founder_id          BIGINT         NOT NULL REFERENCES users(user_id),
    winner_team_id      BIGINT         REFERENCES team(team_id)
);

CREATE TABLE match (
    match_id       BIGSERIAL   PRIMARY KEY,
    match_state    VARCHAR(50) NOT NULL,
    tournament_id  BIGINT      NOT NULL REFERENCES tournament(tournament_id),
    winner_team_id BIGINT      REFERENCES team(team_id)
);

CREATE TABLE payment_transaction (
    transaction_id BIGSERIAL      PRIMARY KEY,
    user_id        BIGINT         NOT NULL REFERENCES users(user_id),
    tournament_id  BIGINT         REFERENCES tournament(tournament_id),
    amount         DECIMAL(12, 2) NOT NULL,
    type_id        INTEGER        NOT NULL REFERENCES payment_type(id),
    status         VARCHAR(50)    NOT NULL,
    created_at     TIMESTAMP      NOT NULL DEFAULT NOW(),
    completed_at   TIMESTAMP
);

-- ==========================================
-- ČÍSELNÍKY — data
-- ==========================================

INSERT INTO user_role (code, description) VALUES
    ('PLAYER',    'Hráč — základní role pro účast v turnajích'),
    ('FOUNDER',   'Organizátor — může zakládat a spravovat turnaje'),
    ('ADMIN',     'Administrátor — plný přístup do systému');

INSERT INTO team_type (code, description) VALUES
    ('TEAM', 'Skupinový tým'),
    ('SOLO', 'Individuální tým');

INSERT INTO payment_type (code, description) VALUES
    ('ENTRY_FEE',     'Registrační poplatek za turnaj'),
    ('PRIZE_PAYOUT',  'Výplata výhry z turnaje'),
    ('PRIZE_DEPOSIT', 'Poplatek za založení turnaje sloužící k vyplacení výhry');

-- ==========================================
-- VAZEBNÍ TABULKY
-- ==========================================

CREATE TABLE user_has_role (
    user_id BIGINT  NOT NULL REFERENCES users(user_id),
    role_id INTEGER NOT NULL REFERENCES user_role(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE user_team (
    user_id   BIGINT    NOT NULL REFERENCES users(user_id),
    team_id   BIGINT    NOT NULL REFERENCES team(team_id),
    joined_at TIMESTAMP NOT NULL DEFAULT NOW(),
    left_at   TIMESTAMP,
    PRIMARY KEY (user_id, team_id)
);

CREATE TABLE team_tournament (
    team_id       BIGINT    NOT NULL REFERENCES team(team_id),
    tournament_id BIGINT    NOT NULL REFERENCES tournament(tournament_id),
    joined_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (team_id, tournament_id)
);

CREATE TABLE team_match (
    team_id  BIGINT NOT NULL REFERENCES team(team_id),
    match_id BIGINT NOT NULL REFERENCES match(match_id),
    PRIMARY KEY (team_id, match_id)
);
