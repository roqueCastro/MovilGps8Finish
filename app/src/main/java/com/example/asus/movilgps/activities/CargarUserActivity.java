package com.example.asus.movilgps.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.asus.movilgps.Globals.Globals;
import com.example.asus.movilgps.R;
import com.example.asus.movilgps.adapters.UsuarioAdapter;
import com.example.asus.movilgps.fragments.WelcomeFragment;
import com.example.asus.movilgps.models.usuario;
import com.google.android.gms.maps.MapFragment;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CargarUserActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<usuario>>, AdapterView.OnItemClickListener  {

    android.app.FragmentManager mapFrag = getFragmentManager();
    private Realm realm;

    private ListView listView;
    private UsuarioAdapter adapter;

    private RealmResults<usuario> usuarios, usuariosLo;
    private Globals g;

    private Button btnM,btnLog;
    private FloatingActionButton fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_user);

        //BD
        realm = Realm.getDefaultInstance();
        usuarios= realm.where(usuario.class).findAll();
        usuarios.addChangeListener(this); //refresh
        g = Globals.getIntanse();

        adapter= new UsuarioAdapter(this, usuarios, R.layout.list_view_user_item);
        listView = (ListView) findViewById(R.id.listViewUser);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        btnM = (Button) findViewById(R.id.user_btn_map);
        btnLog = (Button) findViewById(R.id.user_btn_Login);

        fa = (FloatingActionButton) findViewById(R.id.FabClose);


                Toast.makeText(getApplicationContext(), g.getCoUser(), Toast.LENGTH_SHORT).show();

        btnM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarUserActivity.super.onBackPressed();
            }
        });
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        String Cor = g.getCoUser();

        usuariosLo = realm.where(usuario.class).equalTo("correo", Cor).findAll();

        fa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (usuario usuario:usuariosLo){
                    showAlertConfirm("Confirmacion", "¿Seguro que quieres cerrar sesión?", usuario);
                }
            }
        });


    }


    /* ALERT */

    private void showAlertConfirm(String title, String message, final usuario usuario) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String log = "0";
                cerrar_sesion(log, usuario);
                onLoginSuccess();

            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void onLoginSuccess(){
        Intent i = new Intent(CargarUserActivity.this, MainActivity.class);
        startActivity(i);
    }


    /* CRUD ACTIONS */

    private void deleteUser(usuario usuario) {

        realm.beginTransaction();
        usuario.deleteFromRealm();
        realm.commitTransaction();

    }

    private void  updateUserSele(String nomb, String pass, usuario usuario){
        realm.beginTransaction();

        usuario.setNombre(nomb);
        usuario.setPass(pass);
        realm.copyToRealmOrUpdate(usuario);

        realm.commitTransaction();
    }


    private void  cerrar_sesion(String log, usuario usuario){
        realm.beginTransaction();

        usuario.setLogin(log);
        realm.copyToRealmOrUpdate(usuario);

        realm.commitTransaction();
    }


    /* DIALOGS */

    private void showAlertPrincipal(String title, final usuario usuario) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_user_principal, null);
        builder.setView(viewInflated);

        final AlertDialog dialog = builder.create();


        final Button btnAgregarNota = (Button) viewInflated.findViewById(R.id.btn_board_add_note);
        final Button btnDeleteBoard = (Button) viewInflated.findViewById(R.id.btn_user_delete);
        final Button btnUpdateBoard = (Button) viewInflated.findViewById(R.id.btn_user_update);

        btnAgregarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Perfil",Toast.LENGTH_SHORT).show();

                dialog.cancel();
            }
        });

        btnDeleteBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Eliminando Usuario",Toast.LENGTH_SHORT).show();
                deleteUser(usuario);
                dialog.cancel();
            }
        });

        btnUpdateBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForEditingBoard("Cambiar contraseña", "Nueva contraseña", usuario);
                dialog.cancel();
            }
        });


        dialog.show();

    }

    private void showAlertForEditingBoard(String title, String message, final usuario usuario) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_update_user, null);
        builder.setView(viewInflated);

        final EditText inputN = (EditText) viewInflated.findViewById(R.id.editTextNewName);
        final EditText inputP = (EditText) viewInflated.findViewById(R.id.editTextNewPass);
        inputN.setText(usuario.getNombre());
        inputP.setText(usuario.getPass());

        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nameNew = inputN.getText().toString().trim();
                String passNew = inputP.getText().toString().trim();

                if (nameNew.length() == 0)
                    Toast.makeText(getApplicationContext(), "Ingresa un nombre", Toast.LENGTH_LONG).show();

                else if (nameNew.equals(usuario.getNombre()))
                    Toast.makeText(getApplicationContext(), "No se puede cambiar por el mismo", Toast.LENGTH_LONG).show();

                else if (passNew.length() == 0)
                    Toast.makeText(getApplicationContext(), "Ingresa una contraseña", Toast.LENGTH_LONG).show();

                else if (passNew.equals(usuario.getPass()))
                    Toast.makeText(getApplicationContext(), "No se puede cambiar por la misma", Toast.LENGTH_LONG).show();

                else
                    updateUserSele(nameNew, passNew, usuario);

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, null);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.cerrarSesion:
                Toast.makeText(getApplicationContext(), "cerrar.. ", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onChange(RealmResults<usuario> element) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "ID ->" + id, Toast.LENGTH_SHORT).show();

        showAlertPrincipal("Panel control", usuarios.get(position));

    }
}
