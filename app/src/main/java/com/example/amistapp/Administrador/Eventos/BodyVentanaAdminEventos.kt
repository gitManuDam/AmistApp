package com.example.amistapp.Administrador.Eventos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.amistapp.R
import com.example.amistapp.Rutas

@Composable
fun BodyVentanaAdminEventos(navController: NavController){
    Spacer(modifier = Modifier.height(80.dp))
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
//        horizontalArrangement = Arrangement.spacedBy(32.dp), // Aumenta el espaciado entre los botones
        verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { navController.navigate(Rutas.crearEvento) },colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
        ) {
            Text(text = "Crear evento",
                textAlign = TextAlign.Center)

        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
//        horizontalArrangement = Arrangement.spacedBy(32.dp), // Aumenta el espaciado entre los botones
        verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { /* Fotos eventos*/ },colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
        ) {
            Text(text = "Fotos eventos",
                textAlign = TextAlign.Center)

        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
//        horizontalArrangement = Arrangement.spacedBy(32.dp), // Aumenta el espaciado entre los botones
        verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = {

            navController.navigate(Rutas.proximosEventos)

                        },
            colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
        ) {
            Text(text = "Próximos eventos",
                textAlign = TextAlign.Center)

        }

//        Button(onClick = {
//
//            onNavigateTo("ProximosEventos") },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = colorResource(id = R.color.botones),  // Color de fondo
//                contentColor = colorResource(id = R.color.textoBotones) // Color del texto
//            ),
//            modifier = Modifier.fillMaxWidth()
//            )
//        {
//            Text("Proximos Eventos")
//        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
//        horizontalArrangement = Arrangement.spacedBy(32.dp), // Aumenta el espaciado entre los botones
        verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { navController.navigate(Rutas.historialEventos) },colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
        ) {
            Text(text = "Historial de eventos",
                textAlign = TextAlign.Center)

        }
    }

}