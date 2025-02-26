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

    fun activarDesactivarUser(email: String, estado: Boolean){

        val nuevoEstado = !estado
        val usuariosRef = db.collection(Colecciones.Usuarios)

        usuariosRef.whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val userId = document.id // Obtiene el ID del documento del usuario
                        usuariosRef.document(userId).update("activado", nuevoEstado)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Usuario $email actualizado a estado: $nuevoEstado")
                                obtenerUsuarios()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error al actualizar usuario: ", e)
                            }
                    }
                } else {
                    Log.e("Firestore", "Usuario no encontrado con email: $email")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al buscar usuario: ", e)
            }

    }

    fun cambiarRole(email: String, esAdministrador: Boolean){

        val usuariosRef = db.collection(Colecciones.Usuarios)

        usuariosRef.whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val userId = document.id // Obtiene el ID del documento

                        // Obtiene la lista de roles actual del usuario
                        val listaRolesActual = document.get("role") as List<String>

                        // Si es administrador, eliminar el rol de administrador
                        val listaRolesModificada = listaRolesActual.toMutableList()

                        if (esAdministrador) {
                            // Si es administrador, elimina "administrador"
                            listaRolesModificada.remove("administrador")
                        } else {
                            // Si no es administrador, añade "administrador"
                            listaRolesModificada.add("administrador")
                        }

                        // Actualizar el documento con los nuevos roles
                        usuariosRef.document(userId).update("role", listaRolesModificada)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Rol 'administrador' actualizado correctamente.")
                                obtenerUsuarios()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error al actualizar rol 'administrador': ", e)
                            }
                    }
                } else {
                    Log.e("Firestore", "Usuario no encontrado con email: $email")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al buscar usuario: ", e)
            }

    }
}