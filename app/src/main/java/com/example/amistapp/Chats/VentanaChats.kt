package com.example.amistapp.Chats

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.R
import com.example.amistapp.Modelos.UsuarioChat
import com.example.amistapp.Parametros.Rutas
import com.example.amistapp.estandar.EstandarViewModel
import com.example.amistapp.estandar.MenuPuntosEstandar
//Manuel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentanaChats (navController: NavHostController, loginVM: LoginViewModel,
                  estandarVM: EstandarViewModel,
                  chatVM: ChatViewModel,
                  amigo: String?
){
    val context = LocalContext.current
    var emailAmigo by remember { mutableStateOf(amigo)  }
    if (emailAmigo == null) {
        emailAmigo=""
    }

    val emailUsuarioLogeado = loginVM.getCurrentUser()?.email
    estandarVM.obtenerFotoPerfil(emailUsuarioLogeado!!)

    Scaffold(
    topBar = {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(estandarVM.fotoPerfil.value),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Gray, CircleShape)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = emailUsuarioLogeado,
                        color = colorResource(R.color.texto),
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.botones),
                titleContentColor = colorResource(id = R.color.textoBotones)
            ),
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack() // Esto navega hacia atrás
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            actions = {
                MenuPuntosChat (navController, loginVM)


            }
        )
    },


    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ChatScreen(chatVM, navController, loginVM, amigo = emailAmigo!!)



        }
    }
}
@Composable
fun MenuPuntosChat(navController: NavHostController, loginVM: LoginViewModel) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menú de opciones")
        }

        DropdownMenu(expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(color = colorResource(id = R.color.fondoMenuPuntos))) {


            DropdownMenuItem(
                text = { Text("Cerrar sesión") },
                onClick = {
                    loginVM.signOut(context)
                    expanded = false
                    Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
                    navController.navigate(Rutas.login) {
                        popUpTo(Rutas.login) { inclusive = true }
                    }
                })
        }
    }
}

@Composable
fun BodyChats(navController: NavHostController, estandarVM:EstandarViewModel, chatVM: ChatViewModel ){
    LaunchedEffect(Unit) {
        chatVM.obtenerUsuariosChat()
        estandarVM.obtenerUsuarioActual()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Verificar si la lista está vacía
        if (chatVM.listadoUsuariosChat.isEmpty()) {
            // Si la lista está vacía, mostrar un mensaje
            Text(
                text = "No tienes chats disponibles.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            // Si la lista no está vacía, mostrar los chats
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chatVM.listadoUsuariosChat) { usuario ->
                    ChatItem(usuario, estandarVM) {
                        navController.navigate("chats/${it}")
                    }
                }
            }
        }
    }
}
@Composable
fun ChatItem(usuario: UsuarioChat,estandarVM: EstandarViewModel, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .clickable { onClick(usuario.usuario.email) },

        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de perfil con borde y sombra
            Image(
                painter = rememberImagePainter(usuario.usuario.perfil?.fotoPerfil),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)

            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Nombre del usuario con negrita
                Text(
                    text = usuario.usuario.perfil?.nick ?: "Sin nombre",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Último mensaje con estilo más ligero
                Text(
                    text = usuario.ultimoMensaje.text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if(!usuario.ultimoMensaje.leido && estandarVM.usuarioActual.value!!.email != usuario.ultimoMensaje.sender){
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Red, CircleShape)
                )
            }

        }
    }
}