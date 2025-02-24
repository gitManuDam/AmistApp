package com.example.amistapp.estandar

import android.service.autofill.OnClickAction
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.amistapp.UsuarioCompatibilidad

@Composable
fun BodyVentanaCompatibles(navController: NavHostController, estandarVM: EstandarViewModel) {
    estandarVM.obtenerCompatibles()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(estandarVM.listadoCompatibles) { usuarioCompatibilidad ->
                UsuarioItem(usuarioCompatibilidad ,estandarVM) {

                }
            }
        }
    }
}




@Composable
fun UsuarioItem(usuarioCompatibilidad: UsuarioCompatibilidad, estandarVM: EstandarViewModel, onClick: () -> Unit) {
    var icon by remember { mutableStateOf(Icons.Default.FavoriteBorder) }

    // Verificar si ya hay una petición de amistad
    estandarVM.buscarPeticionEntreUsuarios(
        estandarVM.usuarioActual.value!!.email,
        usuarioCompatibilidad.usuario.email
    ) { peticionEnviada ->
        icon = if (peticionEnviada) {
            Icons.Default.Favorite
        } else {
            Icons.Default.FavoriteBorder
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(usuarioCompatibilidad.usuario.perfil?.fotoPerfil),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(50.dp)
                    .aspectRatio(1f)
                    .padding(end = 8.dp)
            )

            Text(text = usuarioCompatibilidad.usuario.perfil?.nick ?: "Sin nombre", fontSize = 18.sp)

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {
                    if(icon == Icons.Default.FavoriteBorder){
                        estandarVM.enviarPeticion(
                            emisor=estandarVM.usuarioActual.value!!.email,
                            receptor= usuarioCompatibilidad.usuario.email
                        )
                        icon = Icons.Default.Favorite
                    }
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Solicitud de amistad"
                )
            }
        }
    }
}

