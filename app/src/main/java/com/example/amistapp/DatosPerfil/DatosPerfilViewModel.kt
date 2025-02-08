package com.example.amistapp.DatosPerfil

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.amistapp.Colecciones
import com.example.amistapp.Login.LoginViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.io.File
import java.net.URL

class DatosPerfilViewModel : ViewModel() {

    // Para la base de datos que contendrá  a los usuarios
    val db = Firebase.firestore

    private val _completado = mutableStateOf(false)
    val completado: State<Boolean> get() = _completado

    private val _nick = mutableStateOf("")
    val nick: State<String> get() = _nick

    private val _fotoPerfil = mutableStateOf("")
    val fotoPerfil:State<String> get() = _fotoPerfil

    private val _edad = mutableStateOf(0)
    val edad: State<Int> get() = _edad

    private val _amigos = mutableStateListOf<String>()
    val amigos: SnapshotStateList<String> get() = _amigos

    private val _relacionSeria = mutableStateOf(false)
    val relacionSeria: State<Boolean> get() = _relacionSeria

    private val _interesDeporte = mutableStateOf(0)
    val interesDeporte: State<Int> get() = _interesDeporte

    private val _interesArte = mutableStateOf(0)
    val interesArte: State<Int> get() = _interesArte

    private val _interesPolitica = mutableStateOf(0)
    val interesPolitica: State<Int> get() = _interesPolitica

    private val _tieneHijos = mutableStateOf(false)
    val tieneHijos: State<Boolean> get() = _tieneHijos

    private val _quiereHijos = mutableStateOf(false)
    val quiereHijos: State<Boolean> get() = _quiereHijos

    private val _interesadoEn = mutableStateOf("")
    val interesadoEn:State<String> get() = _interesadoEn

    private val _genero = mutableStateOf("")
    val genero:State<String> get() = _genero

    private val _Error = MutableLiveData<String?>()
    val Error : LiveData<String?> = _Error

    // para las fotos
    private val _urlPfp = MutableLiveData<String>()
    val urlPfp: LiveData<String> = _urlPfp

    private val _imageUri = MutableLiveData<Uri>(Uri.EMPTY)
    val imageUri: LiveData<Uri> get() = _imageUri

    private val _imageFile = MutableLiveData<File?>()
    val imageFile: LiveData<File?> get() = _imageFile


    fun setNick(nuevoNick: String){
        if (nuevoNick.isEmpty()){
            _Error.value= "El campo no puede estar vacio"
        }else{
            _nick.value= nuevoNick
            _Error.value = null
        }
    }

    fun setFotoPerfil(nuevaFotoPerfil: String){
        if (nuevaFotoPerfil.isEmpty()){
            _Error.value= "El campo no puede estar vacio"
        }else{
            _fotoPerfil.value= nuevaFotoPerfil
            _Error.value = null
        }
    }

    fun setEdad(nuevaEdad: Int){
        if (nuevaEdad.toString().isEmpty()){
            _Error.value= "El campo no puede estar vacio"
        }else{
            _edad.value = nuevaEdad
            _Error.value = "El campo no puede estar vacio"
        }
    }

    fun setRelacionSeria(nRelacionSeria:Boolean){
        _relacionSeria.value = nRelacionSeria
    }

    fun setInteDepor(nInteresDeporte: Int){
        if (nInteresDeporte.toString().isEmpty()){
            _Error.value= "El campo no puede estar vacio"
        }else{
            _interesDeporte.value = nInteresDeporte
            _Error.value = "El campo no puede estar vacio"
        }
    }

    fun setInteArte(nInteresArte: Int){
        if (nInteresArte.toString().isEmpty()){
            _Error.value= "El campo no puede estar vacio"
        }else{
            _interesArte.value = nInteresArte
            _Error.value = "El campo no puede estar vacio"
        }
    }

    fun setIntePolit(nInteresPolit: Int){
        if (nInteresPolit.toString().isEmpty()){
            _Error.value= "El campo no puede estar vacio"
        }else{
            _interesPolitica.value = nInteresPolit
            _Error.value = "El campo no puede estar vacio"
        }
    }

    fun setTieneHijos(nTenerHijos:Boolean){
        _tieneHijos.value = nTenerHijos
    }

    fun setQuiereHijos(nQuiereHijos:Boolean){
        _relacionSeria.value = nQuiereHijos
    }

    fun setGenero(nGenero: String){
        if (nGenero.isEmpty()){
            _Error.value= "El campo no puede estar vacio"
        }else{
            _genero.value= nGenero
            _Error.value = null
        }
    }

    fun setInteresadoEn(nInteresadoEn: String){
        if (nInteresadoEn.isEmpty()){
            _Error.value= "El campo no puede estar vacio"
        }else{
            _interesadoEn.value= nInteresadoEn
            _Error.value = null
        }
    }

    fun setCompletado(nCompletado: Boolean){
        _completado.value = nCompletado
    }

    // Para las fotos
    fun updateImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun setImageFile(file: File) {
        _imageFile.value = file
    }

    fun setUrlPfp(url: String) {
        this._urlPfp.value = url
    }

   fun actualizarPerfil(emailLogeado:String){
       Log.e("Izaskun"," Estoy en el actualizarPerfil.El email logeado es: ${emailLogeado}" )

        val usuariosRef = db.collection(Colecciones.Usuarios)

        val perfilActualizado = mapOf(
            "completado" to completado.value,
            "nick" to nick.value,
            "fotoPerfil" to fotoPerfil.value,
            "edad" to edad.value,
            "amigos" to amigos.toList(),
            "relacionSeria" to relacionSeria.value,
            "interesDeporte" to interesDeporte.value,
            "interesArte" to interesArte.value,
            "interesPolitica" to interesPolitica.value,
            "tieneHijos" to tieneHijos.value,
            "quiereHijos" to quiereHijos.value,
            "interesadoEn" to interesadoEn.value,
            "genero" to genero.value
        )

       usuariosRef.document(emailLogeado)
           .set(mapOf("perfil" to perfilActualizado), SetOptions.merge())
           .addOnSuccessListener {
               Log.e("Izaskun", "Perfil actualizado con éxito")
               limpiarValores()

           }
           .addOnFailureListener { e ->
               Log.e("Izaskun", "Error al actualizar el perfil: $e")
           }

//        usuariosRef
//            .document(emailLogeado)
//            .update(mapOf("perfil" to perfilActualizado))
//            .addOnSuccessListener {
//                Log.e("Izaskun", "Perfil actualizado con éxito")
//                limpiarValores()
//            }
//            .addOnFailureListener { e ->
//                Log.e("Izaskun", "Error al actualizar el perfil: $e")
//            }




//        usuariosRef
//            .whereEqualTo("email", emailLogeado)
//            .get()
//            .addOnSuccessListener { consulta ->
//                if (!consulta.isEmpty) {
//                    val usuarioDoc = consulta.documents[0]  // Obtener el primer documento de la consulta
//
//                    // Se actualizan los campos del perfil con los datos proporcionados
//                    val perfilRef = usuarioDoc.reference.collection("perfil")
//
//                    perfilRef.document("perfil")  // Suponiendo que el perfil está dentro de una subcolección o es un campo directo
//                        .update(perfilActualizado)
//                        .addOnSuccessListener {
//                            Log.e("Izaskun", "Perfil actualizado con éxito")
//                            limpiarValores()
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e("Izaskun", "Error al actualizar el perfil: $e")
//                        }
//                } else {
//                    Log.e("Izaskun", "El usuario no existe")
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("Izaskun", "Error al consultar al usuario: $e")
//            }
//


//        usuariosRef.update("perfil", perfilActualizado)
//            .addOnSuccessListener{
//                Log.e("Izaskun", "Perfil actualizado con éxito")
//                limpiarValores()
//            }
//            .addOnFailureListener {
//                Log.e("Izaskun", "Error al actualizar el perfil")
//            }

    }

    fun limpiarValores(){
        _completado.value = false
        _nick.value = ""
        _fotoPerfil.value = ""
        _edad.value = 0
        _amigos.clear()
        _relacionSeria.value = false
        _interesDeporte.value = 0
        _interesArte.value = 0
        _interesPolitica.value = 0
        _tieneHijos.value = false
        _quiereHijos.value = false
        _interesadoEn.value = ""
        _genero.value = ""
    }
}