# ADR-001 - Modular Monolith ile Baslangic

## Karar

Proje ilk asamada microservice degil, modular monolith olarak
baslatilacak.

## Gerekce

- domain sinirlari once kod icinde netlessin
- lokal gelistirme hizli kalsin
- test setup'i sade olsun
- erken network dagitimi gereksiz complexity yaratmasin

## Sonuc

- modul sinirlari package ve port/adapter seviyesinde korunacak
- ileride ihtiyac olursa secili moduller ayrilabilecek
