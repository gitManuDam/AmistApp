package com.example.amistapp.estandar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.amistapp.Modelos.Usuario
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
                    AmigoItem(usuario) {
                        navController.navigate("chats/${it}")
                    }
                }
            }
        }
    }

}

@Composable
fun AmigoItem(usuario: Usuario, onClick: (String) -> Unit){
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
        }
    }
}
