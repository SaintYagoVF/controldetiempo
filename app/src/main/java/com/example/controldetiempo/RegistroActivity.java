package com.example.controldetiempo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    EditText txtNombre, txtClave, txtEmail, txtCedula;

    Button btnAceptar;
    private static final String TAG="RegistroDistribuidor";
    private static final String KEY_NOMBRE="nombre";
    private static final String KEY_EMAIL="email";
    private static final String KEY_CEDULA="cedula";
    private static final String KEY_CLAVE="clave";
    private static final String KEY_TOKEN="token";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Shared
    //SharedPreference
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Rol = "rolKey";
    public static final String Token = "tokenKey";
    public static final String Id = "idKey";
    SharedPreferences sharedpreferences;

    //ProgressDialog
    private ProgressDialog pDialog;

    //TokenFirebase
    private static String tokenFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //TokenFirebase
        iniciarFirebase();


        //SharedPreferences

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        txtNombre=(EditText)findViewById(R.id.txtUsuarioRegistro);
        txtClave=(EditText)findViewById(R.id.txtClaveRegistro);
        txtCedula=(EditText)findViewById(R.id.txtCedulaRegistro);
        txtEmail=(EditText)findViewById(R.id.txtEmailRegistro);

        btnAceptar=(Button)findViewById(R.id.btnDistribuidorAceptar);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog.setMessage("Por favor, espere....");
                pDialog.setTitle("Registrando");


                pDialog.setCancelable(true);
                showDialog();

                registroUsuario();
            }
        });


        btnAceptar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.getBackground().setAlpha(150);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.getBackground().setAlpha(255);
                }
                return false;
            }
        });

        txtClave.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (txtClave.getRight() - txtClave.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here




                        txtClave.setTransformationMethod(HideReturnsTransformationMethod.getInstance());



                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void registroUsuario(){

        String nombre=txtNombre.getText().toString();
        final String email=txtEmail.getText().toString();
        final String cedula=txtCedula.getText().toString();
        String clave=txtClave.getText().toString();



        final  Map<String,Object> usuario=new HashMap<>();

        usuario.put(KEY_NOMBRE,nombre);
        usuario.put(KEY_EMAIL,email);
        usuario.put(KEY_CEDULA,cedula);
        usuario.put(KEY_CLAVE,clave);
        usuario.put(KEY_TOKEN,tokenFirebase);

        //db.document("Usuarios/"+email);



        db.collection("Usuario").document(cedula).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()){

                            Toast.makeText(RegistroActivity.this,"Este usuario ya se ha registrado anteriormente",Toast.LENGTH_LONG).show();

                            hideDialog();

                        }
                        else{



                            db.collection("Usuario").document(cedula).set(usuario)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegistroActivity.this,"¡Se ha registrado con éxito!",Toast.LENGTH_LONG).show();

                                            try {


                                                SharedPreferences.Editor editor3 = sharedpreferences.edit();

                                                editor3.putString(Rol,"Usuario");

                                                editor3.commit();



                                            } catch (Exception e) {


                                                e.printStackTrace();
                                            }

                                            try {


                                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                                editor.putString(Id,txtCedula.getText().toString());

                                                editor.commit();



                                            } catch (Exception e) {


                                                e.printStackTrace();
                                            }
                                            try {


                                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                                editor.putString(Token,tokenFirebase);

                                                editor.commit();



                                            } catch (Exception e) {


                                                e.printStackTrace();
                                            }

                                            hideDialog();

                                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);


                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(RegistroActivity.this,"Error al registrar usuario",Toast.LENGTH_LONG).show();
                                            Log.d(TAG,e.toString());
                                            hideDialog();

                                        }
                                    });

                            hideDialog();

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistroActivity.this,"Error: verifique su conexión a internet",Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                });



    }


    //Progress Dialog
    //Show Dialog
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //Hide Dialog
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void iniciarFirebase(){



        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        tokenFirebase=token;

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG,"Token de dispositivo es: "+ tokenFirebase);
                        //Toast.makeText(LoginActivity.this, tokenFirebase, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
