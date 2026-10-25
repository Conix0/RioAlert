package com.example.rioalert;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etTelefono, etEmail, etPassword;
    private MaterialButton btnRegister, btnLogin, btnGoogleAuth;

    // Instancia de Firebase Auth y Realtime Database
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("USUARIOS");

        // Vincular vistas con los IDs del layout XML
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleAuth = findViewById(R.id.btnGoogleAuth);

        // Listener botón registrarse
        btnRegister.setOnClickListener(v -> registrarUsuario());

        // Listener botón iniciar sesión: abre LoginActivity
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Listener botón Google
        btnGoogleAuth.setOnClickListener(v -> {
            Toast.makeText(RegisterActivity.this, "Próximamente: Autenticación con Google", Toast.LENGTH_SHORT).show();
        });
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Validaciones de campos obligatorios
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingresá tu nombre");
            etNombre.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingresá tu correo electrónico");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Ingresá un correo electrónico válido");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingresá una contraseña");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        // Crear usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

                        if (userId != null) {
                            // Guardar datos adicionales (nombre, teléfono opcional, email) en la base de datos
                            Map<String, Object> usuarioMap = new HashMap<>();
                            usuarioMap.put("nombre", nombre);
                            usuarioMap.put("email", email);
                            usuarioMap.put("telefono", TextUtils.isEmpty(telefono) ? "No especificado" : telefono);

                            mDatabase.child(userId).setValue(usuarioMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterActivity.this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                                        // Redirigir a MainActivity (panel principal del río y mapa)
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterActivity.this, "Error al guardar perfil: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Error al registrarse";
                        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}