package com.example.rioalert;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";
    private MapView map = null;
    private GeofencingClient geofencingClient;

    public MapFragment() {
        // Constructor vacío
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Cargar configuración primero
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));

        // 2. Definir User-Agent personalizado DESPUÉS del load
        Configuration.getInstance().setUserAgentValue("RioAlertApp_Cordoba_Dev_v1");

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        TextView tvCoordenadas = view.findViewById(R.id.tvCoordenadas);
        map = view.findViewById(R.id.map);
        geofencingClient = LocationServices.getGeofencingClient(requireActivity());

        if (map != null) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);

            double latitud = -31.4135;
            double longitud = -64.1810;
            GeoPoint startPoint = new GeoPoint(latitud, longitud);

            map.getController().setZoom(15.0);
            map.getController().setCenter(startPoint);

            if (tvCoordenadas != null) {
                tvCoordenadas.setText("Lat: " + latitud + " | Lng: " + longitud);
            }

            // Marcador central
            Marker startMarker = new Marker(map);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.setTitle("Punto de Control del Río");
            map.getOverlays().add(startMarker);

            // 1. Polígono de inundación (OSMDroid para UI + Google LatLng para cálculo)
            configurarPoligonoAlerta(latitud, longitud);

            // 2. Geofencing API (Registro del perímetro en Google Play Services)
            registrarGeocerca(latitud, longitud, 300.0f); // 300 metros de radio
        }

        return view;
    }

    private void configurarPoligonoAlerta(double centerLat, double centerLng) {
        // Vértices del área de inundación
        List<GeoPoint> geoPointsOsm = new ArrayList<>();
        geoPointsOsm.add(new GeoPoint(centerLat + 0.002, centerLng - 0.003));
        geoPointsOsm.add(new GeoPoint(centerLat + 0.003, centerLng + 0.002));
        geoPointsOsm.add(new GeoPoint(centerLat - 0.002, centerLng + 0.003));
        geoPointsOsm.add(new GeoPoint(centerLat - 0.003, centerLng - 0.002));

        // Dibujar polígono en OSMDroid
        Polygon polygon = new Polygon();
        polygon.setPoints(geoPointsOsm);
        polygon.setFillColor(Color.argb(70, 255, 0, 0)); // Rojo traslúcido
        polygon.setStrokeColor(Color.RED);
        polygon.setStrokeWidth(3.0f);
        polygon.setTitle("Área de Crecida / Inundación");
        map.getOverlays().add(polygon);

        // Convertir a LatLng de Google para usar PolyUtil
        List<LatLng> polygonGoogle = new ArrayList<>();
        for (GeoPoint gp : geoPointsOsm) {
            polygonGoogle.add(new LatLng(gp.getLatitude(), gp.getLongitude()));
        }

        // Test instantáneo con PolyUtil
        LatLng userPosition = new LatLng(centerLat, centerLng);
        boolean estaEnRiesgo = PolyUtil.containsLocation(userPosition, polygonGoogle, true);

        if (estaEnRiesgo) {
            Log.w(TAG, "PolyUtil: El punto evaluado está dentro de la zona de inundación.");
        }
    }

    private void registrarGeocerca(double lat, double lng, float radioMetros) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Geofence geofence = new Geofence.Builder()
                .setRequestId("GEOFENCE_RIO_ALERTA_1")
                .setCircularRegion(lat, lng, radioMetros)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        Intent intent = new Intent(requireContext(), GeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        geofencingClient.addGeofences(request, pendingIntent)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Geocerca registrada con éxito"))
                .addOnFailureListener(e -> Log.e(TAG, "Fallo al registrar geocerca: " + e.getMessage()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}