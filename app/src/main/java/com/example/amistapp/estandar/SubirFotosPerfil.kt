package com.example.amistapp.estandar

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.amistapp.Administrador.Eventos.EventoViewModel
import com.example.amistapp.BuildConfig
import com.example.amistapp.Modelos.Usuario
import com.example.amistapp.R
import java.io.File

@Composable
fun SubirFotosPerfil(eventoVM: EventoViewModel , estandarVM: EstandarViewModel, navController: NavHostController){

    val contexto = LocalContext.current
    val usuario = estandarVM.usuarioActual.value

    estandarVM.obtenerUsuarioActual()

    Column (modifier = Modifier.padding(vertical = 50.dp)) {
        CargarImagen(contexto, eventoVM, navController,usuario)

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CargarImagen(
    contexto: Context,
    eventoVM: EventoViewModel,
    navController: NavHostController,
    usuario: Usuario?
) {
    val imageUri by eventoVM.imageUri.observeAsState(Uri.EMPTY)
    val imageFile by eventoVM.imageFile.observeAsState(null)
    val isUploading by eventoVM.isUploading.observeAsState(false)
    val uploadSuccess by eventoVM.uploadSuccess.observeAsState()
    val fileList by eventoVM.fileList.observeAsState(emptyList())


    //Lanzadores de permisos y cámara.
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageFile?.let { file ->
                    eventoVM.updateImageUri(Uri.fromFile(file))
                }
            } else {
                eventoVM.updateImageUri(Uri.EMPTY)
            }
        }


    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val file = File.createTempFile("CAM_", ".jpg", contexto.cacheDir)
                eventoVM.setImageFile(file)
                cameraLauncher.launch(
                    FileProvider.getUriForFile(
                        contexto,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    )
                )
            }
        }


    //Función para cargar imagen desde la galería.
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                //mainViewModel.updateImageUri(it)
                //Copiamos el contenido de la Uri en un archivo temporal
                val inputStream = contexto.contentResolver.openInputStream(it)
                val tempFile = File.createTempFile("GAL_", ".jpg", contexto.cacheDir)
                tempFile.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }

                //Actualizamos el ViewModel con el archivo y la Uri
                eventoVM.updateImageUri(it) //Actualiza la Uri para mostrarla en la UI
                eventoVM.setImageFile(tempFile) //Actualiza el archivo para la carga
            }
        }


    //Carga inicial de la lista de archivos contenids en 'images'.
    LaunchedEffect(Unit) {
        eventoVM.loadFileList()
    }


    //Pintamos los elementos composables.
    Column(modifier = Modifier.fillMaxSize()) {
        //Muestra la imagen estándar si no se ha seleccionado nada.
        if (imageUri != Uri.EMPTY) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Foto del evento",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.pfp),
                contentDescription = "Foto del evento",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp),
                alignment = Alignment.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Botones...
        Row(modifier = Modifier.fillMaxWidth()) {
            //Galería
            ElevatedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Seleccionar de la galeria")
            }

            //Cámara.
            ElevatedButton(
                onClick = {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Hacer foto con la cámara")
            }

            //Subir a storage.
            ElevatedButton(
                onClick = {

                    eventoVM.uploadImageAndSaveToPerfil(contexto,usuario)
                },
                modifier = Modifier.weight(1f),
                enabled = !isUploading
            ) {
                Text(if (isUploading) "Subiendo..." else "Subir foto")
            }
        }


        Spacer(modifier = Modifier.height(50.dp))
        botonVolverSubirFotos(navController)
    }

}
@Composable
fun botonVolverSubirFotos(navController: NavHostController){
    Button(
        onClick = {
//        eventoVM.limpiarDatos()
            navController.popBackStack()
//            navController.navigate(Rutas.historialEventos)
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