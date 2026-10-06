# PastelPro · Roadmap

## MVP 1 (en construcción)
Ingredientes · Unidades · Recetas · Moldes · Costos · Mano de obra
Energía · Merma · Precio · Ganancia · Lista de compras básica
ES/EN/PT · Local-first

## MVP 1.1
Inventario · Proveedores · Empaques · Pedidos · Historial

## V2
Planificador para X personas · Capas · Optimización de moldes
Producción · 3 leches · Compatibilidad pastel/empaque · Compras avanzadas

## V3
PDF · Cotizaciones · Clientes · Calendario · Estadísticas · Respaldo

## V4
Funciones inteligentes · Referencias de mercado
Automatización avanzada · Nube

## Regla de oro
No construir toda PastelPro de una vez.
Orden: estructura → navegación → datos → cálculos → UI → pruebas → monetización → publicación.

---

## 📌 INSIGHTS DE CAMPO — Sesión 2 (Eduardo + esposa repostera)

> Documentado 2026-10-06 · Fuente: conversación directa con Eduardo
> Uso: priorizar features según dolor real del usuario objetivo.

### Realidad operativa actual
1. **Las recetas base son para 5–10 personas.** Los pedidos reales llegan hasta 100 personas.
   → El escalado es la operación más frecuente. Debe ser trivial y a prueba de errores.

2. **Referencias de precio** vienen de: pastelerías locales, internet, Marketplace de Facebook, cobros anteriores propios.
   → La app necesita (V2) sección "Referencias de mercado" para comparar.

3. **Cómo pierden dinero:**
   - (a) Cotizar mal (no aplicar margen correcto).
   - (b) **Un insumo subió de precio y no se enteraron a tiempo.** ← el más crítico.

4. **Qué valoran los clientes:** sabor, sazón, tiempo de entrega.
   → El precio no es lo único, pero cotizar mal es donde ellas absorben el impacto.

### Implicaciones de diseño
- **MVP 1:** Motor de escalado robusto + motor de margen imposible de confundir.
- **V1.1:** Historial de precios de ingredientes + alerta "Este insumo subió X%".
- **V2:** Referencias de mercado + comparador de precios propios.
- **V2:** Al cotizar, verificar automáticamente si algún ingrediente no tiene precio actualizado en los últimos N días.
