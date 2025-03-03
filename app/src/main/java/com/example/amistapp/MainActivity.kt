package com.example.amistapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.example.amistapp.Chats.ChatViewModel
import com.example.amistapp.Chats.VentanaChats
import com.example.amistapp.DatosPerfil.DatosPerfilViewModel
import com.example.amistapp.DatosPerfil.VentanaDatosPerfil
import com.example.amistapp.Login.LoginScreen
import com.example.amistapp.Login.NoEstasActivado
import com.example.amistapp.Parametros.Rutas
import com.example.amistapp.Modelos.Evento
import com.example.amistapp.estandar.BodyVentanaEventosEstandar

import com.example.amistapp.estandar.EstandarViewModel
import com.example.amistapp.estandar.EventosDisponiblesEstandar
import com.example.amistapp.estandar.MapsAsistirVentana
import com.example.amistapp.estandar.MisEventos
import com.example.amistapp.Administrador.Eventos.MostrarAsistentes
import com.example.amistapp.Administrador.Eventos.MostrarFotosEventos
import com.example.amistapp.Administrador.Eventos.MostrarInscritosAdmin
import com.example.amistapp.Administrador.Eventos.SubirFotosEventos
import com.example.amistapp.estandar.MostrarInscritos
import com.example.amistapp.estandar.VentanaEstandar


class MainActivity : ComponentActivity() {
    val loginVM = LoginViewModel()
    val datosPerfilVM = DatosPerfilViewModel()
    val administradorVM = AdministradorViewModel()
    val eventoVM = EventoViewModel()
    val estandarVM = EstandarViewModel()
    val chatVM = ChatViewModel()
    val eventoId = ""
    val evento = Evento()
    val emailLogeado = ""
    companion object {
        const val CHANNEL_ID = "mi_canal_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        enableEdgeToEdge()
        setContent {
            val contexto = LocalContext.current

            AmistAppTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            1
                        )
                    }
                }
                val navController = rememberNavController()
                //Durante la creacion de la ventana proximos evento
//                NavHost(navController = navController, startDestination = Rutas.historialEventos){
                NavHost(navController = navController, startDestination = Rutas.login) {
                    composable(Rutas.login) {
                        LoginScreen(navController, loginVM, datosPerfilVM)
                    }
                    composable(Rutas.estandar) {
                        VentanaEstandar(navController, chatVM, loginVM, estandarVM, datosPerfilVM)
                    }
                    composable(Rutas.administrador) {
                        VentanaAdministrador(navController, datosPerfilVM, eventoVM, loginVM)
//                        Text(text = "Pantalla administrador", fontSize = 24.sp, modifier = Modifier.fillMaxSize())
                    }
                    composable(Rutas.perfil) {
                        VentanaDatosPerfil(navController, loginVM, datosPerfilVM, contexto)
                    }
                    composable(Rutas.noActivado) {
                        NoEstasActivado(navController, loginVM, contexto)
                    }
                    composable(Rutas.altaUsuario) {
                        AltaUsuarios(navController, administradorVM, loginVM)
                    }
                    composable(Rutas.bajaUsuarios) {
                        BajaUsuarios(administradorVM, loginVM, navController)
                    }
                    composable(Rutas.activarDesActivar) {
                        ActivarDesactivarUsuarios(administradorVM, loginVM, navController)
                    }
                    composable(Rutas.cambiarRole) {
                        CambiarRoleAdministrador(administradorVM, loginVM, navController)
                    }
                    composable(Rutas.mapa) {
                        MapsVentana(navController, eventoVM)
                    }
                    composable(Rutas.crearEvento) {
                        CrearEvento(navController, eventoVM)
                    }
                    composable(Rutas.proximosEventos) {
                        ProoximosEventos(navController, eventoVM)
                    }
                    composable(Rutas.historialEventos) {
                        HistorialEventos(navController, eventoVM)
                    }


                    composable("chats/{email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email")
                        VentanaChats(
                            navController,
                            loginVM,
                            estandarVM,
                            chatVM,
                            email
                        )
                    }

                    composable(Rutas.eventosDisponibles) {
                        EventosDisponiblesEstandar(navController, eventoVM, loginVM)
                    }
                    composable(Rutas.misEventos) {
                        MisEventos(navController, estandarVM, loginVM, eventoVM, contexto)
                    }
                    composable(Rutas.bodyVentanaEstandarEventos) {
                        BodyVentanaEventosEstandar(navController)
                    }
                    composable(Rutas.bodyVentanaAdminEventos) {
                        BodyVentanaAdminEventos(navController)
                    }
                    composable(Rutas.bodyVentanaAdminUsuarios) {
                        BodyVentanaAdminUsuarios(navController)
                    }
                    composable(Rutas.mostrarInscritos) {
                        MostrarInscritos(navController, eventoVM)
                    }
                    composable(Rutas.mapaUbicacionUsuario) {
                        MapsAsistirVentana(
                            navController,
                            estandarVM,
                            eventoVM,
                            evento,
                            emailLogeado,
                            contexto
                        )
                    }
                    composable(Rutas.mostrarAsistentes) {
                        MostrarAsistentes(navController, eventoVM)

                    }
                    composable(Rutas.mostrarInscritosAdmin){
                        MostrarInscritosAdmin(navController,eventoVM)
                    }
                    composable(Rutas.subirFotosEventos){
                        SubirFotosEventos(eventoVM, navController)
                    }
                    composable(Rutas.mostrarFotosEventos){
                        MostrarFotosEventos(eventoVM,navController)
                    }
                }
            }
        }
    }
    private fun createNotificationChannel() {
        val CHANNEL_ID = "mi_canal"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones"
            val descriptionText = "Canal para notificaciones de la app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MainActivity.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Registrar el canal en el sistema
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

