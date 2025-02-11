package com.example.amistapp.DatosPerfil

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.R
import com.example.amistapp.BuildConfig
import com.example.amistapp.Rutas
import java.io.File
import java.time.LocalDate
import java.time.Period
import kotlin.math.roundToInt

@Composable
fun VentanaDatosPerfil(
    navController: NavHostController,
    loginVM: LoginViewModel,
    datosPerfilVM: DatosPerfilViewModel,
    contexto: Context
){
    val emailLogeado = loginVM.getCurrentUser()?.email

    Column(modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth()) {
        Spacer(modifier = Modifier.height(50.dp))
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),verticalAlignment = Alignment.CenterVertically)
        {
            nick(datosPerfilVM)
            edad(datosPerfilVM)
        }
        genero(datosPerfilVM)
        tipoDeRelacion(datosPerfilVM)
        hijos(datosPerfilVM)
        interesadoEn(datosPerfilVM)
        Spacer(modifier = Modifier.height(10.dp))
        intDeporte(datosPerfilVM)
        intArte(datosPerfilVM)
        intPolitica(datosPerfilVM)
        Spacer(modifier = Modifier.height(10.dp))
        tomarFoto(datosPerfilVM, contexto)
        Spacer(modifier = Modifier.weight(1f))
        Row( modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
            if (emailLogeado != null) {
                botonAceptar(navController, emailLogeado, datosPerfilVM)
            }
            Spacer(modifier = Modifier.width(8.dp))
            botonCancelar(navController)
        }

//        Spacer(modifier = Modifier.height(10.dp))

    }
}

//@SuppressLint("SuspiciousIndentation")
@Composable
fun nick(datosPerfilVM: DatosPerfilViewModel ){
    val nick by remember {datosPerfilVM.nick}

    Text(
        text = "Nick:",
        color = colorResource(R.color.texto),
        fontSize = 15.sp,
        modifier = Modifier.padding(10.dp)
    )
    TextField(
        value = nick,
        onValueChange = {nuevoNick ->
            datosPerfilVM.setNick(nuevoNick)
                        },
        modifier = Modifier.width(150.dp),
        singleLine = true,
        textStyle = TextStyle(fontSize = 15.sp)
    )
}



// Devuelve verdadero cuando es menor de edad

//@SuppressLint("SuspiciousIndentation")
@Composable
fun edad(datosPerfilVM: DatosPerfilViewModel): Boolean {

    var deshabilitar by remember { mutableStateOf(false) }
    var edad by remember { mutableStateOf(0) }
    var fechaNacimiento by remember { mutableStateOf<LocalDate?>(null) }

    val fechaActual = LocalDate.now()

    val calcularEdad: (LocalDate, LocalDate) -> Int = { fechaNacimiento, fechaActual ->
        Period.between(fechaNacimiento, fechaActual).years
    }

    val contexto = LocalContext.current
    val ventanaFecha = android.app.DatePickerDialog(
        contexto,
        { _, año, mes, dia ->
            val fechaSeleccionada = LocalDate.of(año, mes + 1, dia)
            fechaNacimiento = fechaSeleccionada
            edad = calcularEdad(fechaSeleccionada, fechaActual)
            if (edad<18){
                Toast.makeText(contexto, "Debes ser mayor de edad", Toast.LENGTH_SHORT).show()
                deshabilitar= true
            }
            else{
                deshabilitar = false
                datosPerfilVM.setEdad(edad)
            }
        },
        fechaActual.year,
        fechaActual.monthValue - 1,
        fechaActual.dayOfMonth
    )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            Text(
                text = "Edad: ",
                color = colorResource(R.color.texto),
                fontSize = 15.sp,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = edad.toString()
            )
            IconButton(onClick = {ventanaFecha.show()})
            {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Fecha de nacimiento",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

    datosPerfilVM.setEdad(edad)
    return deshabilitar
}

//@SuppressLint("SuspiciousIndentation")
//@SuppressLint("SuspiciousIndentation")
@SuppressLint("SuspiciousIndentation")
@Composable
fun interesadoEn(datosPerfilVM: DatosPerfilViewModel){
    var selectedText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var opciones = listOf<String>("Hombres", "Mujeres", "Ambos")

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Interesado en: ",
                color = colorResource(R.color.texto),
                fontSize = 15.sp,
                modifier = Modifier.padding(10.dp)
            )

        Box(modifier = Modifier.width(140.dp)) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = { selectedText = it },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .clickable {
                        expanded = true
                    }
                    .wrapContentWidth()
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false })
            {
                opciones.forEach { opcion ->
                    DropdownMenuItem(text = { Text(text = opcion) }, onClick = {
                        expanded = false
                        selectedText = opcion
                        datosPerfilVM.setInteresadoEn(opcion)

                    })
                }
            }
        }
    }
}


@Composable
fun genero(datosPerfilVM: DatosPerfilViewModel){
    var selected by remember {mutableStateOf("")}

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Row( verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)){
            RadioButton(selected = selected == "Hombre", onClick = { selected = "Hombre" })
            Text(text = "Hombre",
                 color = colorResource(R.color.texto),
                 fontSize = 15.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row( verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)){
            RadioButton(selected = selected == "Mujer", onClick = {  selected = "Mujer" })
            Text(text = "Mujer",
                 color = colorResource(R.color.texto),
                 fontSize = 15.sp
                )
        }
    }
    datosPerfilVM.setGenero(selected)
}

@Composable
fun tipoDeRelacion(datosPerfilVM: DatosPerfilViewModel){
    var estadoSeria by remember { mutableStateOf(false) }
    var estadoSEnable by remember { mutableStateOf(true) }
    var estadoEsporadica by remember { mutableStateOf(false) }
    var estadoEEnable by remember { mutableStateOf(true) }

        Column (modifier = Modifier.padding(8.dp))
        {
            Row (horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically)
            {
                Checkbox(
                    checked = estadoSeria,
                    enabled = estadoSEnable,
                    onCheckedChange = { estadoSeria = !estadoSeria })

                Text(" Busca una relación seria",
                    color = colorResource(R.color.texto),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 4.dp))
            }

    }
    datosPerfilVM.setRelacionSeria(estadoSeria)

}

@Composable
fun hijos(datosPerfilVM: DatosPerfilViewModel){
    var estadoTiene by remember { mutableStateOf(false) }
    var estadoTEnable by remember { mutableStateOf(true) }
    var estadoQuiere by remember { mutableStateOf(false) }
    var estadoQEnable by remember { mutableStateOf(true) }

    Row (horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically)
    {
        Checkbox(
            checked = estadoTiene,
            enabled = estadoTEnable,
            onCheckedChange = { estadoTiene = !estadoTiene }
        )
        Text("Tiene hijos",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(vertical = 12.dp))
        Checkbox(
            checked = estadoQuiere,
            enabled = estadoQEnable,
            onCheckedChange = { estadoQuiere= !estadoQuiere }
        )
        Text("Quiere tener hijos",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(vertical = 12.dp))
    }
    datosPerfilVM.setQuiereHijos(estadoQuiere)
    datosPerfilVM.setTieneHijos(estadoTiene)
}

// Muestra la barra para el deporte y lo envia al VM
@Composable
fun intDeporte(datosPerfilVM: DatosPerfilViewModel) {

    var sliderPos by remember { mutableStateOf(0f) }
    var valorPuntual by remember { mutableStateOf("") }
    Row (horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    )
    {
        Text(text = "Deporte: ",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp))
        Slider(
            value = sliderPos,
            valueRange = 0f..100f,
            onValueChange = {
                sliderPos = it
            },
            onValueChangeFinished = { //Este evento se dispara cuando estamos en las posiciones de los step.
                datosPerfilVM.setInteDepor( sliderPos.roundToInt())
            },
            modifier = Modifier
//                .weight(1f) // Permite que el slider se ajuste automáticamente
                .width(200.dp)
        )
        Text(text = sliderPos.roundToInt().toString(),
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 20.dp))
    }
}

@Composable
fun intArte(datosPerfilVM: DatosPerfilViewModel) {

    var sliderPos by remember { mutableStateOf(0f) }
    var valorPuntual by remember { mutableStateOf("") }
    Row (horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    )
    {
        Text(text = "Arte: ",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(20.dp))
        Slider(
            value = sliderPos,
            valueRange = 0f..100f,
            onValueChange = {
                sliderPos = it
            },
            onValueChangeFinished = { //Este evento se dispara cuando estamos en las posiciones de los step.
                datosPerfilVM.setInteArte( sliderPos.roundToInt())
            },
            modifier = Modifier
//                .weight(1f) // Permite que el slider se ajuste automáticamente
                .width(200.dp)
        )
        Text(text = sliderPos.roundToInt().toString(),
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 20.dp))
    }
}

@Composable
fun intPolitica(datosPerfilVM: DatosPerfilViewModel) {

    var sliderPos by remember { mutableStateOf(0f) }
    var valorPuntual by remember { mutableStateOf("") }
    Row (horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    )
    {
        Text(text = "Política: ",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp))
        Slider(
            value = sliderPos,
            valueRange = 0f..100f,
            onValueChange = {
                sliderPos = it
            },
            onValueChangeFinished = { //Este evento se dispara cuando estamos en las posiciones de los step.
                datosPerfilVM.setIntePolit( sliderPos.roundToInt())
            },
            modifier = Modifier
//                .weight(1f) // Permite que el slider se ajuste automáticamente
                .width(200.dp)
        )
        Text(text = sliderPos.roundToInt().toString(),
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 20.dp))
    }
}

@Composable
fun tomarFoto(datosPerfilVM: DatosPerfilViewModel, contexto: Context)
{
    val imageUri by datosPerfilVM.imageUri.observeAsState(Uri.EMPTY)
    val imageFile by datosPerfilVM.imageFile.observeAsState(null)

    //Lanzadores de permisos y cámara.
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageFile?.let { file ->
                datosPerfilVM.updateImageUri(Uri.fromFile(file))

                val imagePath = imageUri.toString()
                datosPerfilVM.setFotoPerfil(imagePath)
            }
        } else {
            datosPerfilVM.updateImageUri(Uri.EMPTY)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val file = File.createTempFile("pfp", ".jpg", contexto.cacheDir)
            datosPerfilVM.setImageFile(file)
            cameraLauncher.launch(FileProvider.getUriForFile(contexto, BuildConfig.APPLICATION_ID + ".provider", file))
        }
    }
    //Pintamos los elementos composables.
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        //Muestra la imagen estándar si no se ha seleccionado nada.
            if (imageUri != Uri.EMPTY) {
             //Esta función es de COIL.
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(100.dp)
                        .clickable {
                            // Al hacer clic sobre la imagen, lanzamos la cámara
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.pfp),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .fillMaxWidth()
                    .   size(100.dp)
                        .clickable {
                            // Al hacer clic sobre la imagen, lanzamos la cámara
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        },
                    alignment = Alignment.Center
                )
            }
        }
    }
}


@Composable
// Cuando pincha en aceptar, se guardan los datos del perfil el la bd
fun botonAceptar(
    navController: NavHostController,
    emailLogeado: String,
    datosPerfilVM: DatosPerfilViewModel
) {
    Button(onClick = {

        Log.e("Izaskun"," Estoy en el botón Aceptar.El email logeado es: ${emailLogeado}")
        datosPerfilVM.setCompletado(true)
        datosPerfilVM.actualizarPerfil(emailLogeado)
        navController.navigate(Rutas.login)},
//        datosPerfilVM.actualizarPerfil(emailLogeado, onSuccess = {
//            navController.navigate(Rutas.login) {  // Navega a la pantalla principal
//                popUpTo(Rutas.login) { inclusive = true }  // Borra la pila de navegación
//            }
//        })
//    },

        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
    )
    {
        Text(text = "Aceptar")
    }

}

@Composable

fun botonCancelar(navController: NavHostController) {
    Button(onClick = {navController.navigate(Rutas.login)},
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
    )
    {
        Text(text = "Cancelar")
    }
}