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
import com.example.amistapp.Administrador.ActivarDesactivarUsuarios
import com.example.amistapp.Administrador.AdministradorViewModel
import com.example.amistapp.Administrador.AltaUsuarios
import com.example.amistapp.Administrador.BajaUsuarios
import com.example.amistapp.Administrador.CambiarRoleAdministrador
import com.example.amistapp.Administrador.VentanaAdministrador
import com.example.amistapp.DatosPerfil.DatosPerfilViewModel
import com.example.amistapp.DatosPerfil.VentanaDatosPerfil
import com.example.amistapp.Login.LoginScreen
import com.example.amistapp.Login.NoEstasActivado
import com.example.amistapp.Login.VentanaElegirRoleAUsar


class MainActivity : ComponentActivity() {
    val loginVM = LoginViewModel()
    val datosPerfilVM = DatosPerfilViewModel()
    val administradorVM = AdministradorViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val contexto = LocalContext.current

            AmistAppTheme {
                val navController = rememberNavController()
                //Durante la creacion de la ventana baja en Administrador
//                NavHost(navController = navController, startDestination = Rutas.bajaUsuarios){
                NavHost(navController = navController, startDestination = Rutas.login){
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
                    composable(Rutas.roleElegido) {
                        VentanaElegirRoleAUsar()
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
                }
            }
        }
    }
}
