package com.example.amistapp.estandar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun BodyVentanaCompatibles(navController: NavHostController){
    Text(text = "Pantalla Compatibles", fontSize = 24.sp, modifier = Modifier.fillMaxSize())
}