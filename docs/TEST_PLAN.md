# PastelPro · Plan de Pruebas

## Criterio de "Lista para publicar"
Una versión NO se considera estable solo porque compila.
Debe superar:

### Funcionalidad
- Sin errores críticos.
- Rutas principales operativas.

### Cálculos
- Costos correctos.
- Márgenes correctos.
- Conversiones correctas.
- Escalado correcto.

### UX
- Una persona nueva puede hacer su primer cálculo sin explicación externa.

### UI
- Sin pantallas incompletas.
- Sin botones sin función.
- Sin textos técnicos.

### Persistencia
- Recetas, ingredientes y datos no se pierden al cerrar y reabrir.

### Dispositivo real
- Probada físicamente.
- Más de un tamaño/densidad.

### Protocolo de prueba del usuario
1. Instalar.
2. Abrir.
3. Crear ingrediente.
4. Crear receta.
5. Crear molde.
6. Calcular pastel.
7. Revisar costo.
8. Revisar precio.
9. Crear lista de compras.
10. Cerrar app.
11. Volver a abrir.
12. Confirmar persistencia.
13. Probar errores.
14. Probar distintos tamaños.

### Pruebas reales con pasteles
Antes de estabilizar el motor: 5–10 pasteles reales.
Comparar cálculo actual de la repostera vs PastelPro.
NO modificar matemáticas solo para coincidir. Investigar diferencias primero.
