package com.example.amistapp.estandar

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.amistapp.Modelos.Peticion

@Composable
fun BodyPeticiones(navController: NavHostController, estandarVM: EstandarViewModel) {
    estandarVM.obtenerPeticionesRecibidas()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Verificar si la lista de peticiones está vacía
        if (estandarVM.listadoPeticiones.isEmpty()) {
            // Si la lista está vacía, mostrar un mensaje
            Text(
                text = "No tienes peticiones disponibles.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            // Si la lista no está vacía, mostrar las peticiones
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(estandarVM.listadoPeticiones) { peticion ->
                    PeticionCard(
                        peticion,
                        onAceptar = {
                            Log.d("Peticion", "Solicitud aceptada: ${peticion.emisor} -> ${peticion.receptor}")
                            estandarVM.aceptarPeticion(peticion)
                            estandarVM.obtenerPeticionesRecibidas()
                        },
                        onRechazar = {
                            estandarVM.rechazarPeticion(peticion)
                            Log.d("Peticion", "Solicitud rechazada: ${peticion.emisor} -> ${peticion.receptor}")
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun PeticionCard(peticion: Peticion, onAceptar: (Peticion) -> Unit, onRechazar: (Peticion) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Solicitud de: ${peticion.emisor}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "Para: ${peticion.receptor}", fontSize = 16.sp, color = Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        onAceptar(peticion)
                    },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(Color.Green)
                ) {
                    Text(text = "Aceptar", color = Color.White)
                }
                Button(
                    onClick = {
                        onRechazar(peticion)
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text(text = "Rechazar", color = Color.White)
                }
            }
        }
    }
}