package com.example.amistapp.Registro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.amistapp.R
import com.example.amistapp.Rutas

@Composable
fun RegistroVentana(navController: NavHostController){
    Column(modifier = Modifier
        .padding(vertical = 20.dp)
        .systemBarsPadding()) {
        Spacer(modifier = Modifier.height(50.dp))
        botonRegistro(navController)
}
}

@Composable
fun botonRegistro(navController: NavHostController) {
    Button(onClick = {navController.navigate(Rutas.registro)},
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
    ) {
        Text(text = "Ventana Registro")
    }
}