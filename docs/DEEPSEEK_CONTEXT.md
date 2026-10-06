# PastelPro · Contexto de Continuidad

> Resumen para retomar chats. Actualizar al cierre de cada sesión.

## Estado
- Repo: `ingeduardoruizaboites-boop/PASTELPRO_MVP` (privado).
- Rama: `main`. Commit actual: `a2f3588` (Bloque 1C cerrado).
- Fase: Fin de Sesión 1 · Fundación.
- Build: PASS · APK debug probado en Xiaomi Redmi 9S.

## Stack fijado
- Kotlin 2.0.20 · AGP 8.5.2 · Gradle 8.9 (wrapper) · JDK 17.
- Jetpack Compose BOM 2024.09.00 + Material 3.
- Navigation Compose 2.8.0.
- Android SDK Platform 34 · Build-tools 34.0.0.
- Room: pendiente de integrar (Bloque 2).
- i18n: ES/EN/PT (sin strings hardcodeadas).

## Estructura
- `/docs` — 7 documentos de control.
- `/app/src/main/java/com/pastelpro/{ui,domain,data,engine}`.
- `/app/src/main/res/{values,values-en,values-pt}`.
- Tema: `ui/theme/{Color,Theme,Type}.kt` con light + dark cálidos.
- Navegación: `ui/navigation/Destinations.kt` (5 tabs: Inicio, Recetas, Pedidos, Compras, Más).

## Pantallas existentes
- Splash · Welcome · Setup (3 pasos) · Home · 4 placeholders.

## Reglas vigentes
- Local-first, offline total.
- Dinero con BigDecimal/Long, nunca Float/Double.
- Free no bloquea cálculo fundamental.
- Una decisión a la vez en formularios.
- Sin textos técnicos visibles.
- Animaciones 180–250 ms.
- Todos los colores vía `MaterialTheme.colorScheme.*`.

## Pendiente inmediato
Bloque 2 — Room + Ingredientes (primer vertical slice completo):
UI → ViewModel → Repository → Room → vuelta a UI.

## Dueño / tester principal
Eduardo · Xiaomi Redmi 9S (principal) · Cubot KingKong 5 (secundario).
Validación de dominio real: su esposa (repostera).
