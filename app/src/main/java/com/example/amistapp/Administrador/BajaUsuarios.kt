package com.example.amistapp.Administrador

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.amistapp.Usuario

@Composable
fun BajaUsuarios(administradorVM: AdministradorViewModel){

    administradorVM.obtenerUsuarios()
    val listadoUsers = administradorVM.listadoUsuarios

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(listadoUsers){ usuario ->
            UsuarioItem(usuario = usuario, administradorVM)
        }
    }

}
@Composable
fun UsuarioItem(usuario: Usuario, administradorVM: AdministradorViewModel){

    var mostrarDialogo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)

    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Imagen de perfil
            AsyncImage(
                model = usuario.perfil?.fotoPerfil,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column (modifier = Modifier.padding(16.dp))
            {
                Text(text = "Email: ${usuario.email}")
                Text(text = "Role: ${usuario.role}")
                Text(text = "Edad: ${usuario.perfil?.edad}")

            }
            Icon(
                imageVector = Icons.Filled.Delete, // Icono papelera
                contentDescription = "Eliminar usuario",
                modifier = Modifier
                    .size(24.dp) // Tamaño del icono
                    .clickable {
                        mostrarDialogo = true // muestra el dialogo de confirmacion
                               }, // Acción al hacer clic

            )
        }
    }

    if (mostrarDialogo){
        confirmacionEliminar(
            administradorVM = administradorVM,
            email = usuario.email,
            onConfirmacion = {
                administradorVM.eliminarUsuarioPorEmail(usuario.email)
                mostrarDialogo = false
            },
            onCancelacion = {
                mostrarDialogo = false
            }
        )
    }
}

@Composable
fun confirmacionEliminar(administradorVM: AdministradorViewModel, email:String,
                         onConfirmacion: () -> Unit,
                         onCancelacion: () -> Unit
){
    AlertDialog(
        onDismissRequest = { onCancelacion() },
        text = {
            Text(text = "¿Deseas eliminar al usuario con el email: $email?")
        },
        confirmButton = {
            TextButton(onClick = {
                administradorVM.eliminarUsuarioPorEmail(email) // Llama a la función para eliminar al usuario
            }) {
                Text("Sí", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onCancelacion() // Cierra el diálogo sin hacer nada
            }) {
                Text("No", color = Color.Gray)
            }
        }
    )
}