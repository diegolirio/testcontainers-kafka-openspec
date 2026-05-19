# testcontainers-kafka

Projeto Spring Boot com Kotlin demonstrando integração entre **Kafka** e **PostgreSQL** usando **Testcontainers** para testes de integração realistas, sem mocks.

## Stack

- Kotlin + Spring Boot 4.x
- Spring Kafka (`spring-boot-starter-kafka`)
- Spring Web MVC
- PostgreSQL (runtime)
- Testcontainers (Kafka nativo + PostgreSQL)
- JUnit 5 + `@ServiceConnection`
- [Awailibity](https://github.com/awaitility/awaitility/wiki/Usage#usage-examples)

## Estrutura de Testes

Os containers são declarados em `TestcontainersConfiguration` e injetados automaticamente via `@ServiceConnection` — sem necessidade de configurar URLs ou portas manualmente.

```kotlin
@Bean
@ServiceConnection
fun kafkaContainer(): KafkaContainer =
    KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"))

@Bean
@ServiceConnection
fun postgresContainer(): PostgreSQLContainer =
    PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
```

## Rodando os Testes

```bash
./gradlew test
```

> Requer Docker em execução.

---

## Visão Geral Mental

Como pensar sobre os dois fluxos de desenvolvimento guiado por IA:

| Conceito      | SpecKit              | OpenSpec          |
|---------------|----------------------|-------------------|
| Ideia inicial | `specify`            | `propose`         |
| Exploração    | IA conversa          | `explore`         |
| Aprovação     | implícita            | mudança formal    |
| Execução      | agente sai codando   | `apply`           |
| Finalização   | acabou               | `archive`         |

---

## Fluxo REAL de uma Feature

> Exemplo: **"Adicionar autenticação JWT no sistema"**

### 1. PROPOSE — Criar a feature

**OpenSpec** — você abre uma mudança formal:

```
/opsx:propose add-jwt-authentication
```

Ou via markdown:

```markdown
# Change Proposal

## Title
Add JWT Authentication

## Problem
System currently has no authentication.

## Solution
Implement JWT-based authentication using Spring Security.

## Scope
- Login endpoint
- JWT filter
- User auth middleware
- Token expiration
```

**SpecKit** — você simplesmente conversa:

```
specify

"Create JWT authentication for Spring Boot"
```

A IA infere requisitos, tasks e estrutura automaticamente.

> **Diferença:** SpecKit é mais automático e menos rastreável. OpenSpec exige que você declare explicitamente a mudança — isso muda muito em projetos grandes.

---

### 2. EXPLORE — Refinar antes de codar

**OpenSpec** — você explora edge cases, impactos e riscos antes de escrever uma linha:

```
/opsx:explore add-jwt-authentication
```

A IA levanta perguntas como:

```
- JWT stateless?
- Refresh token?
- Expiration policy?
- OAuth future compatibility?
- RBAC needed?
```

**SpecKit** — tende a ir de requirement → tasks → implementation muito rápido.

Ótimo para MVP e prototipação. Em sistemas enterprise, decisões ficam implícitas e a dívida técnica cresce rápido.

> **OpenSpec força maturidade arquitetural:** ele basicamente pergunta *"Você TEM CERTEZA do que está construindo?"* — reduzindo retrabalho e inconsistência.

---

### 3. APPLY — Implementar

**OpenSpec** — depois de propor e explorar:

```
/opsx:apply add-jwt-authentication
```

O agente cria código, altera arquivos e implementa as tasks seguindo a spec aprovada.

**SpecKit** — vai implementando conforme a conversa evolui, menos preso à spec formal.

> **Diferença filosófica:**
> - SpecKit → *"Vamos descobrir construindo"*
> - OpenSpec → *"Vamos entender antes de construir"*

---

### 4. ARCHIVE — Finalizar a mudança

Depois que a feature foi implementada, validada e mergeada:

```
/opsx:archive add-jwt-authentication
```

Isso consolida o histórico, fecha a change e mantém rastreabilidade — algo próximo de um RFC, ADR ou Change Management, mas integrado com IA.

**SpecKit** não possui ciclo formal de lifecycle.

---

## Quando Usar Cada Um

| Contexto                         | Ferramenta recomendada |
|----------------------------------|------------------------|
| Velocidade, protótipo, hackathon | SpecKit                |
| Descoberta, vibe coding          | SpecKit                |
| Sistemas enterprise              | OpenSpec               |
| Times grandes                    | OpenSpec               |
| Arquitetura de longa duração     | OpenSpec               |
| Governança e rastreabilidade     | OpenSpec               |

---

## Fluxo Moderno Recomendado

Na prática, os dois se complementam:

| Etapa            | Ferramenta               |
|------------------|--------------------------|
| Ideação rápida   | SpecKit (`specify`)      |
| Refinamento      | OpenSpec (`explore`)     |
| Execução         | OpenSpec (`apply`)       |
| Governance       | OpenSpec (`archive`)     |

**Exemplo real:**

```
# 1. Ideia rápida
specify → "Criar sistema de análise de leilão"

# 2. Formalizar
/opsx:propose auction-analysis-engine

# 3. Refinar
/opsx:explore auction-analysis-engine
→ OCR? IA? scoring? CQRS? cache? filas?

# 4. Implementar
/opsx:apply auction-analysis-engine
```

---

## O Grande Erro de Quem Começa

Usar OpenSpec como **"prompt generator"**.

Ele é mais próximo de:

- engenharia de mudança
- engenharia de produto
- governança arquitetural

---

## Setup Atual

```
.claude/
 ├── commands/
 │    └── opsx/
 │         ├── apply.md
 │         ├── archive.md
 │         ├── explore.md
 │         └── propose.md
 └── skills/
      ├── openspec-apply-change
      ├── openspec-archive-change
      ├── openspec-explore
      └── openspec-propose
```

Com esse setup, o Claude Code atua simultaneamente como **arquiteto**, **analista**, **implementador** e **change manager**.

Esse é provavelmente o workflow mais avançado hoje para engenharia assistida por IA.
