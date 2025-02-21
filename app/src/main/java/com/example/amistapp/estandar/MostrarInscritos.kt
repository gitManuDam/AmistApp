package com.example.amistapp.estandar

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.amistapp.Administrador.Eventos.EventoViewModel
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.Modelos.Evento
import com.example.amistapp.R
import com.example.amistapp.Rutas

// Autora: Izaskun

@Composable
fun MostrarInscritos(navController: NavHostController,
                     eventoVM: EventoViewModel
){
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val eventoId = eventoVM.eventoId.value

    // Recoge los inscritos al evento
    eventoVM.obtenerInscritos(eventoId)
    val inscritos by eventoVM.inscritos.collectAsState()

    // la primera vez muestra el toast aunque no esté vacia,
    LaunchedEffect(inscritos) {
        if (inscritos.isEmpty()) {
            Toast.makeText(context, "No hay inscritos en este evento", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .systemBarsPadding()
    ) {
        LazyColumn(state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {

            items(inscritos) { inscrito ->
                eventoItemInscritos(inscrito, eventoVM)
            }
        }

        botonVolverEventosInscritos(navController)

    }
}

@Composable
fun eventoItemInscritos(inscrito: String, eventoVM: EventoViewModel){

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = inscrito, fontSize = 15.sp, fontWeight = FontWeight.Bold)

        }
    }

}

@Composable
fun botonVolverEventosInscritos(navController: NavHostController){
    Button(
        onClick = {
//        eventoVM.limpiarDatos()
            navController.navigate(Rutas.eventosDisponibles)
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