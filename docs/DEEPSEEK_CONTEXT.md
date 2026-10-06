# PastelPro · Contexto de Continuidad

> Este archivo resume el estado del proyecto para retomar chats.

## Estado
- Repo: `ingeduardoruizaboites-boop/PASTELPRO_MVP` (privado).
- Rama activa: `main`.
- Fase: Sesión 1 · Fundación.
- Build: pendiente de verificación (Sub-bloque 1A).

## Stack fijado
- Kotlin 2.0.20
- AGP 8.5.2
- Gradle Wrapper 8.9
- Jetpack Compose (BOM 2024.09.00) + Material 3
- Room (aún no integrado)
- Navigation Compose 2.8.0
- Java 17 (JDK forzado en Codespaces)
- Android SDK Platform 34 / Build-tools 34.0.0

## Estructura
- `/docs` — los 7 documentos de control.
- `/app/src/main/java/com/pastelpro/{ui,domain,data,engine}` — código.
- `/app/src/main/res/{values,values-en,values-pt}` — i18n.

## Reglas vigentes
- Local-first, offline total.
- Dinero con BigDecimal/Long, nunca Float/Double.
- Free no bloquea cálculo fundamental.
- Una decisión a la vez en formularios.
- Sin textos técnicos visibles.
- Animaciones 180–250 ms.

## Pendiente inmediato
Sub-bloque 1A: generar wrapper y validar que Gradle arranca sin error.
Sub-bloque 1B: Splash + Bienvenida + Onboarding + navegación 5 tabs + i18n + APK debug.

## Dueño / tester principal
Eduardo. Validación de dominio real: su esposa (repostera).
