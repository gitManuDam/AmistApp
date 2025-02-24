package com.example.amistapp.Chats

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.amistapp.Chat
import com.example.amistapp.Colecciones
import com.example.amistapp.MensajeChat
import com.example.amistapp.Usuario
import com.example.amistapp.UsuarioChat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val coleccion = "chats"
    val TAG = "Manuel"
    private val dbRef = FirebaseDatabase.getInstance().getReference(coleccion)
    val db = Firebase.firestore

    private val _mensajes = MutableStateFlow<List<MensajeChat>>(emptyList())
    val mensajes: StateFlow<List<MensajeChat>> get() = _mensajes

    private val _listadoChat = mutableListOf<Pair<String, String>>()
    val listadoChat: List<Pair<String, String>> get() = _listadoChat



    private val _listadoUsuariosChat = mutableStateListOf<UsuarioChat>()
    val listadoUsuariosChat: SnapshotStateList<UsuarioChat> get() = _listadoUsuariosChat

    val isLoading = MutableStateFlow(false)

    fun crearChat(usuario1: String, usuario2: String, callback: (String) -> Unit) {
        Log.d(TAG, "Intentando crear chat entre $usuario1 y $usuario2")
        isLoading.value = true
        dbRef.get().addOnSuccessListener { snapshot ->
            for (chatSnapshot in snapshot.children) {
                val chat = chatSnapshot.getValue(Chat::class.java)
                if (chat?.usuarios?.containsAll(listOf(usuario1, usuario2)) == true) {
                    Log.d(TAG, "Chat existente encontrado con ID: ${chat.id}")
                    callback(chat.id!!)
                    return@addOnSuccessListener
                }
            }

            val nuevoChatId = dbRef.push().key!!
            val nuevoChat = Chat(
                id = nuevoChatId,
                usuarios = listOf(usuario1, usuario2),
                mensajes = emptyList()
            )

            dbRef.child(nuevoChatId).setValue(nuevoChat).addOnSuccessListener {
                Log.d(TAG, "Chat creado con ID: $nuevoChatId")
                callback(nuevoChatId)
                isLoading.value = false
            }.addOnFailureListener {
                Log.e(TAG, "Error al crear el chat", it)
                isLoading.value = false
            }
        }
    }

    fun enviarMensaje(chatId: String, usuario: String, mensaje: String) {
        Log.d(TAG, "Enviando mensaje de $usuario en chat $chatId: $mensaje")
        val mensajeNuevo = MensajeChat(usuario, mensaje, System.currentTimeMillis())
        isLoading.value = true

        dbRef.child(chatId).get().addOnSuccessListener { snapshot ->
            val chat = snapshot.getValue(Chat::class.java)
            if (chat != null) {
                val mensajesActualizados = chat.mensajes.toMutableList()
                mensajesActualizados.add(mensajeNuevo)

                dbRef.child(chatId).child("mensajes").setValue(mensajesActualizados)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Mensaje enviado correctamente")
                        } else {
                            Log.e(TAG, "Error al enviar el mensaje", task.exception)
                        }
                        isLoading.value = false
                    }
            } else {
                Log.e(TAG, "No se encontró el chat con ID: $chatId")
            }
        }.addOnFailureListener {
            Log.e(TAG, "Error al obtener chat", it)
            isLoading.value = false
        }
    }

    fun observeMessages(chatId: String) {
        Log.d(TAG, "Observando mensajes del chat: $chatId")
        dbRef.child(chatId).child("mensajes")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nuevosMensajes = snapshot.children.mapNotNull { it.getValue(MensajeChat::class.java) }
                        .sortedByDescending { it.timestamp }
                    _mensajes.value = nuevosMensajes.toList()
                    Log.d(TAG, "Se han cargado ${nuevosMensajes.size} mensajes en el chat $chatId")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error al cargar mensajes", error.toException())
                }
            })
    }

    fun obtenerEmailsChat() {
        val currentUser = auth.currentUser
        val email = currentUser?.email
        Log.d(TAG, "Obteniendo emails de los chats del usuario $email")

        if (email == null) {
            Log.e(TAG, "Usuario no autenticado.")
            return
        }

        isLoading.value = true
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               var listaChat = mutableListOf<Pair<String, String>>()

                for (chatSnapshot in snapshot.children) {
                    val chat = chatSnapshot.getValue(Chat::class.java)

                    if (chat != null && chat.usuarios.contains(email)) {
                        val otroUsuario = chat.usuarios.firstOrNull { it != email }

                        // Obtener el último mensaje ordenado por timestamp
                        val mensajes = chat.mensajes
                        val ultimoMensaje =mensajes.last().text

                        if (otroUsuario != null) {
                            listaChat.add(Pair(otroUsuario, ultimoMensaje))
                        }
                    }
                }
                _listadoChat.clear()
                _listadoChat.addAll(listaChat)

                isLoading.value = false
                Log.d(TAG, "Se han cargado ${listadoChat.size} chats con último mensaje")
                listadoChat.forEach { Log.d(TAG, "Chat con ${it.first}: Último mensaje -> ${it.second}") }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error al obtener chats", error.toException())
                isLoading.value = false
            }
        })
    }

    fun obtenerUsuariosChat() {
        val currentUser = auth.currentUser
        val email = currentUser?.email
        Log.d(TAG, "Obteniendo usuarios de chat para el usuario $email")

        obtenerEmailsChat()


        if (email != null) {
            isLoading.value = true
            db.collection(Colecciones.Usuarios)
                .get()
                .addOnSuccessListener { snapshot ->
                    val usuariosChat = snapshot.documents.mapNotNull { document ->
                        val usuario = document.toObject(Usuario::class.java)
                        Log.d(TAG, "Usuario obtenido: $usuario")
                        if (usuario != null && usuario.email in listadoChat.map { it.first } && usuario.email != email) {
                            val ultimoMensaje = listadoChat.find { it.first == usuario.email }?.second ?: "Sin mensajes"
                            UsuarioChat(usuario, ultimoMensaje)

                        } else {
                            null
                        }
                    }

                    _listadoUsuariosChat.clear()
                    _listadoUsuariosChat.addAll(usuariosChat)
                    isLoading.value = false
                    Log.d(TAG, "Se han cargado ${usuariosChat.size} usuarios.")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al obtener usuarios: ", exception)
                    isLoading.value = false
                }
        } else {
            Log.e(TAG, "Usuario no autenticado.")
        }
    }
}
