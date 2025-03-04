package com.example.amistapp.Parametros

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.amistapp.MainActivity
import com.example.amistapp.R

object NotificationHelper {
    private const val CHANNEL_ID = "mi_canal_id"

     fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones"
            val descriptionText = "Canal para notificaciones de la app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Registrar el canal en el sistema
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun sendNotificationBasic(context: Context){
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        var notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_no_leidos)
            .setContentTitle("Mensajes")
            .setContentText("Tienes mensajes sin leer")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(hashCode(), notification)
    }

}