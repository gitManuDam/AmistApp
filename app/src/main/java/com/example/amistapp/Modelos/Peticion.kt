package com.example.amistapp.Modelos
//Manuel
data class Peticion(var id :String,var emisor:String, var receptor:String, ) {
    constructor() : this(
        id = "",
        emisor = "",
        receptor = ""

        )
}