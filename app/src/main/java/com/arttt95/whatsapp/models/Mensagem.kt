package com.arttt95.whatsapp.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Mensagem(
    val idUsuario: String = "",
    val mensagem: String = "",
    @ServerTimestamp
    val data: Date? = null, // Date de java.util
)
