package com.example.amistapp.estandar

import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.amistapp.Modelos.Usuario
import com.example.amistapp.Parametros.Rutas
import com.example.amistapp.R

//Manuel
@Composable
fun BodyVentanaAmigos(navController: NavHostController, estandarVM:EstandarViewModel){
    LaunchedEffect(Unit) {
        estandarVM.obtenerAmigos()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Verificar si la lista de amigos está vacía
        if (estandarVM.listadoAmigos.isEmpty()) {
            // Si la lista está vacía, mostrar un mensaje
            Text(
                text = "No tienes amigos disponibles.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            // Si la lista no está vacía, mostrar los amigos
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(estandarVM.listadoAmigos) { usuario ->
                    AmigoItem(usuario, estandarVM) {
                        navController.navigate("chats/${it}")
                    }
                }
            }
        }
    }

}

@Composable
fun AmigoItem(usuario: Usuario, estandarVM:EstandarViewModel, onClick: (String) -> Unit){
    var mostrarDialogo by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(usuario.email) },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(usuario.perfil?.fotoPerfil),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(50.dp)
                    .aspectRatio(1f)
                    .padding(end = 8.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = usuario.perfil?.nick ?: "Sin nombre", fontSize = 18.sp)

                // Mostrar el estado basado en el atributo enLinea
                Text(
                    text = if (usuario.enLinea) "En línea" else "Desconectado",
                    fontSize = 14.sp,
                    color = if (usuario.enLinea) Color.Green else Color.Gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = "Subir fotos",
                modifier = Modifier
                    .size(24.dp) // Tamaño del icono
                    .clickable {
                        mostrarDialogo = true // muestra el dialogo de confirmacion
                    } // Acción al hacer clik

            )
            if (mostrarDialogo) {
                DialogoEliminarAmigo(
                    onEliminar = {
                        mostrarDialogo = false
                        Log.d("Eliminar amigo", "Eliminando a ${usuario.email}")
                        estandarVM.eliminarAmigos(usuario.email, estandarVM.usuarioActual.value!!.email)
                        },
                    onDismiss = { mostrarDialogo = false }
                )
            }


        }
    }
}
@Composable
fun  DialogoEliminarAmigo( onEliminar:() -> Unit,onDismiss:() -> Unit){
    var mostrar by remember { mutableStateOf(true)  }
    if (mostrar) {
        Dialog(onDismissRequest = { mostrar=false },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = true)
        ){

            Column(modifier = Modifier
                .width(350.dp)
                .padding(20.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Estas seguro de eliminar a este amigo",
                    color = colorResource(R.color.texto),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        mostrar = false
                        onEliminar()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.botones), // Color de fondo del botón
                        contentColor = colorResource(R.color.textoBotones) // Color del texto
                    )
                ) {
                    Text("Si")
                }
                Spacer(modifier = Modifier.height(8.dp))



                Button(
                    onClick = {
                        mostrar = false
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.botones), // Color de fondo del botón
                        contentColor = colorResource(R.color.textoBotones) // Color del texto
                    )
                ) {
                    Text("No")
                }
            }
//            }
        }
    }
}
