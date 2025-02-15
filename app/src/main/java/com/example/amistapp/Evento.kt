package com.example.amistapp

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Evento(
    var id: String? = null,
    val descripcion: String = "",
    val longitud: Double? = 0.0,
    val latitud: Double? = 0.0,
    val inscritos: List<String>,// Almacena los email de los usuarios inscritos al evento
    val asistentes: List<AsistenteEvento>,
    val fecha: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val hora: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
    val plazoInscripcion: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
)
