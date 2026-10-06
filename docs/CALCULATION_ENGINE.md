# PastelPro · Motor de Cálculos

> Zona crítica. Todo cambio aquí requiere casos de prueba.

## Reglas base
- Dinero: BigDecimal o Long en centavos. NUNCA Float/Double.
- Unidades: kg, g, L, ml, pieza, docena, paquete, caja, unidad.
- Precisión interna alta; redondeo solo al presentar.

## Costo de ingrediente
`costo_consumido = (precio_compra / cantidad_compra) * cantidad_usada`

Ejemplo: 1 kg harina = $28 → 250 g = $7.

## Costo completo
Costo real =
  ingredientes + rellenos + cobertura + decoración
  + empaque + mano de obra + gas + electricidad
  + merma + otros

## Mano de obra
`costo = horas_totales * valor_hora`
Desglose posible: preparación, horneado, decoración, limpieza.

## Energía (MVP)
Método simple: gasto mensual / producción mensual aproximada.
Se presenta como **estimación**, no medición exacta.

## Merma
Muestra:
1. Costo antes de merma.
2. Costo de merma.
3. Costo total.

## Precio
Distinguir explícitamente:
- Costo
- Markup
- Margen

El usuario ve "Quiero ganar 40 %". El sistema calcula el precio correcto.
El sistema muestra: precio mínimo, recomendado, premium, ganancia, margen, ganancia/hora.

## Moldes
Capacidad geométrica ≠ volumen útil recomendado de mezcla.
Factor de llenado configurable.
