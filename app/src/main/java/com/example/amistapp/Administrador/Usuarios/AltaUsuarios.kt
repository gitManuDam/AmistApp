package com.example.amistapp.Administrador.Usuarios

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.amistapp.Administrador.AdministradorViewModel
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.R
import com.example.amistapp.Parametros.Rutas

@Composable
 fun AltaUsuarios(navController: NavController, administradorVM: AdministradorViewModel, loginVM: LoginViewModel){
    val context = LocalContext.current

    val errorMessage by administradorVM.errorMessage.collectAsState()
    val isLoading by administradorVM.isLoading.collectAsState()
    val loginSuccess by administradorVM.loginSuccess.collectAsState()

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Alta nueva cuenta de usuario",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                    administradorVM.registerNewUserWithEmail(email.text, password.text)
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.botones), // Color de fondo del botón
                contentColor = colorResource(R.color.textoBotones) // Color del texto
            )
        ) {
            Text(
                "Registrar",
                color = colorResource(R.color.texto),
                fontSize = 15.sp,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))


        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }

    LaunchedEffect(loginSuccess) {
        // si no ha habido problemas en aauthentication ( que ya existiera)
        if (loginSuccess) {
            Toast.makeText(context, "Usuario dado de alta", Toast.LENGTH_SHORT).show()
            if (!loginVM.existeUsuario()) {
                loginVM.addUsuario(email.text!!)
            }
            navController.navigate(Rutas.administrador)
        }
    }

}