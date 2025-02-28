package com.example.amistapp.Modelos

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
// Autora: Izaskun
data class Evento(
    var id: String? = null,
    val descripcion: String = "",
    val longitud: Double? = 0.0,
    val latitud: Double? = 0.0,
    val inscritos: List<String>,// Almacena los email de los usuarios inscritos al evento
    val asistentes: List<AsistenteEvento>,
    val fecha: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val hora: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
    val plazoInscripcion: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val timestamp: Long = System.currentTimeMillis(), // para ordenarlos en la rv
    val fotos: List<String> = emptyList()
){
    // Constructor sin argumentos  por Firebase, daba error sin él
    constructor() : this(
        id = null,
        descripcion = "",
        latitud = 0.0,
        longitud = 0.0,
        fecha = "",
        hora = "",
        plazoInscripcion = "",
        inscritos = emptyList(),
        asistentes = emptyList(),
        timestamp = 0L
    )
}
