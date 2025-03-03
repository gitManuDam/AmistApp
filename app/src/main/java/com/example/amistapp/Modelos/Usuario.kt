package com.example.amistapp.Modelos

data class Usuario(
    val idUsuario: String?= null,
    val activado: Boolean = false,
    val email: String = "",
    val enLinea: Boolean = false,
    val role: List<String> = emptyList(), // como puede tener dos roles: administrador y estandar
    val perfil: Perfil? = null // el perfil del usuario que inicialmente estará vacio
)
