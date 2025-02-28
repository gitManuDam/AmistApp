package com.example.amistapp.Administrador.Eventos

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.amistapp.Modelos.Evento
import com.example.amistapp.Parametros.Rutas
import com.example.amistapp.R

@Composable
fun MostrarInscritosAdmin(navController: NavHostController,
                          eventoVM: EventoViewModel){

    val context = LocalContext.current
    val listState = rememberLazyListState()

    val eventoId = eventoVM.eventoId.value
    Log.e("Izaskun", "en mostrar evento el idEvento es del que obtiene los inscritos: $eventoId")
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
                eventoItemInscritosA(inscrito, eventoVM)
            }
        }

        botonVolverInscritosAdmin(navController)

    }
}

@Composable
fun eventoItemInscritosA(inscrito: String, eventoVM: EventoViewModel){
    var mostrarDialogo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = inscrito, fontSize = 15.sp, fontWeight = FontWeight.Bold)

            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Eliminar inscrito",
                modifier = Modifier
                    .size(24.dp) // Tamaño del icono
                    .clickable (){
                        mostrarDialogo = true // muestra el dialogo de confirmacion
                    }, // Acción al hacer clic
            )

        }
        if(mostrarDialogo){
            confirmacionEliminarInscritoAdmin(eventoVM,inscrito) {
                mostrarDialogo = false
            }

        }

    }

}

@Composable
fun confirmacionEliminarInscritoAdmin(eventoVM: EventoViewModel, inscrito: String, onDismiss:() -> Unit) {

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
                    text = "Va a eliminar a  ${inscrito} de la lista de inscritos al evento",
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
                            eventoVM.eliminarInscrito(inscrito)

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
fun botonVolverInscritosAdmin(navController: NavHostController){
    Button(
        onClick = {
//        eventoVM.limpiarDatos()
            navController.navigate(Rutas.proximosEventos)
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

