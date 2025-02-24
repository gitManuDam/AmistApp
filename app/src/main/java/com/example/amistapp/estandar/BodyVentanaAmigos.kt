package com.example.amistapp.estandar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.amistapp.Usuario

@Composable
fun BodyVentanaAmigos(navController: NavHostController, estandarVM:EstandarViewModel){
    estandarVM.obtenerAmigos()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(estandarVM.listadoAmigos) { usuario ->
                AmigoItem ( usuario) {

                }
            }
        }
    }
}

@Composable
fun AmigoItem(usuario: Usuario, onClick: () -> Unit){
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
                painter = rememberImagePainter(usuario.perfil?.fotoPerfil),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(50.dp)
                    .aspectRatio(1f)
                    .padding(end = 8.dp)
            )

            Text(text = usuario.perfil?.nick ?: "Sin nombre", fontSize = 18.sp)




        }
    }
}