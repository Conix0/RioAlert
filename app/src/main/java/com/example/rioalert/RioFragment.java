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

    private TextView fecha, agua;
    private LottieAnimationView animationView;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference referenceEntrada3 = database.getReference("LECTURAS");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rio, container, false);

        agua = view.findViewById(R.id.agua);
        fecha = view.findViewById(R.id.fecha);
        animationView = view.findViewById(R.id.animationView);

        referenceEntrada3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object valorAgua = snapshot.child("agua").getValue();
                    Object valorFecha = snapshot.child("fecha").getValue();

                    if (valorAgua != null) {
                        agua.setText(valorAgua.toString());
                    }

                    if (valorFecha != null) {
                        fecha.setText(valorFecha.toString());
                    }

                    if (animationView != null) {
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