package com.example.amistapp.Administrador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.amistapp.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.text.style.TextAlign
import com.example.amistapp.Rutas

@Composable
fun BodyVentanaAdminUsuarios(navController: NavController){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(32.dp), // Aumenta el espaciado entre los botones
        verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { navController.navigate(Rutas.altaUsuario) },colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        ),
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Alta Usuarios",
                textAlign = TextAlign.Center)

        }
//    }
//    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { /*
        Baja Usuarios */ },colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        ),
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Baja Usuarios",
                textAlign = TextAlign.Center)
        }
    }

        Spacer(modifier = Modifier.height(60.dp))

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(32.dp), // Aumenta el espaciado entre los botones
        verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { /* AActivar Usuarios */ },colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        ),
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Activar Usuarios",
                textAlign = TextAlign.Center)
        }
//    }
//
//    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { /* Añadir Administrador */ },colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        ),
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Añadir administrador",
                textAlign = TextAlign.Center)
        }
    }}
}