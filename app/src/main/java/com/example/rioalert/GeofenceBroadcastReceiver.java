package com.example.rioalert;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceReceiver";
    private static final String CHANNEL_ID = "canal_alertas_rio";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent == null || geofencingEvent.hasError()) {
            int errorCode = geofencingEvent != null ? geofencingEvent.getErrorCode() : -1;
            Log.e(TAG, "Error en Geofence: " + GeofenceStatusCodes.getStatusCodeString(errorCode));
            return;
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.w(TAG, "Evento: Entrada a zona de riesgo del río.");
            enviarNotificacionAlerta(
                    context,
                    "⚠️ ¡Alerta de Crecida / Peligro!",
                    "Has ingresado al perímetro de riesgo o crecida del río. Aléjate de la orilla."
            );
        } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(TAG, "Evento: Salida de zona de riesgo.");
            enviarNotificacionAlerta(
                    context,
                    "Zona segura",
                    "Has salido del perímetro de riesgo del río."
            );
        }
    }

    private void enviarNotificacionAlerta(Context context, String titulo, String mensaje) {
        // En Android 13+ (API 33), verificar permiso POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permiso POST_NOTIFICATIONS no concedido. No se puede mostrar la alerta.");
                return;
            }
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) return;

        // Sonido de alerta por defecto
        Uri sonidoAlerta = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Patrón de vibración: esperar 0ms, vibrar 500ms, pausar 250ms, vibrar 500ms
        long[] patronVibracion = new long[]{0, 500, 250, 500};

        // Crear canal de notificación para Android 8.0 (API 26)+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Alertas de Riesgo del Río",
                    NotificationManager.IMPORTANCE_HIGH
            );
            canal.setDescription("Notificaciones críticas sobre crecidas y perímetros del río");
            canal.enableVibration(true);
            canal.setVibrationPattern(patronVibracion);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            canal.setSound(sonidoAlerta, audioAttributes);

            notificationManager.createNotificationChannel(canal);
        }

        // Intent para abrir MainActivity al tocar la notificación
        Intent intentPrincipal = new Intent(context, MainActivity.class);
        intentPrincipal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intentPrincipal,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // Puedes reemplazarlo por R.drawable.ic_launcher_foreground
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(sonidoAlerta)
                .setVibrate(patronVibracion)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}