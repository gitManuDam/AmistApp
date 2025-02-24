package com.example.amistapp.estandar

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.amistapp.Administrador.BottomNavigationBar
import com.example.amistapp.Administrador.Eventos.BodyVentanAdminEventos
import com.example.amistapp.Administrador.MenuPuntos
import com.example.amistapp.Administrador.Usuarios.BodyVentanaAdminUsuarios
import com.example.amistapp.DatosPerfil.DatosPerfilViewModel
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentanaEstandar(navController: NavHostController, datosPerfilVM: DatosPerfilViewModel, loginVM: LoginViewModel, estandarVM: EstandarViewModel){
    val context = LocalContext.current
    var currentRoute by remember { mutableStateOf("Amigos") }

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
                actions = {
                    MenuPuntosEstandar()
                }
            )
        },
        bottomBar = {
            BottomNavigationBarEstandar(navController, currentRoute){nuevaRuta ->
                currentRoute = nuevaRuta
            }
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentRoute == "Amigos") BodyVentanaAmigos(navController, estandarVM)
            else if (currentRoute == "Compatibles")BodyVentanaCompatibles(navController, estandarVM)
            else BodyVentanaEventosEstandar(navController)


        }
    }
}

@Composable
fun MenuPuntosEstandar(){
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
                text = { Text("Perfil") },
                onClick = {
                    Toast.makeText(context, "Perfil", Toast.LENGTH_SHORT).show()
                    expanded = false
                })

            DropdownMenuItem(
                text = { Text("Cerrar sesión") },
                onClick = {
                    Toast.makeText(context, "Cerrar sesión", Toast.LENGTH_SHORT).show()
                    expanded = false
                })
        }
    }
}
@Composable
fun BottomNavigationBarEstandar(navController: NavController, currentRoute: String, onNuevaRuta: (String) -> Unit) {
    BottomNavigation (
        modifier = Modifier,
        backgroundColor = colorResource(id = R.color.botones), ){

        BottomNavigationItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Amigos") },
            label = { Text("Amigos") },
            selected = currentRoute == "Amigos",
            onClick = { onNuevaRuta("Amigos") }

        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Eventos") },
            label = { Text("Eventos") },
            selected = currentRoute == "Eventos",
            onClick = { onNuevaRuta("Eventos") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Accessibility, contentDescription = "Compatibles") },
            label = { Text("Compatibles") },
            selected = currentRoute == "Compatibles",
            onClick = { onNuevaRuta("Compatibles") }
        )

    }
}