# Phase 1 Architecture Overview

Faz 1 runtime akisinin hedefi:

`HTTP/API -> Application Use Case -> Domain -> Persistence`

Bu fazda henuz Kafka veya scheduler yok.

Ilk odak:

- request alma
- validation
- domain nesnesi olusturma
- persistence'a hazirlik

Yuksek seviye akis:

```text
Client Request
-> Controller
-> Application Service / Use Case
-> Domain Model
-> Repository Port
-> Persistence Adapter
-> PostgreSQL
```

Redis bu fazda daha cok altyapi hazirligi olarak bulunur; aktif business
kritik rol sonraki fazlarda gelecektir.
