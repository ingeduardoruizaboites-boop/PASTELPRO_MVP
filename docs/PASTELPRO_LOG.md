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

---

## ENTRADA — 2026-10-05 · Sesión 1 · Bloque 1B + 1C cerrados

**Versión/build:** 0.1.0-mvp-debug
**Objetivo:** Construir UI base (splash, onboarding, navegación 5 tabs), implementar tema claro + oscuro, y validar en dispositivo físico.

### QUÉ SE HIZO
- Bloque 1B: Kotlin + Jetpack Compose + Material 3 + Navigation Compose.
- Splash (1.2s, fade-in 400ms) → Welcome → Setup 3 pasos → Main con 5 tabs.
- Tema visual cálido-premium: crema + cacao + berry.
- Bloque 1C: dark mode cálido real (dos color schemes) + edge-to-edge adaptativo + Home rediseñado (botón compacto, ya no `fillMaxSize` erróneo).
- Refactor: pantallas usan `MaterialTheme.colorScheme.*` (no colores hardcodeados).

### ARCHIVOS
- Kotlin: `MainActivity.kt`, `PastelProApp.kt`
- UI: `ui/theme/{Color,Theme,Type}.kt`, `ui/navigation/Destinations.kt`, `ui/screens/{Splash,Welcome,Setup,MainTabs}.kt`
- Recursos: `AndroidManifest.xml`, `res/values{,-en,-pt}/strings.xml`, `res/values/{themes,colors}.xml`, `res/drawable/ic_launcher_pastelpro.xml`
- Gradle: `app/build.gradle.kts`, `gradle.properties` (ajustado a Codespaces: heap 1.5 GB + Kotlin daemon 1 GB)
- Docs: `docs/PASTELPRO_LOG.md` (esta entrada)

### COMANDOS
- `./gradlew assembleDebug` → BUILD SUCCESSFUL (3m 22s primera vez, 9s incremental)
- `git commit -m "feat(theme): dark mode cálido..."` → commit `a2f3588`

### BUILD
PASS

### TESTS
N/A (aún sin tests unitarios).

### DISPOSITIVO
- Xiaomi Redmi 9S · flujo completo validado en modo claro (capturas adjuntas en conversación).
- Dark mode confirmado visualmente por el usuario (pendiente captura en bitácora).
- Cubot KingKong 5: pendiente de prueba.

### RESULTADO REAL
- Navegación fluida, sin crashes.
- Tema claro y oscuro funcionando, respeta al sistema.
- Iconos de barra de estado adaptativos.

### ¿ERA FIX?
Sí — 2 errores resueltos:
1. OOM de Gradle daemon (heap 2 GB → 1.5 GB + Kotlin daemon 1 GB).
2. Botón Home con `fillMaxSize()` que ignoraba `height(64.dp)`.

### HIPÓTESIS DESCARTADAS
- "Java 25 funciona para Android" → FALSO, requiere JDK 17.
- "El botón gigante era intencional" → FALSO, era bug.

### SUGERENCIA
Toggle manual de apariencia (Sistema / Claro / Oscuro) persistido en DataStore.

### DEUDA/PENDIENTE
- Captura dark mode en bitácora.
- Prueba en Cubot KingKong 5.
- Bloque 2: Room + primer vertical slice (Ingredientes).
