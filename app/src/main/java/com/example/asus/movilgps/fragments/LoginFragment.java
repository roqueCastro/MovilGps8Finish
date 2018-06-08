package com.example.asus.movilgps.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.movilgps.BuildConfig;
import com.example.asus.movilgps.Globals.Globals;
import com.example.asus.movilgps.R;
import com.example.asus.movilgps.app.MyApplication;
import com.example.asus.movilgps.models.usuario;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private static final int REQUEST_SIGNUP = 0;


    private static  final String PREFS_KEY = "LoginFragment";
    private static  final String ESTADO_BOTON = "estado_boton";

    SharedPreferences preferences;
    String email;

    private Realm realm;
    private RealmResults<usuario> usuarios1, usuariosLog;
    private Globals g;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView (R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup)  TextView _singupLink;

    private View rootView;
    String ms="";

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);

        // Estado de boton
        SharedPreferences prefe=getActivity().getSharedPreferences("datos",Context.MODE_PRIVATE);
        ms =  prefe.getString("login","");

        //BD
        realm = Realm.getDefaultInstance();
        // Variable G
        g = Globals.getIntanse();


        if(ms==""){

        }else{
            usuariosLog = realm.where(usuario.class).equalTo("correo", ms).findAll();


            for(usuario usuario:usuariosLog){
                String cod = usuario.getLogin();
                int id = usuario.getId();
                String codi="1";
                if(codi.equals(cod)){

                    g.setCoUser(ms);
                    Fragment bienv_Fragment = new WelcomeFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, bienv_Fragment);
                    transaction.addToBackStack(null);

                    // Commit a la transacción
                    transaction.commit();
                }
            }
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        _singupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment register_User_Fragment = new RegisterUserFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, register_User_Fragment);
                transaction.addToBackStack(null);

                // Commit a la transacción
                transaction.commit();
            }
        });

        //Datos pasados por fragment
        String email = getArguments() != null ? getArguments().getString("email") : "";
        String pas = getArguments() != null ? getArguments().getString("pass") : "";

        //Escribimos el valor del email guardado anteriormente, si existe dicho valor

        _emailText.setText(email);
        _passwordText.setText(pas);

        return rootView;
    }

     /* CRUD ACTIONS */

    private void  updateUserSele(String login, usuario usuario){
        realm.beginTransaction();

        usuario.setLogin(login);
        realm.copyToRealmOrUpdate(usuario);

        realm.commitTransaction();
    }


    public  void login() {
        Log.d(TAG, "INICIAR SESIÓN");

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        usuarios1= realm.where(usuario.class).equalTo("correo", email).findAll();

        if (!validate()){
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
        progressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed

                            if(usuarios1.size()==0){
                                _emailText.setError("Correo incorrecto");
                                _loginButton.setEnabled(true);
                            }else {
                                _emailText.setError(null);
                                for (usuario usuario : usuarios1) {
                                    if(password.equals(usuario.getPass())){
                                        _passwordText.setError(null);

                                        onLoginSuccess();
                                        g.setCoUser(email);

                                        SharedPreferences preferencias=getActivity().getSharedPreferences("datos",Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor=preferencias.edit();
                                        editor.putString("login", email);
                                        editor.commit();

                                        String log = "1";
                                        updateUserSele(log, usuario);

                                    }else{
                                        _passwordText.setError("Contraseña incorrecta");
                                        _loginButton.setEnabled(true);
                                    }

                                }
                            }

                            progressDialog.dismiss();

                        }
                    }, 3000);
    }

    public void onLoginSuccess() {

        Fragment welcome_fragment = new WelcomeFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, welcome_fragment);
        transaction.addToBackStack(null);

        // Commit a la transacción
        transaction.commit();
    }

    public void onLoginFailed() {
        Toast.makeText(getContext(), "No esta entrando a la bd", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Introduzca una dirección de correo electrónico válida");
            valid=false;
        } else {
            _emailText.setError(null);
        }

        if(password.isEmpty() || password.length() == 0) {
            _passwordText.setError("Ingresa caracteres");
            valid=false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}
