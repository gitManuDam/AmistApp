package com.example.amistapp.estandar

import android.content.Context
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.amistapp.Administrador.Eventos.EventoViewModel
import com.example.amistapp.Modelos.Evento
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.R
import com.example.amistapp.Parametros.Rutas
// Autora: Izaskun
@Composable

// Eventos disponibles muestra aquellos eventos a los que todavia se puede inscribir
// Si ya está inscrito, también puede borrarse del evento
// También puede ver quienes están inscritos al evento

fun EventosDisponiblesEstandar(
    navController: NavHostController,
    eventoVM: EventoViewModel,
    loginVM: LoginViewModel
){

    val emailLogeado = loginVM.getCurrentUser()?.email

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
        ) {

            items(eventos) { evento ->
                eventoItemDisponible(evento, eventoVM, emailLogeado, navController)
            }
        }

        botonVolverEventosDisponibles(navController)

    }
}

@Composable
fun eventoItemDisponible(
    evento: Evento,
    eventoVM: EventoViewModel,
    emailLogeado: String?,
    navController: NavHostController
) {

    // para saber si el usuario está inscrito
    var estaInscrito by remember { mutableStateOf(false) }

    // llama a la función `estaInscrito` que comprueba si el usuario está inscrito en este evento
    eventoVM.estaInscrito(evento.id!!, emailLogeado!!) { inscrito ->
        estaInscrito = inscrito // Actualizamos el estado local con el resultado
    }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mostrarDialogoBorrarse by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

            Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly){
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Inscribirse al evento",
                    modifier = Modifier
                        .size(24.dp) // Tamaño del icono
                        // si no está inscrito habilita el icono
                        .clickable(enabled = !estaInscrito) {
                            mostrarDialogo = true // muestra el dialogo de confirmacion
                        } // Acción al hacer clic
                        .graphicsLayer(
                            alpha = if (estaInscrito) 0.4f else 1f
                        )

                )

                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Borrarse del evento",
                    modifier = Modifier
                        .size(24.dp) // Tamaño del icono
                        // si está inscrito habilita el icono
                        .clickable(enabled = estaInscrito) {
                            mostrarDialogoBorrarse = true // muestra el dialogo de confirmacion
                        } // Acción al hacer clic
                        .graphicsLayer(
                            alpha = if (!estaInscrito) 0.4f else 1f
                        )

                )

                Icon(
                    imageVector = Icons.Filled.Attribution,
                    contentDescription = "Inscritos al eventos",
                    modifier = Modifier
                        .size(24.dp) // Tamaño del icono
                        .clickable() {
                            eventoVM.setEventoId(evento.id!!)
                            navController.navigate(Rutas.mostrarInscritos)
                        }
                )
            }
        }
    }
    if(mostrarDialogo){
        confirmacionInscribirseEvento(eventoVM, evento, emailLogeado, context) {
            mostrarDialogo = false
        }

    }
    if(mostrarDialogoBorrarse){
        confirmacionBorrarseEvento(eventoVM, evento, emailLogeado, context) {
            mostrarDialogoBorrarse = false
        }

    }

}
@Composable
fun confirmacionInscribirseEvento(eventoVM: EventoViewModel, evento: Evento, emailLogeado: String?, context: Context, onDismiss:() -> Unit) {

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
                    text = "Vas a inscribirte a  ${evento.descripcion}",
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
                            eventoVM.incribirseEnEvento(evento.id!! ,emailLogeado!! , context)
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
fun confirmacionBorrarseEvento(eventoVM: EventoViewModel, evento: Evento, emailLogeado: String?, context: Context, onDismiss:() -> Unit) {

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
                    text = "Vas a borrarte de  ${evento.descripcion}",
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
                            eventoVM.borrarseEnEvento(evento.id!! ,emailLogeado!! , context)
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
fun botonVolverEventosDisponibles(navController: NavHostController) {
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