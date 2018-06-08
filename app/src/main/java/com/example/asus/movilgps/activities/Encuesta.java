package com.example.asus.movilgps.activities;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.movilgps.R;

public class Encuesta extends AppCompatActivity {

    private EditText Lat,Lon;
    private Button btn,atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta);

        Lat = (EditText) findViewById(R.id.editTextLatitud);
        Lon = (EditText) findViewById(R.id.editTextLongitud);

        btn = (Button) findViewById(R.id.btnListView);
        atras = (Button) findViewById(R.id.btnatras);

        String Latitude = getIntent().getStringExtra("latitud");
        String Longitude = getIntent().getStringExtra("longitud");

        Lat.setText(Latitude);
        Lon.setText(Longitude);

        Lon.setEnabled(false);
        Lat.setEnabled(false);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Encuesta.this, CargarUserActivity.class);
                startActivity(intent);
            }
        });
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Encuesta.super.onBackPressed();
            }
        });


    }
}
