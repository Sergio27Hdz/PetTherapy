package com.example.proyectologin

import androidx.appcompat.app.AppCompatActivity

class user : AppCompatActivity {
    constructor(name: String?, email: String?, key: String?) {
        this.name = name
        this.email = email
        this.key = key
    }

    constructor() {}

    var name: String? = null
    var email: String? = null
    var key: String? = null
}