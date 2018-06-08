package com.example.asus.movilgps.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.activities.CargarUserActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class EncuestaFragment extends Fragment {

    private EditText Lat,Lon;
    private Button btn,atras;

    public EncuestaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_encuesta, container, false);

        Lat = rootView.findViewById(R.id.editLatitud);
        Lon = rootView.findViewById(R.id.editLongitud);
        btn = rootView.findViewById(R.id.btnEnviar);

        //Datos pasados por fragment
        String latitud = getArguments() != null ? getArguments().getString("latitud") : "";
        String longitud = getArguments() != null ? getArguments().getString("longitud") : "";

        Lat.setText(latitud);
        Lon.setText(longitud);
        Lon.setEnabled(false);
        Lat.setEnabled(false);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CargarUserActivity.class);  // para dirigirnos de la clase donde stamos a la otra
                startActivity(intent);
            }
        });

        return rootView;
    }

}
