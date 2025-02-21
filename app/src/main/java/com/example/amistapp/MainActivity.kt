package com.example.amistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.ui.theme.AmistAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.amistapp.Administrador.Usuarios.ActivarDesactivarUsuarios
import com.example.amistapp.Administrador.AdministradorViewModel

import com.example.amistapp.Administrador.Eventos.BodyVentanaAdminEventos
import com.example.amistapp.Administrador.Usuarios.AltaUsuarios
import com.example.amistapp.Administrador.Usuarios.BajaUsuarios
import com.example.amistapp.Administrador.Usuarios.CambiarRoleAdministrador
import com.example.amistapp.Administrador.Eventos.CrearEvento
import com.example.amistapp.Administrador.Eventos.EventoViewModel
import com.example.amistapp.Administrador.Eventos.GoogleMaps.MapsVentana
import com.example.amistapp.Administrador.Eventos.HistorialEventos
import com.example.amistapp.Administrador.Eventos.ProoximosEventos
import com.example.amistapp.Administrador.Usuarios.BodyVentanaAdminUsuarios
import com.example.amistapp.Administrador.VentanaAdministrador
import com.example.amistapp.DatosPerfil.DatosPerfilViewModel
import com.example.amistapp.DatosPerfil.VentanaDatosPerfil
import com.example.amistapp.Login.LoginScreen
import com.example.amistapp.Login.NoEstasActivado
import com.example.amistapp.estandar.BodyVentanaEventosEstandar
import com.example.amistapp.estandar.EstandarViewModel
import com.example.amistapp.estandar.EventosDisponiblesEstandar
import com.example.amistapp.estandar.MisEventos
import com.example.amistapp.estandar.MostrarInscritos
import com.example.amistapp.estandar.VentanaEstandar


class MainActivity : ComponentActivity() {
    val loginVM = LoginViewModel()
    val datosPerfilVM = DatosPerfilViewModel()
    val administradorVM = AdministradorViewModel()
    val eventoVM = EventoViewModel()
    val estandarVM = EstandarViewModel()
    val eventoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val contexto = LocalContext.current

            AmistAppTheme {
                val navController = rememberNavController()
                //Durante la creacion de la ventana proximos evento
//                NavHost(navController = navController, startDestination = Rutas.historialEventos){
                NavHost(navController = navController, startDestination = Rutas.login){
                    composable(Rutas.login){
                        LoginScreen(navController, loginVM, datosPerfilVM)
                    }
                    composable(Rutas.estandar){
                        VentanaEstandar(navController,datosPerfilVM, loginVM, estandarVM)
                    }
                    composable(Rutas.administrador){
                        VentanaAdministrador(navController, datosPerfilVM,eventoVM, loginVM)
//                        Text(text = "Pantalla administrador", fontSize = 24.sp, modifier = Modifier.fillMaxSize())
                    }
                    composable(Rutas.perfil){
                        VentanaDatosPerfil(navController, loginVM, datosPerfilVM, contexto)
                    }
                    composable(Rutas.noActivado) {
                        NoEstasActivado(navController,loginVM,contexto)
                    }
                    composable(Rutas.altaUsuario) {
                        AltaUsuarios(navController,administradorVM,loginVM)
                    }
                    composable(Rutas.bajaUsuarios){
                        BajaUsuarios(administradorVM, loginVM, navController)
                    }
                    composable(Rutas.activarDesActivar) {
                        ActivarDesactivarUsuarios(administradorVM, loginVM, navController)
                    }
                    composable(Rutas.cambiarRole){
                        CambiarRoleAdministrador(administradorVM, loginVM, navController)
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
                    composable(Rutas.eventosDisponibles){
                        EventosDisponiblesEstandar(navController,eventoVM, loginVM)
                    }
                    composable(Rutas.misEventos) {
                        MisEventos(navController,estandarVM,loginVM,eventoVM)
                    }
                    composable(Rutas.bodyVentanaEstandarEventos) {
                        BodyVentanaEventosEstandar(navController)
                    }
                    composable(Rutas.bodyVentanaAdminEventos){
                        BodyVentanaAdminEventos(navController)
                    }
                    composable(Rutas.bodyVentanaAdminUsuarios){
                        BodyVentanaAdminUsuarios(navController)
                    }
                    composable(Rutas.mostrarInscritos){
                        MostrarInscritos(navController,eventoVM)
                    }
                }
            }
        }
    }
}
