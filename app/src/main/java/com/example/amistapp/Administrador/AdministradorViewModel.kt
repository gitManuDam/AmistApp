package com.example.amistapp.Administrador

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.amistapp.Colecciones
import com.example.amistapp.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdministradorViewModel : ViewModel(){
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val TAG = "Izaskun"

    // Para la base de datos que contendrá  a los usuarios
    val db = Firebase.firestore

    private val _listadoUsuarios = mutableStateListOf<Usuario>()
    val listadoUsuarios: SnapshotStateList<Usuario> get() = _listadoUsuarios

    //Variables para los estados...
    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)
    val loginSuccess = MutableStateFlow(false)


    fun registerNewUserWithEmail(email: String, password: String) {
        isLoading.value = true
        errorMessage.value = null
        loginSuccess.value = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading.value = false
                if (task.isSuccessful) {
                    loginSuccess.value = true
                } else {
                    errorMessage.value = task.exception?.message ?: "Error desconocido"
                }
            }
    }

    fun obtenerUsuarios(){

        isLoading.value = true
        errorMessage.value = null
        db.collection(Colecciones.Usuarios)
            .get()
            .addOnSuccessListener { result ->
                val usuarios = result.documents.mapNotNull { doc ->
                    doc.toObject(Usuario::class.java)?.copy(idUsuario = doc.id)
                }
                // convierte la lista de usauraios s SnapshotStateList

                _listadoUsuarios.clear()// limpia la lista
                _listadoUsuarios.addAll(usuarios)// añade todos los usuarios a la lista
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
    }

    fun eliminarUsuarioPorEmail(email: String){
        isLoading.value = true
        errorMessage.value = null

        // Buscamos el usuario en la colección de usuarios por email
        db.collection(Colecciones.Usuarios)
            .whereEqualTo("email", email) // Filtramos por email
            .get()
            .addOnSuccessListener { result ->
                // Si se encuentra el documento con ese email
                if (!result.isEmpty) {
                    // como solo puede haber un usuario con ese email, entonces lo eliminamos
                    val usuarioDoc = result.documents.first()

                    // Elimina el documento
                    db.collection(Colecciones.Usuarios)
                        .document(usuarioDoc.id) // Usamos el ID del documento encontrado
                        .delete()
                        .addOnSuccessListener {
                            // El usuario fue eliminado correctamente
                            Log.d(TAG, "Usuario eliminado con éxito")
                            // Actualizamos la lista de usuarios
                            obtenerUsuarios()
                        }
                        .addOnFailureListener { exception ->
                            // Si hubo un error al eliminar
                            errorMessage.value = exception.message
                        }
                } else {
                    // No se encontró el usuario con ese email
                    errorMessage.value = "Usuario no encontrado"
                }
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                // Si hubo un error al realizar la consulta
                errorMessage.value = exception.message
                isLoading.value = false
            }
    }
}