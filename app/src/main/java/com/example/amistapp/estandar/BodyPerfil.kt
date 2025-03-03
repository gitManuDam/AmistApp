package com.example.amistapp.estandar

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState


import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.amistapp.BuildConfig
import com.example.amistapp.DatosPerfil.DatosPerfilViewModel
import com.example.amistapp.R
import java.io.File

@Composable
fun BodyPerfil(navController: NavHostController, estandarVM: EstandarViewModel, datosPerfilVM: DatosPerfilViewModel, contexto: Context) {
    // Obtén el perfil del usuario
    val usuarioActual = estandarVM.usuarioActual.value // Accede directamente al valor del State

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
    // Llamada para obtener el perfil del usuario
    LaunchedEffect(Unit) {
        estandarVM.obtenerUsuarioActual() // Carga los datos del usuario actual
    }

    // Lanzadores para tomar foto o seleccionar foto de la galería


    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            datosPerfilVM.updateImageUri(it) // Actualiza la URI de la imagen seleccionada
            datosPerfilVM.setFotoPerfil(it.toString()) // Actualiza la URL de la foto de perfil en el ViewModel
        }
    }

    // Función para solicitar permisos y lanzar la cámara


    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Mostrar la foto de perfil
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (imageUri != Uri.EMPTY) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)

                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.pfp), // Imagen predeterminada
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        ,
                    alignment = Alignment.Center
                )
            }
        }

        // Botones para tomar foto o seleccionar de la galería
        Row(modifier = Modifier.padding(top = 16.dp).fillMaxWidth()) {
            Button(
                onClick = {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                },
                modifier = Modifier.weight(1f).padding(bottom = 8.dp)
            ) {
                Text("Tomar foto")
            }

            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Galería")
            }
            Button(
                onClick = {
                    if(imageUri != Uri.EMPTY){
                        datosPerfilVM.actualizarFotoPerfil(usuarioActual!!.email)
                        estandarVM.obtenerFotoPerfil(usuarioActual.email)
                    }

                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Actualizar perfil")
            }

        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(){
            Button(
                onClick = {
                    navController.navigate("mostrarFotosPerfil")
                }
            ){
                Text(text = "Ver mis fotos")
            }
            Button(
                onClick = {
                    navController.navigate("subirFotosPerfil")
                }
            ){
                Text(text = "Subir Fotos")
            }
        }


        // Mostrar otros datos del perfil si es necesario
        usuarioActual?.let {
            Spacer(modifier = Modifier.height(16.dp))

            // Card para mostrar los detalles de perfil
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,

            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Nombre: ${it.perfil?.nick}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Edad: ${it.perfil?.edad}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Email: ${it.email}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


