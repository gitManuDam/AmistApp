package com.example.amistapp.estandar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.amistapp.Administrador.Eventos.EventoViewModel
import com.example.amistapp.Modelos.Evento
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.R
import com.example.amistapp.Rutas

@Composable
fun MisEventos(
    navController: NavHostController,
    estandarVM: EstandarViewModel,
    loginVM: LoginViewModel,
    eventoVM: EventoViewModel
    ){
        val emailLogeado = loginVM.getCurrentUser()?.email

        estandarVM.obtenerMisEventos(emailLogeado!!)

        // Recoge los proximos eventos desde ViewModel
        val misEventos by estandarVM.misEventos.collectAsState()

        // para el desplamiento de los eventos
        val listState = rememberLazyListState()
        Column(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .systemBarsPadding()
        ) {
            LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(4.dp)) {

                items(misEventos) { evento ->
                    eventoItemMisEventos(evento, eventoVM)
                }
            }

            botonVolverMisEventos(navController)

        }
    }

@Composable
fun eventoItemMisEventos(evento: Evento, eventoVM: EventoViewModel) {
    var mostrarDialogo by remember { mutableStateOf(false) }
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
            Icon(
                imageVector = Icons.Filled.AddLocationAlt,
                contentDescription = "He llegado",
                modifier = Modifier
                    .size(24.dp) // Tamaño del icono
                    .clickable (){
                        mostrarDialogo = true // muestra el dialogo de confirmacion
                    }, // Acción al hacer clic
            )
        }
    }
}

@Composable
fun botonVolverMisEventos(navController: NavHostController) {
    Button(
        onClick = {
//        eventoVM.limpiarDatos()
            navController.navigate(Rutas.estandar)
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