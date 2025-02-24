package com.example.amistapp

data class MensajeChat(
    val id: String = "",
    val sender: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {

}
