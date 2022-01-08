package com.example.proyectologin

data class Chat(
    var id: String = "",
    var name: String = "",
    var inicio: Int = 0,
    var phone: String = "",
    var users: List<String> = emptyList()

)

public data class Admin(
        var otherUser: String =""
)


