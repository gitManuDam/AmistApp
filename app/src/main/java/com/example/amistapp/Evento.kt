package com.example.amistapp

import com.google.firebase.Timestamp

data class Evento(
    val id: String? = null,
    val longitud: Double? = 0.0,
    val latitud: Double? = 0.0,
    val inscritos: List<String>,// Almacena los email de los usuarios inscritos al evento
    val asistentes: List<AsistenteEvento>,
    val fecha: Timestamp,
    val hora: Timestamp,
    val plazoInscripcion: Timestamp
)
