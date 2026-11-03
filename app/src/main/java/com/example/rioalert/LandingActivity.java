package com.example.rioalert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LandingActivity extends AppCompatActivity {

    private MaterialButton btnComenzar;
    private MaterialButton btnTengoCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        btnComenzar = findViewById(R.id.btnComenzar);
        btnTengoCuenta = findViewById(R.id.btnTengoCuenta);

        LinearLayout cardZonaInteres = findViewById(R.id.cardZonaInteres);
        cardZonaInteres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, ZonaActivity.class);
                startActivity(intent);
            }
        });

        // Lleva al registro o al dashboard principal
        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Lleva al Login de la cuenta
        btnTengoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}