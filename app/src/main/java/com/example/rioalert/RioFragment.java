package com.example.rioalert;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RioFragment extends Fragment {

    private TextView agua, fecha, tvNivelGrande, tvHoraActualizado, tvEstadoBadge;
    private LottieAnimationView animationView;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference referenceEntrada3 = database.getReference("LECTURAS");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rio, container, false);

        // Vincular componentes
        agua = view.findViewById(R.id.agua);
        fecha = view.findViewById(R.id.fecha);
        tvNivelGrande = view.findViewById(R.id.tvNivelGrande);
        tvHoraActualizado = view.findViewById(R.id.tvHoraActualizado);
        tvEstadoBadge = view.findViewById(R.id.tvEstadoBadge);
        animationView = view.findViewById(R.id.animationView);

        // Escuchar datos de Firebase
        referenceEntrada3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object valorAgua = snapshot.child("agua").getValue();
                    Object valorFecha = snapshot.child("fecha").getValue();

                    if (valorAgua != null) {
                        String nivelStr = valorAgua.toString();
                        // Actualiza tanto el encabezado grande como la tarjeta
                        tvNivelGrande.setText(nivelStr + " cm");
                        agua.setText(nivelStr + " cm");

                        try {
                            double nivel = Double.parseDouble(nivelStr);
                            if (nivel < 10.0) { // Umbral de alerta
                                tvEstadoBadge.setText("¡Alerta!");
                                tvEstadoBadge.setBackgroundColor(0xFFFF4444); // Rojo
                            } else {
                                tvEstadoBadge.setText("Normal");
                                tvEstadoBadge.setBackgroundColor(0xFF00B074); // Verde
                            }
                        } catch (NumberFormatException ignored) {}
                    }

                    if (valorFecha != null) {
                        String fechaStr = valorFecha.toString();
                        fecha.setText(fechaStr);
                        tvHoraActualizado.setText(fechaStr);
                    }

                    if (animationView != null && !animationView.isAnimating()) {
                        animationView.playAnimation();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return view;
    }
}