package com.example.amistapp.Administrador.Eventos.GoogleMaps

import com.google.android.gms.maps.model.LatLng

data class MapMaker(
    val position: LatLng,
    val title: String,
    val snippet: String? = null
)
