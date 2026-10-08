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

---

## ENTRADA — 2026-10-06 · Sesión 2 · Bloque 2A + 2A-bis cerrados

**Versión/build:** 0.1.0-mvp-debug
**Objetivo:** Validar arquitectura completa UI ↔ ViewModel ↔ Repository (sin Room) con el primer dominio funcional: Ingredientes.

### QUÉ SE HIZO
- Bloque 2A: pantalla Ingredientes funcional con 3 datos de ejemplo en memoria.
  - Botón "+ Nuevo pastel" en Home navega a Ingredientes.
  - Bottom Sheet para crear ingrediente (nombre, categoría, presentación, cantidad, unidad, precio, proveedor).
  - Lista con tarjetas, eliminar por ítem.
  - Estados: Cargando / Vacío / Con datos.
- Bloque 2A-bis: botón "Resetear datos de ejemplo" en menú ⋮ con diálogo de confirmación.
- Fix `Icons.Filled.ArrowBack` deprecated → `Icons.AutoMirrored.Filled.ArrowBack`.
- i18n ES/EN/PT completos para todas las cadenas nuevas.

### ARCHIVOS
- `domain/model/Ingrediente.kt`
- `domain/repository/IngredienteRepository.kt`
- `data/repository/IngredienteRepositoryEnMemoria.kt`
- `data/repository/RepositorioProvider.kt`
- `ui/screens/ingredientes/{IngredientesScreen,IngredientesViewModel,CrearIngredienteSheet}.kt`
- `ui/navigation/Destinations.kt` (+ ruta INGREDIENTES)
- `PastelProApp.kt` (registro de ruta + callback Home)
- `res/values{,-en,-pt}/strings.xml` (+ 24 cadenas)
- `app/build.gradle.kts` (+ lifecycle-viewmodel-compose 2.8.6)

### COMANDOS
- `./gradlew assembleDebug` → BUILD SUCCESSFUL in 1m 18s (sin warnings)
- `find app/build/outputs/apk/debug -name "*.apk"` → 16 MB
- `git push origin main` → ae4b15b

### BUILD
PASS · sin warnings.

### TESTS
N/A (aún sin tests unitarios — se añaden en Bloque 3 con motor de costos).

### DISPOSITIVO
- Xiaomi Redmi 9S · flujo completo validado:
  - Navegación Home → Ingredientes ✅
  - Alta de ingrediente vía Bottom Sheet ✅
  - Lista reactiva ✅
  - Eliminación individual ✅
  - Reset de ejemplos con diálogo ✅
  - Light y dark mode correctos ✅
- Cubot KingKong 5: pendiente.

### RESULTADO REAL
- Arquitectura validada de punta a punta.
- UI y ViewModel funcionan sin conocer la implementación del Repository.
- El patrón es replicable: cuando metamos Room en 2B, solo cambia el Repository.

### ¿ERA FIX?
Sí — reset de ejemplos funciona. ArrowBack deprecated resuelto.

### HIPÓTESIS DESCARTADAS
- "Material3 AlertDialog tiene problemas con tema oscuro custom" → FALSO, se ve perfecto.
- "Bottom Sheet necesita configuración especial en dark mode" → FALSO, respeta el tema automáticamente.

### SUGERENCIA
`git tag v0.1.0-en-memoria` antes de Bloque 2B (Room) como punto de retorno.

### DEUDA/PENDIENTE
- Bloque 2B: Room real (persistencia).
- Prueba en Cubot KingKong 5.
- Tests unitarios del motor de costos (Bloque 3).

---

## ENTRADA — 2026-10-06 · Sesión 2 · Bloque 2B cerrado (Room real)

**Versión/build:** 0.1.0-mvp-debug
**Objetivo:** Reemplazar Repository en memoria por Room (SQLite) sin tocar UI ni ViewModel.

### QUÉ SE HIZO
- Añadido Room 2.6.1 + KSP 2.0.20-1.0.25.
- Entity, DAO, Database, Mapper, RepositoryRoom.
- Service Locator (`RepositorioProvider`) con `init(context)`.
- `MainActivity.onCreate` inicializa el provider antes de setContent.
- Eliminado `IngredienteRepositoryEnMemoria.kt` (obsoleto).
- **CERO cambios en UI, ViewModel, ni Navigation.**
- i18n intacto.

### ARCHIVOS
- `app/build.gradle.kts` (+ KSP + Room)
- `build.gradle.kts` (raíz, + plugin KSP)
- `data/local/IngredienteEntity.kt`
- `data/local/IngredienteDao.kt`
- `data/local/PastelProDatabase.kt`
- `data/local/mapper/IngredienteMapper.kt`
- `data/repository/IngredienteRepositoryRoom.kt`
- `data/repository/RepositorioProvider.kt` (init con Context)
- `MainActivity.kt` (init provider)
- Eliminado: `data/repository/IngredienteRepositoryEnMemoria.kt`

### COMANDOS
- `./gradlew assembleDebug` → BUILD SUCCESSFUL in 4m 28s (primera compilación con KSP)
- 38 tasks ejecutados, 0 warnings

### BUILD
PASS · sin warnings.

### TESTS
N/A (aún sin tests unitarios).

### DISPOSITIVO
- Xiaomi Redmi 9S · Room confirmado:
  - Ingredientes de ejemplo cargados al reset.
  - "mantequilla" agregada manualmente.
  - App cerrada por completo.
  - App reabierta → los 4 ingredientes siguen ahí (incluido mantequilla).
  - Persistencia real validada en disco.

### RESULTADO REAL
- Room escribe a SQLite correctamente.
- Flow reactivo de Room → Repository → ViewModel → UI funciona.
- Mapper Entity↔Domain convierte sin pérdida.
- BigDecimal sobrevive ida y vuelta como TEXT.

### ¿ERA FIX?
No aplica — funcionalidad nueva completada.

### HIPÓTESIS DESCARTADAS
- "Room + KSP + AGP 8.5.2 tarda mucho en compilar" → normal en primera vez (4m 28s), incremental luego ~30s.

### SUGERENCIA
En Bloque 3 (motor de costos), añadir tests unitarios JUnit.

### DEUDA/PENDIENTE
- Bloque 3: motor de costos.
- Prueba en Cubot KingKong 5.
- Diseño de migraciones Room para futuras entidades.

---

## ENTRADA — 2026-10-06 · Sesión 3 · Bloques 3A, 3B, 3C cerrados

**Versión/build:** 0.1.0-mvp-debug
**Objetivo:** Construir el motor de cálculos puro (Kotlin, sin Android) con tests unitarios JUnit.

### QUÉ SE HIZO
- **3A:** MotorUnidades + MotorCostoIngrediente. 14 tests.
- **3B:** MotorEscalado. 24 tests. Redondeo CEILING para discretas, HALF_UP para fraccionables.
- **3C:** MotorManoObra + MotorEnergia + MotorMerma. 26 tests.
- **Total:** 6 motores, 64 tests, 0 fallos.

### ARCHIVOS
- `engine/Unidad.kt` — enum con categoría + factor base
- `engine/MotorUnidades.kt` — conversión entre unidades compatibles
- `engine/MotorCostoIngrediente.kt` — costo de uso de ingrediente
- `engine/MotorEscalado.kt` — escalado por personas/piezas con redondeo por tipo
- `engine/MotorManoObra.kt` — horas × valor_hora + ganancia por hora
- `engine/MotorEnergia.kt` — prorrateo mensual (marcado como ESTIMACIÓN)
- `engine/MotorMerma.kt` — costo antes/merma/total (§31)
- 6 archivos de test con JUnit 4.13.2

### COMANDOS
- `./gradlew test` → BUILD SUCCESSFUL
- 64 tests: 0 failures

### BUILD
PASS · sin warnings.

### TESTS
64/64 PASS.

### DISPOSITIVO
N/A (motor puro, sin UI todavía).

### RESULTADO REAL
Motor de cálculos validado matemáticamente. Listo para conectar a UI en Bloques 4+.

### HIPÓTESIS DESCARTADAS
Ninguna — motor limpio desde el inicio.

### SUGERENCIA
Validación con precios reales de pasteles antes de cerrar Bloque 3.

### DEUDA/PENDIENTE
- Bloque 3D: MotorPrecio (margen vs markup).
- Conexión motor ↔ UI en Bloque 4 (Recetas).

---

## ENTRADA — 2026-10-06 · Sesión 3 · Bloque 3D cerrado · MOTOR COMPLETO

**Versión/build:** 0.1.0-mvp-debug
**Objetivo:** Cerrar el motor de cálculos puro con MotorPrecio (margen vs markup).

### QUÉ SE HIZO
- `engine/MotorPrecio.kt` con margen vs markup diferenciados matemáticamente.
- 20 tests JUnit. 2 bugs de test corregidos en 3D-bis + 1 bug real del motor (precio=0).
- **Motor completo: 7 motores, 84 tests, 0 fallos.**

### ARCHIVOS
- `engine/MotorPrecio.kt`
- `test/engine/MotorPrecioTest.kt`

### COMANDOS
- `./gradlew test` → BUILD SUCCESSFUL in 10s · 84 tests, 0 fallos
- `git tag v0.3.0-motor-completo` → pusheado

### BUILD
PASS · sin warnings.

### TESTS
84/84 PASS. Cobertura acumulada de:
- Unidades (masa, volumen, conteo)
- Costo de ingrediente
- Escalado (personas, piezas)
- Mano de obra (horas, desglose, ganancia por hora)
- Energía (prorrateo mensual)
- Merma (antes/merma/total)
- Precio (margen vs markup + 3 precios sugeridos)

### DISPOSITIVO
N/A (motor puro, sin UI aún).

### RESULTADO REAL
Motor matemáticamente validado. Listo para conectar a UI.

### ¿ERA FIX?
3D-bis: sí. Causa raíz: 1 caso borde real (precio=0) + 2 errores míos de aserción en tests. Corrección quirúrgica, sin refactor.

### HIPÓTESIS DESCARTADAS
- "El motor tenía bug estructural" → FALSO, era un caso borde defensivo.
- "Margen y markup dan el mismo resultado" → FALSO, con costo $100 y 40%: margen da $166.67, markup da $140.00.

### SUGERENCIA
Crear test de integración con 3 pasteles reales (§77 del Maestro).

### DEUDA/PENDIENTE
- Validación con datos reales de Eduardo + esposa.
- Bloque 4: UI de Recetas que consuma el motor.
- Bloque 5: UI de costo + precio con los 3 precios sugeridos.

---

## ENTRADA — 2026-10-07 · Sesión 4 · Bloque 4B cerrado + UX Home

**Versión/build:** 0.1.0-mvp-debug
**Objetivo:** Cerrar ingredientes de receta, arreglar UX del Home, preparar escalado.

### QUÉ SE HIZO
- Bloque 4B: ingredientes de receta con Room + Bottom Sheet selector.
- Empty state cuando no hay ingredientes.
- Fix UX: Home rediseñado con accesos rápidos (Maestro §13).
- Icono adaptativo con safe zone correcta (logo al 80%).

### ARCHIVOS
- `ui/screens/HomeScreen.kt` (nuevo, rediseñado)
- `ui/screens/recetas/{AgregarIngredienteSheet,RecetaDetalleScreen,RecetaDetalleViewModel}.kt`
- `data/local/{RecetaEntity,RecetaIngredienteEntity,RecetaDao}.kt`
- `data/local/mapper/RecetaMapper.kt`
- `res/mipmap-*/ic_launcher*` (iconos adaptativos + legacy)

### COMANDOS
- `./gradlew assembleDebug` → BUILD SUCCESSFUL
- `git tag v0.4.2-ingredientes-receta` pusheado

### BUILD
PASS

### TESTS
84 tests motor (siguen en verde).

### DISPOSITIVO
- Xiaomi Redmi 9S: persistencia de ingredientes de receta confirmada tras cerrar/reabrir.
- Icono en launcher correcto.

### RESULTADO REAL
- 4B funciona end-to-end.
- UX Home arreglada.
- Listo para 4C (escalado).

### SUGERENCIA
En 4C: migración Room v2→v3 real (no destructiva).

---

## ENTRADA — 2026-10-07 · Sesión 4 · Bloque 4C cerrado (escalado)

**Versión/build:** 0.1.0-mvp-debug
**Objetivo:** Migración Room v2→v3 + UI de escalado conectada al MotorEscalado.

### QUÉ SE HIZO
- Migración v2 → v3 con recreate pattern (SQLite < 3.35 no soporta DROP COLUMN).
- `Receta`: `rendimiento: String` → `rendimientoCantidad: Int` + `rendimientoUnidad: String`.
- `EscalarRecetaSheet` con input + chips rápidos (×2, ×3, ×10, 100).
- `RecetaDetalleScreen`: botón "Escalar" (relleno) + "Agregar ingrediente" (outline).
- `MotorEscalado` conectado: escalado + redondeo por tipo de unidad.
- Fix migración (recreate pattern) tras primer intento fallido — Room crasheaba por columna fantasma.

### ARCHIVOS
- `data/local/PastelProDatabase.kt` (v3 + MIGRACION_2_3 recreate)
- `data/local/RecetaEntity.kt`, `domain/model/Receta.kt`, `mapper/RecetaMapper.kt`
- `ui/screens/recetas/{CrearRecetaSheet,RecetasScreen,RecetaDetalleScreen,EscalarRecetaSheet}.kt`
- `res/values{,-en,-pt}/strings.xml` (+15 cadenas)

### COMANDOS
- `./gradlew assembleDebug` → BUILD SUCCESSFUL
- Migración validada en Xiaomi Redmi 9S · persistencia preservada

### BUILD
PASS · sin warnings

### TESTS
84 tests de motor siguen en verde.

### DISPOSITIVO
- Xiaomi Redmi 9S:
  - 3 recetas persistieron tras migración v2→v3 ✅
  - Escalado: 20 porciones → 200 porciones = factor ×10
  - harina 1 kg → 10 kg ✅

### RESULTADO REAL
- Escalado funcional end-to-end.
- Migración Room real (no destructiva) probada en dispositivo.
- MotorEscalado integrado con la UI.

### ¿ERA FIX?
Sí — migración inicial falló (columna fantasma). Corregido con recreate pattern.

### HIPÓTESIS DESCARTADAS
- "SQLite moderno permite DROP COLUMN" → FALSO en Android 8-11.
- "Room re-ejecuta migración si falla" → Room hace rollback limpio pero necesita fix de código.

### SUGERENCIA
Bloque 5: costo real de receta + 3 precios sugeridos (mínimo/recomendado/premium).

### DEUDA/PENDIENTE
- Wizard "Nuevo pastel" (Bloque 5).
- Tests de migración con MigrationTestHelper (Bloque 6).
- Transiciones entre pantallas (Bloque 6).

---

## ENTRADA — 2026-10-07 · Sesión 4 · Bloque 5A cerrado (costo + precio)

**Versión/build:** 0.1.0-mvp-debug
**Objetivo:** Conectar motor de costo y precios a UI. Cerrar ciclo de valor.

### QUÉ SE HIZO
- `CalculadoraCostoReceta` con desglose + merma + precios (12 tests).
- `CostoPrecioScreen` con 3 tarjetas de precio (mínimo/recomendado/premium).
- Fallback de match por nombre para recetas con IDs huérfanos.
- `DiagnosticLogger` para debug en dispositivo sin ADB (escribe a Download/).
- Crash handler global que captura stacktrace completo.
- Fix crash: `%` no escapado en string ES.
- Formatters defensivos con `setScale(2)` antes de `format()`.

### ARCHIVOS
- `engine/CalculadoraCostoReceta.kt` + tests
- `diagnostico/DiagnosticLogger.kt`
- `ui/screens/recetas/CostoPrecioScreen.kt`
- `ui/screens/recetas/CostoPrecioViewModel.kt`
- `MainActivity.kt` (init logger)
- `res/xml/file_paths.xml` + provider en Manifest

### COMANDOS
- `./gradlew test` → 96 tests, 0 fallos
- `./gradlew assembleDebug` → BUILD SUCCESSFUL
- Probado en Xiaomi Redmi Note 9S · scroll completo sin crash

### DISPOSITIVO
- Receta "pastel de naranja" (50 porciones):
  - Costo $239.40
  - Precio recomendado $399.00 (margen 40%, ganancia $159.60)

### ¿ERA FIX?
Sí — 2 bugs resueltos:
1. `UnknownFormatConversionException` por `%` no escapado en strings.xml
2. ViewModel no reactivo (leía inventario antes de que Room emitiera)

### HIPÓTESIS DESCARTADAS
- "El crash era por LazyColumn keys duplicados" → FALSO, era el string resource.
- "NumberFormat crashea con BigDecimal" → FALSO en este caso.

### SUGERENCIA
Bloque 5B: mano de obra + energía + merma configurable.

### DEUDA TÉCNICA
[IMPORTANTE] Leche guardada como "kg" en inventario pero usada como "l" en receta.
   El motor no convierte entre categorías (correcto). Mensaje claro al usuario.
   Deuda UX: permitir editar unidad de compra de un ingrediente existente.
