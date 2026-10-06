# PASTELPRO LOG

> Bitácora append-only. Nunca sobrescribir entradas previas.

---

## ENTRADA — 2026-10-05 · Sesión 0→1 · Fundación

**Versión/build:** 0.0.0 (pre-MVP)
**Objetivo:** Poner el proyecto en estado reproducible: entorno, repo, documentación base, estructura Gradle.

### QUÉ SE HIZO
- Creación del repositorio `ingeduardoruizaboites-boop/PASTELPRO_MVP` en GitHub (privado).
- Apertura de Codespace sobre `main`.
- Reparación de entorno:
  - JDK 17 instalado en paralelo (Java 25 incompatible con AGP).
  - Android SDK cmdline-tools 12.0 instalado.
  - SDK Platform 34 + Build-tools 34.0.0 instalados.
  - Variables ANDROID_HOME, ANDROID_SDK_ROOT, JAVA_HOME configuradas.
- Definición de stack: Kotlin + Jetpack Compose + Material 3 + Gradle Kotlin DSL.
- Creación de los 7 documentos de control en `/docs`.

### ARCHIVOS
- `.gitignore`, `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `local.properties`
- `app/build.gradle.kts`, `app/proguard-rules.pro`
- `docs/PRODUCT_SPEC.md`, `docs/UI_SPEC.md`, `docs/CALCULATION_ENGINE.md`, `docs/TEST_PLAN.md`, `docs/ROADMAP.md`, `docs/PASTELPRO_LOG.md`, `docs/DEEPSEEK_CONTEXT.md`

### COMANDOS
- `apt-get install openjdk-17-jdk-headless`
- `sdkmanager --install platform-tools platforms;android-34 build-tools;34.0.0`
- `gradle wrapper --gradle-version 8.9` (pendiente en 1A)

### BUILD
Pendiente en el cierre de 1A.

### TESTS
N/A (aún sin código de producción).

### DISPOSITIVO
N/A (aún sin APK).

### RESULTADO REAL
Pendiente de evidencia.

### ¿ERA FIX?
Sí — entorno reparable. Java 25 → 17 resuelto. SDK ausente → instalado.

### HIPÓTESIS DESCARTADAS
- "El Codespace no permite instalar JDK 17" — FALSO, se instaló sin problemas.
- "El SDK ya venía preinstalado" — FALSO, había que instalarlo manualmente.

### SUGERENCIA
Activar Issues en GitHub para bitácora paralela de pruebas físicas.

### DEUDA/PENDIENTE
- Compilar y validar Gradle en Sub-bloque 1A.
- Crear código Kotlin + Compose en Sub-bloque 1B.
- Primer APK debug.
