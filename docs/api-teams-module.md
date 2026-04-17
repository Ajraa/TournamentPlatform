# API Documentation — Teams Module

Base path: `/api/v1`

> **Poznámka:** Tento dokument je návrh. Endpointy nejsou dosud implementovány — návrh vychází z DB schématu.

---

## Teams

### POST /api/v1/teams

Vytvoří nový tým. Zakladatel se automaticky stává kapitánem a prvním členem.

- **Auth:** JWT cookie required (`jwt`), pouze role `PLAYER`
- **Status:** `201 Created` | `400 Bad Request` | `401 Unauthorized` | `409 Conflict`

**Request body**

```json
{
  "name": "Team Avarice",
  "tag": "AVR"
}
```

#### Field reference

| Field  | Type   | Constraints                    |
|--------|--------|--------------------------------|
| `name` | string | Required, unique               |
| `tag`  | string | Required, unique, max 10 znaků |

**Response body** (`201`) — `TeamDto`

```json
{
  "teamId": 1,
  "name": "Team Avarice",
  "tag": "AVR",
  "rating": 0,
  "type": "TEAM",
  "captainId": 5,
  "members": [
    { "userId": 5, "nickname": "ProGamer42", "joinedAt": "2026-04-17T10:00:00Z" }
  ]
}
```

**Error responses**

| Status | Condition                          |
|--------|------------------------------------|
| `400`  | Validation failed                  |
| `409`  | Team name or tag already in use    |

---

### GET /api/v1/teams/{teamId}

Vrátí detail týmu včetně seznamu aktivních členů.

- **Auth:** Public
- **Status:** `200 OK` | `404 Not Found`

**Path parameters**

| Parameter | Type    | Description  |
|-----------|---------|--------------|
| `teamId`  | integer | ID týmu      |

**Response body** (`200`) — `TeamDto`

```json
{
  "teamId": 1,
  "name": "Team Avarice",
  "tag": "AVR",
  "rating": 1200,
  "type": "TEAM",
  "captainId": 5,
  "members": [
    { "userId": 5, "nickname": "ProGamer42", "joinedAt": "2026-04-17T10:00:00Z" },
    { "userId": 8, "nickname": "FragMaster", "joinedAt": "2026-04-18T09:30:00Z" }
  ]
}
```

**Error responses**

| Status | Condition        |
|--------|------------------|
| `404`  | Team not found   |

---

### PUT /api/v1/teams/{teamId}

Aktualizuje název nebo tag týmu.

- **Auth:** JWT cookie required (`jwt`), pouze kapitán týmu
- **Status:** `200 OK` | `400 Bad Request` | `401 Unauthorized` | `403 Forbidden` | `404 Not Found` | `409 Conflict`

**Path parameters**

| Parameter | Type    | Description  |
|-----------|---------|--------------|
| `teamId`  | integer | ID týmu      |

**Request body**

```json
{
  "name": "Team Avarice Reborn",
  "tag": "AVRR"
}
```

**Response body** (`200`) — aktualizovaný `TeamDto`.

**Error responses**

| Status | Condition                       |
|--------|---------------------------------|
| `403`  | Caller is not the team captain  |
| `404`  | Team not found                  |
| `409`  | Name or tag already in use      |

---

### DELETE /api/v1/teams/{teamId}

Smaže tým. SOLO týmy (auto-vytvořené pro jednotlivce) nelze mazat.

- **Auth:** JWT cookie required (`jwt`), pouze kapitán týmu
- **Status:** `204 No Content` | `401 Unauthorized` | `403 Forbidden` | `404 Not Found`

**Path parameters**

| Parameter | Type    | Description  |
|-----------|---------|--------------|
| `teamId`  | integer | ID týmu      |

**Error responses**

| Status | Condition                                      |
|--------|------------------------------------------------|
| `403`  | Caller is not the captain, or team type is SOLO |
| `404`  | Team not found                                 |

---

## Members

### GET /api/v1/teams/{teamId}/members

Vrátí seznam aktivních členů týmu.

- **Auth:** Public
- **Status:** `200 OK` | `404 Not Found`

**Path parameters**

| Parameter | Type    | Description  |
|-----------|---------|--------------|
| `teamId`  | integer | ID týmu      |

**Response body** (`200`) — list `MemberDto`

```json
[
  { "userId": 5, "nickname": "ProGamer42", "joinedAt": "2026-04-17T10:00:00Z" },
  { "userId": 8, "nickname": "FragMaster", "joinedAt": "2026-04-18T09:30:00Z" }
]
```

---

### POST /api/v1/teams/{teamId}/members

Přidá hráče do týmu.

- **Auth:** JWT cookie required (`jwt`), pouze kapitán týmu
- **Status:** `200 OK` | `400 Bad Request` | `401 Unauthorized` | `403 Forbidden` | `404 Not Found` | `409 Conflict`

**Path parameters**

| Parameter | Type    | Description  |
|-----------|---------|--------------|
| `teamId`  | integer | ID týmu      |

**Request body**

```json
{
  "userId": 8
}
```

**Error responses**

| Status | Condition                             |
|--------|---------------------------------------|
| `403`  | Caller is not the team captain        |
| `404`  | Team or user not found                |
| `409`  | User is already an active team member |

---

### DELETE /api/v1/teams/{teamId}/members/{userId}

Odebere hráče z týmu. Soft delete — nastaví `left_at`. Povoleno kapitánovi nebo hráči samotnému (opuštění týmu).

- **Auth:** JWT cookie required (`jwt`), kapitán nebo daný hráč
- **Status:** `204 No Content` | `401 Unauthorized` | `403 Forbidden` | `404 Not Found`

**Path parameters**

| Parameter | Type    | Description         |
|-----------|---------|---------------------|
| `teamId`  | integer | ID týmu             |
| `userId`  | integer | ID odebíraného hráče |

**Error responses**

| Status | Condition                                      |
|--------|------------------------------------------------|
| `403`  | Caller is neither the captain nor the user     |
| `404`  | Team or membership not found                   |

---

### PUT /api/v1/teams/{teamId}/captain

Předá kapitánství jinému členovi týmu.

- **Auth:** JWT cookie required (`jwt`), pouze stávající kapitán
- **Status:** `200 OK` | `400 Bad Request` | `401 Unauthorized` | `403 Forbidden` | `404 Not Found`

**Path parameters**

| Parameter | Type    | Description  |
|-----------|---------|--------------|
| `teamId`  | integer | ID týmu      |

**Request body**

```json
{
  "userId": 8
}
```

**Error responses**

| Status | Condition                                   |
|--------|---------------------------------------------|
| `403`  | Caller is not the current captain           |
| `404`  | Team not found, or user is not a team member |

**Response body** (`200`) — aktualizovaný `TeamDto` s novým `captainId`.

---

## DTOs

| DTO                | Used in                                    | Fields                                                              |
|--------------------|--------------------------------------------|---------------------------------------------------------------------|
| `CreateTeamDto`    | `POST /teams` request                      | `name`, `tag`                                                       |
| `UpdateTeamDto`    | `PUT /teams/{teamId}` request              | `name`, `tag`                                                       |
| `TeamDto`          | `POST /teams`, `GET/PUT /teams/{teamId}` response | `teamId`, `name`, `tag`, `rating`, `type`, `captainId`, `members`  |
| `MemberDto`        | `GET /teams/{teamId}/members` response, embedded in `TeamDto` | `userId`, `nickname`, `joinedAt`               |
| `AddMemberDto`     | `POST /teams/{teamId}/members` request     | `userId`                                                            |
| `ChangeCaptainDto` | `PUT /teams/{teamId}/captain` request      | `userId`                                                            |
