package com.example.proyectologin

import java.util.*

data class Message(
        var message: String = "",
        var from: String = "",
        var by: String = "",
        var from2: String ="",
        var dob: Date = Date(),
        var imgUrl: String ="",
        var opcion : Int = 0

)