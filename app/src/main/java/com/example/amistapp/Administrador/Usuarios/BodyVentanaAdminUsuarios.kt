package com.example.amistapp.Administrador.Usuarios

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
import com.example.amistapp.Parametros.Rutas

// Autora: Izaskun
// Crea una interfaz de usuario que permite al administrador realizar las siguientes operaciones
// alta usuarios
// baja de usuarios
// activar/desactivar
// añadir/quitar roles
// cada una de estas opciones le llevará a la ventana adecuada
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
        Button(onClick = { navController.navigate(Rutas.bajaUsuarios) },colors = ButtonDefaults.buttonColors(
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
        Button(onClick = { navController.navigate(Rutas.activarDesActivar) },colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        ),
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Activar/Desactivar Usuarios",
                textAlign = TextAlign.Center)
        }

        Button(onClick = { navController.navigate(Rutas.cambiarRole) },colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        ),
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Añadir/Quitar role administrador",
                textAlign = TextAlign.Center)
        }
    }}
}