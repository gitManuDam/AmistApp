package com.example.amistapp.Modelos
//Manuel
data class MensajeChat(

    val sender: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val leido: Boolean = false
) {

}
