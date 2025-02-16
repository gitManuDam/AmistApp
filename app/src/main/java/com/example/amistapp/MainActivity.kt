package com.example.amistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.ui.theme.AmistAppTheme
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.amistapp.Administrador.Usuarios.ActivarDesactivarUsuarios
import com.example.amistapp.Administrador.AdministradorViewModel
import com.example.amistapp.Administrador.Usuarios.AltaUsuarios
import com.example.amistapp.Administrador.Usuarios.BajaUsuarios
import com.example.amistapp.Administrador.CambiarRoleAdministrador
import com.example.amistapp.Administrador.Eventos.CrearEvento
import com.example.amistapp.Administrador.Eventos.EventoViewModel
import com.example.amistapp.Administrador.Eventos.GoogleMaps.MapsVentana
import com.example.amistapp.Administrador.Eventos.HistorialEventos
import com.example.amistapp.Administrador.Eventos.ProoximosEventos
import com.example.amistapp.Administrador.VentanaAdministrador
import com.example.amistapp.DatosPerfil.DatosPerfilViewModel
import com.example.amistapp.DatosPerfil.VentanaDatosPerfil
import com.example.amistapp.Login.LoginScreen
import com.example.amistapp.Login.NoEstasActivado



class MainActivity : ComponentActivity() {
    val loginVM = LoginViewModel()
    val datosPerfilVM = DatosPerfilViewModel()
    val administradorVM = AdministradorViewModel()
    val eventoVM = EventoViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val contexto = LocalContext.current

            AmistAppTheme {
                val navController = rememberNavController()
                //Durante la creacion de la ventana proximos evento
                NavHost(navController = navController, startDestination = Rutas.proximosEventos){
//                NavHost(navController = navController, startDestination = Rutas.login){
                    composable(Rutas.login){
                        LoginScreen(navController, loginVM, datosPerfilVM)
                    }
                    composable(Rutas.estandar){
                        Text(text = "Pantalla estándar", fontSize = 24.sp, modifier = Modifier.fillMaxSize())
                    }
                    composable(Rutas.administrador){
                        VentanaAdministrador(navController, datosPerfilVM)
//                        Text(text = "Pantalla administrador", fontSize = 24.sp, modifier = Modifier.fillMaxSize())
                    }
                    composable(Rutas.perfil){
                        VentanaDatosPerfil(navController, loginVM, datosPerfilVM, contexto)
                    }
                    composable(Rutas.noActivado) {
                        NoEstasActivado()
                    }
                    composable(Rutas.altaUsuario) {
                        AltaUsuarios(navController,administradorVM,loginVM)
                    }
                    composable(Rutas.bajaUsuarios){
                        BajaUsuarios(administradorVM, loginVM)
                    }
                    composable(Rutas.activarDesActivar) {
                        ActivarDesactivarUsuarios(administradorVM, loginVM)
                    }
                    composable(Rutas.cambiarRole){
                        CambiarRoleAdministrador(administradorVM, loginVM)
                    }
                    composable(Rutas.mapa){
                        MapsVentana(navController, eventoVM)
                    }
                    composable(Rutas.crearEvento){
                        CrearEvento(navController,eventoVM)
                    }
                    composable(Rutas.proximosEventos){
                        ProoximosEventos(navController,eventoVM)
                    }
                    composable(Rutas.historialEventos){
                        HistorialEventos(navController,eventoVM)
                    }
                }
            }
        }
    }
}
