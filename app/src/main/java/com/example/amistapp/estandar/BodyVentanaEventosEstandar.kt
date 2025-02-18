package com.example.amistapp.estandar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun BodyVentanaEventosEstandar(navController: NavHostController){
    Text(text = "Pantalla Eventos en estandar", fontSize = 24.sp, modifier = Modifier.fillMaxSize())
}