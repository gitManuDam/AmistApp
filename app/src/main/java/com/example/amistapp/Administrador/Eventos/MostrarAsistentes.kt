package com.example.amistapp.Administrador.Eventos

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.amistapp.Modelos.AsistenteEvento
import com.example.amistapp.R
import com.example.amistapp.Parametros.Rutas

// Autora: Izaskun
// Muestra un listado de los asistentes a un evento determinado
@Composable
fun MostrarAsistentes(navController: NavHostController,
                      eventoVM: EventoViewModel
){
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val eventoId = eventoVM.eventoId.value

    // Recoge los asistentes al evento
    eventoVM.obtenerAsistentes(eventoId)
    val asistentes by eventoVM.asistentes.collectAsState()

    Log.e("Izaskun", "Asistentes en la UI: $asistentes")

    Column(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .systemBarsPadding()
    ) {
        LazyColumn(state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {

            items(asistentes) { asistente ->
                eventoItemAsistentes(asistente, eventoVM)
            }
        }

        botonVolverMostrarAsistentes(navController)

    }
}

@Composable
fun eventoItemAsistentes(asistente: AsistenteEvento, eventoVM: EventoViewModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Email: ${asistente.email}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = "Hora: ${asistente.hora}", fontSize = 14.sp)
        }
    }
}

@Composable
fun botonVolverMostrarAsistentes(navController: NavHostController){
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