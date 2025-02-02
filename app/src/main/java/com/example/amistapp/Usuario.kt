package com.example.amistapp

data class Usuario(
    val idUsuario: String?= null,
    val email: String = "",
    val role: List<String> = emptyList(), // como puede tener dos roles: administrador y estandar
    val nick: String = "",
    val amigos: List<String> = emptyList(), // lista de amigos, tendrá el idUsuario de cada amigo
    val relacionSeria: Boolean = false,
    val interesDeporte: Int = 0,
    val interesArte: Int = 0,
    val interesPolitica: Int = 0,
    val tieneHijos: Boolean = false,
    val quiereHijos: Boolean = false,
    val interesadoEn: String = "", // contendrá Hombres, Mujeres o Ambos
    val genero: String = "" // contendrá masculino, femenino
)
