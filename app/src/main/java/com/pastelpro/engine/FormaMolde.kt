package com.pastelpro.engine

/**
 * Formas de molde soportadas.
 * Cada forma tiene un factor de forma para el cálculo de volumen.
 */
enum class FormaMolde(val etiqueta: String) {
    REDONDO("Redondo"),
    CUADRADO("Cuadrado"),
    RECTANGULAR("Rectangular"),
    CORAZON("Corazón");

    companion object {
        fun desdeEtiqueta(e: String): FormaMolde? =
            values().firstOrNull { it.etiqueta.equals(e, ignoreCase = true) }
    }
}
