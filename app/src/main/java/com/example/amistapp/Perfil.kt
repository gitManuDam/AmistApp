package com.example.amistapp

data class Perfil(
    val completado: Boolean = false,
    val nick: String = "",
    val edad: Int = 0,
    val fotoPerfil: String = "", // aquí guardaremos la URL de la imagen
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
