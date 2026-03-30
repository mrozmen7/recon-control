# recon-control

`recon-control`, bankacilik odakli bir reconciliation ve settlement
control system projesidir.

Bu proje su gercek problemleri cozmeyi hedefler:

- ayni islemin farkli sistemlerde farkli gorunmesi
- eksik veya duplicate transaction kaydi
- settlement gecikmesi veya takilmasi
- mismatch tespit edilmesine ragmen operasyonel aksiyon uretilememesi
- audit ve incident incelemesinde aciklanamayan kararlar

Ilk asamada proje `Modular Monolith + Hexagonal Architecture`
yaklasimiyla kurulacaktir.

Hedef teknoloji omurgasi:

- Java 21
- Spring Boot 3
- PostgreSQL
- Redis
- Flyway
- Spring Security
- Testcontainers

Ilerleyen fazlarda:

- Kafka
- Outbox Pattern
- Settlement state machine
- Case management
- Observability
- AWS deployment

## Faz 1 Hedefi

Faz 1'de amac:

- repo temelini kurmak
- Spring Boot temelini temizlemek
- `Account`, `InternalTransaction`, `ExternalTransactionRecord`
  domainlerini planlamak
- ilk spec ve mimari kararlarini netlestirmek

## Not

Bu proje sadece kod yazma egzersizi degil, ayni zamanda bankacilik
backend dusuncesini ogrenme projesidir.
