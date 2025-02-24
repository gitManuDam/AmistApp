package com.example.amistapp

data class Chat(
    val id: String? = null,
    val usuarios: List<String>,
    val mensajes: List<MensajeChat>,


) {
    constructor() : this(
        id = null,
        usuarios = emptyList(),
        mensajes = emptyList(),

    )
}