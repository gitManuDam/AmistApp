package com.example.amistapp.Administrador.Usuarios

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.amistapp.Administrador.AdministradorViewModel
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.R
import com.example.amistapp.Modelos.Usuario

@Composable
fun BajaUsuarios(administradorVM: AdministradorViewModel, loginVM: LoginViewModel){

    administradorVM.obtenerUsuarios()
    val listadoUsers = administradorVM.listadoUsuarios

    val emailLogeado = loginVM.getCurrentUser()?.email

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(listadoUsers){ usuario ->
            UsuarioItem(usuario = usuario, administradorVM, emailLogeado)
        }
    }

}
@Composable
fun UsuarioItem(usuario: Usuario, administradorVM: AdministradorViewModel, emailLogeado: String?){

    var mostrarDialogo by remember { mutableStateOf(false) }

    val esElMismoUsuario = usuario.email == emailLogeado

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
                Text(text = "Roles: ")
                usuario.role.forEach { rol ->
                    Text(text = "- $rol")
                }
                Text(text = "Edad: ${usuario.perfil?.edad}")

            }
            Icon(
                imageVector = Icons.Filled.Delete, // Icono papelera
                contentDescription = "Eliminar usuario",
                modifier = Modifier
                    .size(24.dp) // Tamaño del icono
                    .alpha(if (esElMismoUsuario) 0.3f else 1f)
                    .clickable (enabled = !esElMismoUsuario){
                        mostrarDialogo = true // muestra el dialogo de confirmacion
                               }, // Acción al hacer clic

            )
        }
    }

    if(mostrarDialogo){
        confirmacionEliminar(administradorVM, usuario.email) {
            mostrarDialogo = false
        }

    }
}

@Composable
fun confirmacionEliminar(administradorVM: AdministradorViewModel, email:String, onDismiss:() -> Unit) {

    var mostrar by remember { mutableStateOf(true) }
    if (mostrar) {
        Dialog(
            onDismissRequest = { mostrar = false },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = true)
        ){
            Column(modifier = Modifier
                .width(350.dp)
                .padding(20.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Va a eliminar a  $email",
                    color = colorResource(R.color.texto),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row ( modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) )
                {
                Button(
                    onClick = {
                        mostrar = false
                        administradorVM.eliminarUsuarioPorEmail(email) // Llama a la función para eliminar al usuario
                    },
                    modifier =  Modifier.weight(1f),
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
                    modifier =  Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.botones), // Color de fondo del botón
                        contentColor = colorResource(R.color.textoBotones) // Color del texto
                    )
                ) {
                    Text("Cancelar")
                }
            }}
        }
    }
}