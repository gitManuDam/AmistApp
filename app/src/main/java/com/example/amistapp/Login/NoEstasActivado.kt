package com.example.amistapp.Login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.amistapp.R
import com.example.amistapp.Rutas
// Autora: Izaskun
@Composable
fun NoEstasActivado(navController: NavHostController, loginVM: LoginViewModel, contexto: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No estás activado aún, revisa tu correo",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp)
        )

        botonVolverLogin(navController, loginVM,contexto)
    }
}

@Composable
fun botonVolverLogin(navController: NavHostController, loginVM: LoginViewModel, contexto: Context) {
    Button(
        onClick = {
//        eventoVM.limpiarDatos()
            loginVM.signOut(contexto)
            Toast.makeText(contexto, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
            navController.navigate(Rutas.login) {
                popUpTo(Rutas.login) { inclusive = true }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
    )
    {
        Text(text = "Volver")
    }

}