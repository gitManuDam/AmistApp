package com.example.amistapp

data class Usuario(
    val idUsuario: String?= null,
    val email: String = "",
    val role: List<String> = emptyList(), // como puede tener dos roles: administrador y estandar
    val perfil: Perfil? = null // el perfil del usuario que inicialmente estará vacio
)
