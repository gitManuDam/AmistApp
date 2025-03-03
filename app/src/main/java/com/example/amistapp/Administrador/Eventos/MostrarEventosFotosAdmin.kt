package com.example.amistapp.Administrador.Eventos

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.amistapp.R

// Autora: Izaskun
// Muestra las fotos de un evento y permite eliminar imágenes .
@Composable
fun MostrarEventosFotosAdmin (
    eventoVM: EventoViewModel,
    navController: NavHostController,
    contexto: Context
){

    val eventoId = eventoVM.eventoId.value
    val fotos by eventoVM.fotos.collectAsState()

    eventoVM.obtenerFotosDelEvento(eventoId)
    Column () {
        // Mostrar las fotos en una LazyColumn
        Spacer(modifier = Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 columnas
            modifier = Modifier.fillMaxWidth()
        ) {
            items(fotos) { fotoUrl ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Carga la imagen desde el URL almacenado en Firebase Storage
                    AsyncImage(
                        model = fotoUrl, // URL de la foto de Firebase Storage
                        contentDescription = "Foto del evento",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp)
                    )

                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar imagen",
                        modifier = Modifier
                            .size(24.dp) // Tamaño del icono
                            .clickable {
                                    eventoVM.deleteFile(fotoUrl, contexto)
                                    eventoVM.deleteImageFromEvent(eventoId, fotoUrl, contexto)
                            },
                    )
                }
            }
        }
        botonVolverFotosEventosAdmin(navController)
    }

}
@Composable
fun botonVolverFotosEventosAdmin(navController: NavHostController){
    Button(
        onClick = {

            navController.popBackStack()

        },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
    )
    {
        Text(text = "Volver")
    }
}