package com.example.asus.movilgps.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.asus.movilgps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMinZoomPreference(10); // Escala minima 10 ZOOM
        mMap.setMaxZoomPreference(17); // Escala maxima 17 ZOOM

        // Add a marker in Sydney and move the camera
        LatLng Yamboro = new LatLng(1.892269995633687, -76.09405769394527);
        mMap.addMarker(new MarkerOptions().position(Yamboro).title("Hola Yamboro").draggable(true));

        CameraPosition camera = new CameraPosition.Builder()
                .target(Yamboro)
                .zoom(10) // VISTA DE VIAS MAX 20
                .bearing(90) //POSITION CAMARA 90° MAX 365°
                .tilt(30)  // TILT DE LA CAMARA PARA VER EFECTO 3D 30° MAX 90°
                .build();


        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(Yamboro));

              /* EVENTS MAPS */



        // --↓-- Cuando damos un click normal
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this,"Click On: \n" +
                        "Lat: "+latLng.latitude +" \n" +
                        "Lon: "+latLng.longitude , Toast.LENGTH_SHORT).show();
            }
        });

        //  -----------↓------------- Cuando damos un click mas largo
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this,"Long Click On: \n" +
                        "Lat: "+latLng.latitude +" \n" +
                        "Lon: "+latLng.longitude , Toast.LENGTH_SHORT).show();
            }
        });

        //  ------↓----------  Esto es para arrastrar los marcadores
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(MapsActivity.this,"Marker dragged to:  \n" +
                        "Lat: " + marker.getPosition().latitude +" \n" +
                        "Lon: " + marker.getPosition().longitude , Toast.LENGTH_SHORT).show();
            }
        });


    }
}
