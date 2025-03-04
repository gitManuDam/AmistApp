package com.example.amistapp.estandar

import android.content.Context
import android.util.Log
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Image
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
// Muestra los eventos a los que el usuario está inscrito
// Para cada evento puede:
// He llegado: para indicar que ha llegado al evento (según su ubicación)
// subir foto: subir una foto de ese evento
// Ver fotos: ver las fotos de ese evento
@Composable
fun MisEventos(
    navController: NavHostController,
    estandarVM: EstandarViewModel,
    loginVM: LoginViewModel,
    eventoVM: EventoViewModel,
    contexto: Context,

){
        val emailLogeado = loginVM.getCurrentUser()?.email
        estandarVM.setEmailLogeado(emailLogeado!!)

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
            LazyColumn(state = listState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
                ) {

                items(misEventos) { evento ->
                    eventoItemMisEventos(evento, eventoVM, contexto, navController, estandarVM, emailLogeado)
                }
            }

            botonVolverMisEventos(navController)

        }
    }

@Composable
fun eventoItemMisEventos(
    evento: Evento,
    eventoVM: EventoViewModel,

    contexto: Context,
    navController: NavHostController,
    estandarVM: EstandarViewModel,
    emailLogeado: String
) {
    // para saber si el usuario asistió al evento
    var asistio by remember { mutableStateOf(false) }

    // llama a la función `asistio` que comprueba si el usuario asistió a este evento
    eventoVM.asistio(evento.id!!, emailLogeado!!) { asistente ->
        asistio = asistente
    }



    var mostrarDialogo by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        val latitudEvento = evento.latitud
        val longitudEvento = evento.longitud

        val direccion= eventoVM.getDireccion(latitudEvento!!, longitudEvento!!)

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = evento.descripcion, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = "Fecha: ${evento.fecha} - Hora: ${evento.hora}", fontSize = 15.sp)
            Text(text = "Ubicación: ${direccion}", fontSize = 15.sp)
            Text(text = "Plazo inscripción: ${evento.plazoInscripcion}", fontSize = 15.sp)
            Text(text = "Inscritos: ${evento.inscritos.size}", fontSize = 15.sp)
            Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly){
            Icon(
                imageVector = Icons.Filled.AddLocationAlt,
                contentDescription = "He llegado",
                modifier = Modifier
                    .size(24.dp) // Tamaño del icono
                    .clickable (){
                        mostrarDialogo = true // muestra el dialogo de confirmacion
                    } // Acción al hacer clic
            )

            Icon(
                imageVector = Icons.Filled.AddAPhoto,
                contentDescription = "Subir fotos",
                modifier = Modifier
                    .size(24.dp) // Tamaño del icono
                    .clickable (enabled = asistio){
                        eventoVM.setEventoId(evento.id!!)
                        navController.navigate(Rutas.subirFotosEventos)
                    } // Acción al hacer clik
                    .graphicsLayer(
                        alpha = if (!asistio) 0.4f else 1f
                    )
            )

            Icon(
                imageVector = Icons.Filled.Image,
                contentDescription = "Ver fotos",
                modifier = Modifier
                    .size(24.dp) // Tamaño del icono
                    .clickable (enabled = asistio){
                        eventoVM.setEventoId(evento.id!!)
                        navController.navigate(Rutas.mostrarFotosEventos)
                    } // Acción al hacer clic
                    .graphicsLayer(
                        alpha = if (!asistio) 0.4f else 1f
                    )
            )}


        }
    }
    if(mostrarDialogo){
        confirmacionAsistirEvento(eventoVM, evento,  contexto, navController, estandarVM) {
            mostrarDialogo = false
        }

    }
}

@Composable
fun confirmacionAsistirEvento(
    eventoVM: EventoViewModel,
    evento: Evento,

    context: Context,
    navController: NavHostController,
    estandarVM: EstandarViewModel,
    onDismiss: () -> Unit
) {

        val latitudEvento = evento.latitud
        val longitudEvento = evento.longitud
        // Ponemos los valores en VM para luego compararlos con los datos
        // de la ubicación del usuario
        Log.e("DEBUG", "Latitud evento antes de asignar al ViewModel: $latitudEvento")
        Log.e("DEBUG", "Longitud evento antes de asignar al ViewModel: $longitudEvento")
        eventoVM.setEventoId(evento.id!!)
        eventoVM.setLatitud(latitudEvento!!)
        eventoVM.setLongitud(longitudEvento!!)


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
                    text = "Vas a asistir a  ${evento.descripcion}",
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
                            navController.navigate(Rutas.mapaUbicacionUsuario)



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