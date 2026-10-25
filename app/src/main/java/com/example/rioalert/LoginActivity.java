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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmailLogin, etPasswordLogin;
    private MaterialButton btnLoginSubmit, btnIrARegistro, btnGoogleLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // Si el usuario ya inició sesión antes, entra directo al inicio
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            irAMainActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        btnIrARegistro = findViewById(R.id.btnIrARegistro);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);


        btnLoginSubmit.setOnClickListener(v -> autenticarUsuario());


        btnIrARegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


        btnGoogleLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Próximamente acceso con Google", Toast.LENGTH_SHORT).show();
        });
    }

    private void autenticarUsuario() {
        String email = etEmailLogin.getText() != null ? etEmailLogin.getText().toString().trim() : "";
        String password = etPasswordLogin.getText() != null ? etPasswordLogin.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            etEmailLogin.setError("Ingresá tu correo");
            etEmailLogin.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailLogin.setError("Correo electrónico inválido");
            etEmailLogin.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPasswordLogin.setError("Ingresá tu contraseña");
            etPasswordLogin.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "¡Bienvenido/a de nuevo!", Toast.LENGTH_SHORT).show();
                        irAMainActivity();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Fallo al iniciar sesión";
                        Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void irAMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Cierra el login para que no vuelva con el botón 'atrás'
    }
}