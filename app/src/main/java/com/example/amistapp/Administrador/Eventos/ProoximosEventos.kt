package com.example.amistapp.Administrador.Eventos

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
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
import com.example.amistapp.Modelos.Evento
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.amistapp.R
import com.example.amistapp.Rutas
// Autora: Izaskun
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

        LazyColumn(state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
            )
        {

            items(eventos) { evento ->
                eventoItem(evento, eventoVM)
            }
        }

        botonVolverProximos(navController)
    }
}

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun eventoItem(evento: Evento, eventoVM: EventoViewModel) {
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
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar Evento",
                    modifier = Modifier
                        .size(24.dp) // Tamaño del icono
                        .clickable (){
                            mostrarDialogo = true // muestra el dialogo de confirmacion
                        }, // Acción al hacer clic
                )
            }
        }
        if(mostrarDialogo){
            confirmacionEliminarEvento(eventoVM, evento) {
                mostrarDialogo = false
            }

        }
    }

@Composable
fun confirmacionEliminarEvento(eventoVM: EventoViewModel, evento: Evento, onDismiss:() -> Unit) {

    var mostrar by remember { mutableStateOf(true) }
    if (mostrar) {
        Dialog(
            onDismissRequest = { mostrar = false },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = true)
        ) {
            Column(
                modifier = Modifier
                    .width(350.dp)
                    .padding(20.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Va a eliminar a  ${evento.descripcion}",
                    color = colorResource(R.color.texto),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    Button(
                        onClick = {
                            mostrar = false
                           eventoVM.eliminarEvento(evento)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.botones), // Color de fondo del botón
                            contentColor = colorResource(R.color.textoBotones) // Color del texto
                        )
                    ) {
                        Text("Aceptar")
                    }

                    Button(
                        onClick = {
                            mostrar = false
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.botones), // Color de fondo del botón
                            contentColor = colorResource(R.color.textoBotones) // Color del texto
                        )
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}
    @Composable
    fun botonVolverProximos(navController: NavHostController ) {
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

