# Faz 1 - Foundation Spec

## Overview

Bu fazin amaci runtime business complexity'e girmeden once proje temelini
ve ilk domain omurgasini kurmaktir.

## Initial Domain Scope

- Account
- InternalTransaction
- ExternalTransactionRecord

## Goals

- proje temelini temizlemek
- DB + cache local setup hazirlamak
- package ve modul dusuncesini netlestirmek
- ilk migration ve ilk application config'e zemin hazirlamak

## Out of Scope

- reconciliation rule engine
- settlement state machine
- Kafka
- fraud
- AWS deployment

## Acceptance Criteria

- proje temiz dependency seti ile aciliyor olmali
- local config PostgreSQL ve Redis'e hazir olmali
- Faz 1 alanlari dokumante edilmeli
- mimari kararlar koddan once yazilmis olmali
