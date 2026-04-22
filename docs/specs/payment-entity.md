# Spec: Payment Entity — payment-service

## Kontext

Micronaut projekt `payment-service`, balíček `cz.ajraa.tournament.payment`.
Databáze sdílená se spring-api (PostgreSQL, tabulky `payment_type` a `payment_transaction`).

## Soubory k vytvoření

| Soubor | Typ |
|--------|-----|
| `PaymentStatus.java` | enum |
| `PaymentType.java` | enum |
| `PaymentTypeEntity.java` | JPA entita |
| `PaymentTransaction.java` | JPA entita |

## PaymentStatus (enum)

```
PENDING, COMPLETED, FAILED, REFUNDED
```

## PaymentType (enum)

```
ENTRY_FEE, PRIZE_PAYOUT, PRIZE_DEPOSIT
```

## PaymentTypeEntity

Tabulka: `payment_type`

| Pole | Typ | Constraints |
|------|-----|-------------|
| id | Integer | PK, IDENTITY |
| code | PaymentType (enum) | @Enumerated(STRING), unique, not null |
| description | String | nullable |

## PaymentTransaction

Tabulka: `payment_transaction`

| Pole | Typ | Constraints |
|------|-----|-------------|
| transactionId | Long | PK, IDENTITY |
| userId | Long | not null (cross-service ID) |
| tournamentId | Long | nullable (cross-service ID) |
| amount | BigDecimal | not null, precision=12, scale=2 |
| type | PaymentTypeEntity | @ManyToOne LAZY, FK type_id, not null |
| status | PaymentStatus (enum) | @Enumerated(STRING), not null, length=50 |
| createdAt | LocalDateTime | not null |
| completedAt | LocalDateTime | nullable |

## Vzory

- Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor` (ne `@Data`)
- `jakarta.persistence.*` (stejné jako spring-api)
- Package-private viditelnost tříd (stejně jako spring-api)
- Žádné komentáře, žádná javadoc
