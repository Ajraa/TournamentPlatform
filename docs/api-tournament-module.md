# API Documentation — Tournament Module

Base path: `/api/v1`

> **Poznámka:** Tento dokument je návrh. Endpointy nejsou dosud implementovány — návrh vychází z DB schématu.

---

## Tournaments

### POST /api/v1/tournaments

Vytvoří nový turnaj. Zakladatelem se automaticky stává přihlášený uživatel.

- **Auth:** JWT cookie required (`jwt`), pouze role `FOUNDER`
- **Status:** `201 Created` | `400 Bad Request` | `401 Unauthorized` | `403 Forbidden`

**Request body**

```json
{
  "name": "Spring Cup 2026",
  "startTime": "2026-06-01T12:00:00Z",
  "prize": 10000.00,
  "price": 50.00,
  "minimalRating": 0,
  "maximalRating": 2000,
  "playersPerTeam": 5,
  "minimalTeamAmount": 4,
  "maximalTeamAmount": 16
}
```

#### Field reference

| Field               | Type      | Constraints                                                  |
|---------------------|-----------|--------------------------------------------------------------|
| `name`              | string    | Required                                                     |
| `startTime`         | ISO 8601  | Required, musí být v budoucnosti                             |
| `prize`             | decimal   | Optional, výše výhry                                         |
| `price`             | decimal   | Optional, vstupní poplatek; pokud uveden, `prize` je povinný |
| `minimalRating`     | integer   | Optional, minimální rating týmu                              |
| `maximalRating`     | integer   | Optional, maximální rating týmu; musí být > `minimalRating`  |
| `playersPerTeam`    | integer   | Required, min 1                                              |
| `minimalTeamAmount` | integer   | Required, min 2                                              |
| `maximalTeamAmount` | integer   | Required, musí být >= `minimalTeamAmount`                    |

**Response body** (`201`) — `TournamentDto`

```json
{
  "tournamentId": 1,
  "name": "Spring Cup 2026",
  "startTime": "2026-06-01T12:00:00Z",
  "prize": 10000.00,
  "price": 50.00,
  "minimalRating": 0,
  "maximalRating": 2000,
  "playersPerTeam": 5,
  "minimalTeamAmount": 4,
  "maximalTeamAmount": 16,
  "founderId": 3,
  "winnerTeamId": null,
  "registeredTeamsCount": 0,
  "state": "OPEN"
}
```

**Error responses**

| Status | Condition                                          |
|--------|----------------------------------------------------|
| `400`  | Validation failed                                  |
| `403`  | Caller does not have FOUNDER role                  |

---

### GET /api/v1/tournaments

Vrátí stránkovaný seznam turnajů. Podporuje filtrování.

- **Auth:** Public
- **Status:** `200 OK`

**Query parameters**

| Parameter  | Type    | Default | Description                                      |
|------------|---------|---------|--------------------------------------------------|
| `state`    | string  | —       | Filtr dle stavu: `OPEN`, `IN_PROGRESS`, `FINISHED` |
| `page`     | integer | `0`     | Číslo stránky                                    |
| `size`     | integer | `20`    | Počet položek na stránku (max 100)               |

**Response body** (`200`) — stránkovaný seznam `TournamentDto`

```json
{
  "content": [
    {
      "tournamentId": 1,
      "name": "Spring Cup 2026",
      "startTime": "2026-06-01T12:00:00Z",
      "prize": 10000.00,
      "price": 50.00,
      "minimalRating": 0,
      "maximalRating": 2000,
      "playersPerTeam": 5,
      "minimalTeamAmount": 4,
      "maximalTeamAmount": 16,
      "founderId": 3,
      "winnerTeamId": null,
      "registeredTeamsCount": 7,
      "state": "OPEN"
    }
  ],
  "totalElements": 42,
  "totalPages": 3,
  "page": 0,
  "size": 20
}
```

---

### GET /api/v1/tournaments/{tournamentId}

Vrátí detail turnaje.

- **Auth:** Public
- **Status:** `200 OK` | `404 Not Found`

**Path parameters**

| Parameter      | Type    | Description   |
|----------------|---------|---------------|
| `tournamentId` | integer | ID turnaje    |

**Response body** (`200`) — `TournamentDto`

**Error responses**

| Status | Condition              |
|--------|------------------------|
| `404`  | Tournament not found   |

---

### PUT /api/v1/tournaments/{tournamentId}

Aktualizuje parametry turnaje. Povoleno pouze před zahájením (`state = OPEN`).

- **Auth:** JWT cookie required (`jwt`), pouze zakladatel turnaje
- **Status:** `200 OK` | `400 Bad Request` | `401 Unauthorized` | `403 Forbidden` | `404 Not Found`

**Path parameters**

| Parameter      | Type    | Description   |
|----------------|---------|---------------|
| `tournamentId` | integer | ID turnaje    |

**Request body** — stejná pole jako POST, všechna optional

**Response body** (`200`) — aktualizovaný `TournamentDto`

**Error responses**

| Status | Condition                                          |
|--------|----------------------------------------------------|
| `400`  | Validation failed, nebo turnaj již nezahájen      |
| `403`  | Caller is not the tournament founder               |
| `404`  | Tournament not found                               |

---

### DELETE /api/v1/tournaments/{tournamentId}

Smaže turnaj. Povoleno pouze před zahájením (`state = OPEN`) bez přihlášených týmů.

- **Auth:** JWT cookie required (`jwt`), zakladatel nebo `ADMIN`
- **Status:** `204 No Content` | `400 Bad Request` | `401 Unauthorized` | `403 Forbidden` | `404 Not Found`

**Path parameters**

| Parameter      | Type    | Description   |
|----------------|---------|---------------|
| `tournamentId` | integer | ID turnaje    |

**Error responses**

| Status | Condition                                            |
|--------|------------------------------------------------------|
| `400`  | Tournament already has registered teams, or started  |
| `403`  | Caller is not the founder or ADMIN                   |
| `404`  | Tournament not found                                 |

---

## Registrace týmů do turnaje

### GET /api/v1/tournaments/{tournamentId}/teams

Vrátí seznam týmů registrovaných do turnaje.

- **Auth:** Public
- **Status:** `200 OK` | `404 Not Found`

**Path parameters**

| Parameter      | Type    | Description   |
|----------------|---------|---------------|
| `tournamentId` | integer | ID turnaje    |

**Response body** (`200`) — list `TournamentTeamDto`

```json
[
  {
    "teamId": 1,
    "name": "Team Avarice",
    "tag": "AVR",
    "rating": 1200,
    "joinedAt": "2026-04-20T08:00:00Z"
  }
]
```

---

### POST /api/v1/tournaments/{tournamentId}/teams

Přihlásí tým do turnaje. Pokud je nastaven `price`, vytvoří se platební transakce `ENTRY_FEE`.

- **Auth:** JWT cookie required (`jwt`), pouze kapitán daného týmu
- **Status:** `201 Created` | `400 Bad Request` | `401 Unauthorized` | `403 Forbidden` | `404 Not Found` | `409 Conflict`

**Path parameters**

| Parameter      | Type    | Description   |
|----------------|---------|---------------|
| `tournamentId` | integer | ID turnaje    |

**Request body**

```json
{
  "teamId": 1
}
```

**Error responses**

| Status | Condition                                                           |
|--------|---------------------------------------------------------------------|
| `400`  | Tournament is full, not open, or team rating out of range           |
| `403`  | Caller is not the team captain                                      |
| `404`  | Tournament or team not found                                        |
| `409`  | Team is already registered                                          |

---

### DELETE /api/v1/tournaments/{tournamentId}/teams/{teamId}

Odhlásí tým z turnaje. Povoleno pouze před zahájením (`state = OPEN`).

- **Auth:** JWT cookie required (`jwt`), kapitán týmu nebo zakladatel turnaje
- **Status:** `204 No Content` | `401 Unauthorized` | `403 Forbidden` | `404 Not Found`

**Path parameters**

| Parameter      | Type    | Description   |
|----------------|---------|---------------|
| `tournamentId` | integer | ID turnaje    |
| `teamId`       | integer | ID týmu       |

**Error responses**

| Status | Condition                                              |
|--------|--------------------------------------------------------|
| `403`  | Caller is not the team captain or tournament founder   |
| `404`  | Tournament or registration not found                   |

---

## Zápasy

### GET /api/v1/tournaments/{tournamentId}/matches

Vrátí seznam zápasů v turnaji.

- **Auth:** Public
- **Status:** `200 OK` | `404 Not Found`

**Path parameters**

| Parameter      | Type    | Description   |
|----------------|---------|---------------|
| `tournamentId` | integer | ID turnaje    |

**Response body** (`200`) — list `MatchDto`

```json
[
  {
    "matchId": 10,
    "state": "FINISHED",
    "teams": [
      { "teamId": 1, "name": "Team Avarice", "tag": "AVR" },
      { "teamId": 2, "name": "FragStars", "tag": "FST" }
    ],
    "winnerTeamId": 1
  }
]
```

#### Stavy zápasu

| Stav          | Popis                           |
|---------------|---------------------------------|
| `PENDING`     | Zápas naplánován, čeká na start |
| `IN_PROGRESS` | Zápas právě probíhá             |
| `FINISHED`    | Zápas ukončen, výsledek zaznamenán |

---

### GET /api/v1/tournaments/{tournamentId}/matches/{matchId}

Vrátí detail zápasu.

- **Auth:** Public
- **Status:** `200 OK` | `404 Not Found`

**Path parameters**

| Parameter      | Type    | Description   |
|----------------|---------|---------------|
| `tournamentId` | integer | ID turnaje    |
| `matchId`      | integer | ID zápasu     |

**Response body** (`200`) — `MatchDto`

---

### PUT /api/v1/tournaments/{tournamentId}/matches/{matchId}

Zaznamená výsledek zápasu nebo aktualizuje stav.

- **Auth:** JWT cookie required (`jwt`), zakladatel turnaje nebo `ADMIN`
- **Status:** `200 OK` | `400 Bad Request` | `401 Unauthorized` | `403 Forbidden` | `404 Not Found`

**Path parameters**

| Parameter      | Type    | Description   |
|----------------|---------|---------------|
| `tournamentId` | integer | ID turnaje    |
| `matchId`      | integer | ID zápasu     |

**Request body**

```json
{
  "state": "FINISHED",
  "winnerTeamId": 1
}
```

#### Field reference

| Field          | Type    | Constraints                                                       |
|----------------|---------|-------------------------------------------------------------------|
| `state`        | string  | Required, viz stavy zápasu výše                                   |
| `winnerTeamId` | integer | Required pokud `state = FINISHED`; musí být jedním z týmů zápasu |

**Response body** (`200`) — aktualizovaný `MatchDto`

**Error responses**

| Status | Condition                                                      |
|--------|----------------------------------------------------------------|
| `400`  | Invalid state transition, or `winnerTeamId` not in this match  |
| `403`  | Caller is not the tournament founder or ADMIN                  |
| `404`  | Tournament or match not found                                  |

---

## DTOs

| DTO                    | Used in                                           | Fields                                                                                                                  |
|------------------------|---------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| `CreateTournamentDto`  | `POST /tournaments` request                       | `name`, `startTime`, `prize`, `price`, `minimalRating`, `maximalRating`, `playersPerTeam`, `minimalTeamAmount`, `maximalTeamAmount` |
| `UpdateTournamentDto`  | `PUT /tournaments/{id}` request                   | stejná pole jako `CreateTournamentDto`, vše optional                                                                    |
| `TournamentDto`        | `POST/GET/PUT /tournaments` response              | `tournamentId`, `name`, `startTime`, `prize`, `price`, `minimalRating`, `maximalRating`, `playersPerTeam`, `minimalTeamAmount`, `maximalTeamAmount`, `founderId`, `winnerTeamId`, `registeredTeamsCount`, `state` |
| `TournamentTeamDto`    | `GET /tournaments/{id}/teams` response            | `teamId`, `name`, `tag`, `rating`, `joinedAt`                                                                           |
| `RegisterTeamDto`      | `POST /tournaments/{id}/teams` request            | `teamId`                                                                                                                |
| `MatchDto`             | `GET/PUT /tournaments/{id}/matches` response      | `matchId`, `state`, `teams`, `winnerTeamId`                                                                             |
| `UpdateMatchDto`       | `PUT /tournaments/{id}/matches/{matchId}` request | `state`, `winnerTeamId`                                                                                                 |

---

## Poznámky k implementaci

- Stav turnaje (`state`) není uložen v DB — odvozuje se ze `start_time` a existence výsledků.
  - `OPEN` — před startem, bez vítěze
  - `IN_PROGRESS` — po startu, bez vítěze
  - `FINISHED` — `winner_team_id IS NOT NULL`
- Při přihlášení týmu s `price > 0` se vytváří `payment_transaction` s typem `ENTRY_FEE` a stavem `PENDING`.
- Zápasy se generují při startu turnaje — buď manuálně (zakladatel), nebo automaticky dle systému pavouka.
- Generování zápasů při startu turnaje není pokryto tímto návrhem — vyžaduje samostatné rozhodnutí o formátu turnaje (single elimination, round robin, …).
