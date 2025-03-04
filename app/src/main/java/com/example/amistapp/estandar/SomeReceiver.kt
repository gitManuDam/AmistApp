package com.example.amistapp.estandar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
//Manuel

class SomeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //Recuperar datos del Intent (si es necesario)
        val actionData = intent.getStringExtra("valor_boton")


        //Realizamos la acción que necesitemos a continuación..
        Log.d("Fernando", "Acción recibida: $actionData")
    }
}