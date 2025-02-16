package com.example.amistapp.Administrador.Eventos

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.amistapp.Evento
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.amistapp.R
import com.example.amistapp.Rutas

@Composable
fun ProoximosEventos(navController: NavHostController, eventoVM: EventoViewModel) {

    // Recoge los proximos eventos desde ViewModel
    val eventos by eventoVM.proximosEventos.collectAsState()

    // para el desplamiento de los eventos
    val listState = rememberLazyListState()
    Column(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .systemBarsPadding()
    ) {
//        Spacer(modifier = Modifier.height(50.dp))
        //botonSoyBarman()


        // LazyColumn para mostrar las comandas, con el estado de desplazamiento
        LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(4.dp)) {

            items(eventos) { evento ->
                eventoItem(evento, eventoVM)
            }
        }

        botonVolver(navController)

    }
}

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun eventoItem(evento: Evento, eventoVM: EventoViewModel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            val latitud = evento.latitud
            val longitud = evento.longitud
            val direccion= eventoVM.getDireccion(latitud!!, longitud!!)

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = evento.descripcion, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = "Fecha: ${evento.fecha} - Hora: ${evento.hora}", fontSize = 15.sp)
                Text(text = "Ubicación: ${direccion}", fontSize = 15.sp)
                Text(text = "Plazo inscripción: ${evento.plazoInscripcion}", fontSize = 15.sp)
                Text(text = "Inscritos: ${evento.inscritos.size}", fontSize = 15.sp)
            }

        }
    }

@Composable
fun botonVolver(navController: NavHostController)
{
    Button(onClick = {
//        eventoVM.limpiarDatos()
        navController.navigate(Rutas.administrador)},
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
    )
    {
        Text(text = "Volver")
    }

}
