package com.example.asus.movilgps.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.activities.Encuesta;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, LocationListener {

    private View rootView;
    private MapView mapView;
    private GoogleMap gMap;

    private LocationManager locationManager;
    private Location currentLocation;
    private Marker marker;

    private CameraPosition camera;

    private Geocoder geocoder;  // recoge datos Lat, Long
    private List<Address> addresses; // todos los datos de la app

    private FloatingActionButton fab;
    private Button carMap;

    private int valorGuardado,msg,ultimo; // Creas tu variable global en espera de los valores


    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ESTO ES PARA CUANDO LE DA EN CLICK EN go map LE INFLE LA VISTA DEL MAPA
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        carMap = (Button) rootView.findViewById(R.id.cargar_map);
        carMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment Actualizar = new MapFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, Actualizar);
                transaction.addToBackStack(null);

                // Commit a la transacción
                transaction.commit();
            }
        });

        fab = (FloatingActionButton) rootView.findViewById(R.id.FabGps);
        fab.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ESTO ES POR SI EL FRAGMENT ESTA VACIO LO LLENE CON EL MAPA
        mapView = (MapView) rootView.findViewById(R.id.fragment_map);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);



        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
            return;
        }
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);

/*
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getContext(), "Click", Toast.LENGTH_SHORT);
            }
        });

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 11000, 1, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);

*/

    }

    // si esta activo o no el gps
    private boolean isGPSEnabled() {
        try {
            int gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);

            if (gpsSignal == 0) {
                // El Gps no esta activado
                return false;
            } else {
                return true;
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showInfoAlert() {
        new AlertDialog.Builder(getContext())
                .setTitle("ACTIVAR GPS ")
                .setMessage("EL GPS esta desactivado")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }


    @Override
    public void onClick(View v) {
        if (!this.isGPSEnabled()) {
            showInfoAlert();
        } else {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext() , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestP
                //
                // ermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }


            currentLocation = location;

            if(currentLocation != null) {
                createOrUpdateMarkerByLocation(location);
                //zoomToLocation(location);
//                Toast.makeText(getContext(), "coordenadas...." , Toast.LENGTH_SHORT).show();

                if(valorGuardado == 0){
                    valorGuardado=1;
                    Toast.makeText(getContext(), "Te falta 2 t", Toast.LENGTH_SHORT).show();
                    zoomToLocation3(location);
                }else{
                    valorGuardado = 2;

                    if(msg == 0) {
                        zoomToLocation2(location);
                        Toast.makeText(getContext(), "Te falta 1 t", Toast.LENGTH_SHORT).show();
                        msg=1;
                    }else{
                        msg = 2;

                        if(ultimo == 0){
                            zoomToLocation1(location);
                            Toast.makeText(getContext(), "Listo para la encuesta", Toast.LENGTH_SHORT).show();
                            ultimo = 1;
                        }else{
                            ultimo = 2;
                        }

                    }
                }

                if (ultimo == 2){
                    showAlertMapLocation(location);
                }

            }

        }
    }

    /// Creamos el marker con la location
    private void createOrUpdateMarkerByLocation(Location location) {
        // actializa o crea el marcador en la posicion ..
        if(marker == null) {
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).draggable(true));
        }else {
            marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

        }
    }

    private void zoomToLocation3 (Location location) {
        camera = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(14) // VISTA DE VIAS MAX 20
                .bearing(0) //POSITION CAMARA 90° MAX 365°
                .tilt(30)  // TILT DE LA CAMARA PARA VER EFECTO 3D 30° MAX 90°
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }
    private void zoomToLocation2 (Location location) {
        camera = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(18) // VISTA DE VIAS MAX 20
                .bearing(0) //POSITION CAMARA 90° MAX 365°
                .tilt(30)  // TILT DE LA CAMARA PARA VER EFECTO 3D 30° MAX 90°
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }
    private void zoomToLocation1 (Location location) {
        camera = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(20) // VISTA DE VIAS MAX 20
                .bearing(0) //POSITION CAMARA 90° MAX 365°
                .tilt(50)  // TILT DE LA CAMARA PARA VER EFECTO 3D 30° MAX 90°
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }

    /* Alert Dialog */
    private void showAlertMapLocation(final Location location) {
        new AlertDialog.Builder(getContext())
                .setTitle("COORDENADAS ")
                .setMessage("Ir a la encuesta")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());
                        /*Intent intent = new Intent(getActivity(), Encuesta.class);  // para dirigirnos de la clase donde stamos a la otra
                        intent.putExtra("latitud", latitude);
                        intent.putExtra("longitud", logitude);
                        startActivity(intent);*/
                        Fragment frag_Encuesta = new EncuestaFragment();
                        Bundle args = new Bundle();
                        args.putString("latitud", latitude);
                        args.putString("longitud", longitude);
                        frag_Encuesta.setArguments(args);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, frag_Encuesta);
                        transaction.addToBackStack(null);

                        // Commit a la transacción
                        transaction.commit();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }




    @Override
    public void onLocationChanged(Location location) {
          createOrUpdateMarkerByLocation(location);
/*        Toast.makeText(getContext(), "LOCATION LIVE::.. "+ "\n" +
                "Latitud: -> " + location.getLatitude()+ "\n" +
                "Longitud: -> " + location.getLongitude(),Toast.LENGTH_SHORT).show();*/

            Snackbar.make(getView(),"Latitud: " + location.getLatitude()+ "\n" +
                    "Longitud: " + location.getLongitude(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}

