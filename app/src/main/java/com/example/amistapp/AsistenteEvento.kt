package com.example.amistapp

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class AsistenteEvento(
    val email: String = "", //
    val hora: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
)
