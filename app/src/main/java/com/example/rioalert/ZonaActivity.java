package com.example.rioalert;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ZonaActivity extends AppCompatActivity {

    private TextView itemRioSuquia;
    private TextView itemRioSanAntonio;
    private TextView tvVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zona);

        itemRioSuquia = findViewById(R.id.itemRioSuquia);
        itemRioSanAntonio = findViewById(R.id.itemRioSanAntonio);
        tvVolver = findViewById(R.id.tvVolver);

        tvVolver.setOnClickListener(v -> finish());

        itemRioSuquia.setOnClickListener(v -> seleccionarZona("Río Suquía"));
        itemRioSanAntonio.setOnClickListener(v -> seleccionarZona("Río San Antonio"));
    }

    private void seleccionarZona(String nombreZona) {
        // Guarda la zona elegida para usarla en el resto de la app
        SharedPreferences prefs = getSharedPreferences("RiverAlertPrefs", MODE_PRIVATE);
        prefs.edit().putString("zona_seleccionada", nombreZona).apply();

        Toast.makeText(this, "Seleccionado: " + nombreZona, Toast.LENGTH_SHORT).show();
        finish(); // Cierra y regresa
    }
}