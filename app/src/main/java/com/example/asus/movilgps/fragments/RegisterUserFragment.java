package com.example.asus.movilgps.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.activities.Encuesta;
import com.example.asus.movilgps.models.usuario;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterUserFragment extends Fragment {

    private static final String TAG = "RegisterUserFragment";

    private View rootView;

    private Realm realm;
    private RealmResults<usuario> usuarios;

    @BindView(R.id.input_nombre)    EditText _nombre;
    @BindView(R.id.input_apellido) EditText _apellido;
    @BindView(R.id.input_telefono) EditText _telefono;
    @BindView(R.id.input_correo) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)   Button _signupButton;
    @BindView(R.id.link_login)    TextView _loginLink;


    public RegisterUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_register_user, container, false);
        ButterKnife.bind(this, rootView);
        //BD
        realm = Realm.getDefaultInstance();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        return rootView;
    }

    /** CRUD actions **/
    private void createNewUser(String nom, String ape, String tel, String user, String pass, String log) {
        realm.beginTransaction();
        usuario usuario = new usuario(nom, ape, tel, user, pass, log);
        realm.copyToRealm(usuario);
        realm.commitTransaction();
    }




    public void signUp() {
        Log.d(TAG, "Signup");

        if(!validate()){
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registrando...");
        progressDialog.show();

        final String nom = _nombre.getText().toString();
        final String ape = _apellido.getText().toString();
        final String user = _emailText.getText().toString();
        final String tel = _telefono.getText().toString();
        final String pass = _passwordText.getText().toString();
        final String login = "0";


        usuarios = realm.where(usuario.class).equalTo("correo", user).findAll();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(usuarios.size() ==0){
                            createNewUser(nom,ape,tel,user,pass,login);
                            progresSucess(user,pass);
                            Toast.makeText(getContext(), "Cargando..", Toast.LENGTH_SHORT).show();


                        }else{
                            _emailText.setError("Correo ya existe");
                            _signupButton.setEnabled(true);
                        }


                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void progresSucess(String user, String pass){
        Fragment login_fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString("email", user);
        args.putString("pass", pass);
        login_fragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, login_fragment);
        transaction.addToBackStack(null);

        // Commit a la transacción
        transaction.commit();
    }



    public void onSignupFailed() {
        Toast.makeText(getContext(), "Fallo inicio de sesión", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
            boolean valid = true;

        String name = _nombre.getText().toString();
        String apellido = _apellido.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _telefono.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nombre.setError("Minimo 3 letras");
            valid = false;
        } else {
            _nombre.setError(null);
        }

        if (apellido.isEmpty() || apellido.length() < 3) {
            _apellido.setError("Minimo 3 letras");
            valid = false;
        } else {
            _apellido.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Introduzca una dirección de correo electrónico válida");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=10) {
            _telefono.setError("Ingrese un numero de móvil valido");
            valid = false;
        } else {
            _telefono.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("No puede tener menos de 4 caracteres");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("La contraseña no coincide");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

}
