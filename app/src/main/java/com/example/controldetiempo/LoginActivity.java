package com.example.controldetiempo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {

    Button btnLoginUsuario, btnRegistroUsuario;

    EditText txtCedulaUsuario, txtClaveUsuario;

    //FIRESTORE
    private static final String TAG="LoginActivity";
    private static final String KEY_TOKEN="token";
    private static final String KEY_CLAVE="clave";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreference
    public static final String MyPREFERENCES = "MyPrefs" ;
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
        setContentView(R.layout.activity_login);

        //TokenFirebase
        iniciarFirebase();

        btnLoginUsuario=(Button)findViewById(R.id.btnLoginUsuario);
        btnRegistroUsuario=(Button)findViewById(R.id.btnRegistroUsuario);

        txtCedulaUsuario=(EditText)findViewById(R.id.txtLoginCedula);
        txtClaveUsuario=(EditText)findViewById(R.id.txtloginClave);

        //SharedPreferences

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnLoginUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog.setMessage("Por favor, espere....");
                pDialog.setTitle("Iniciando Sesión");


                pDialog.setCancelable(true);
                showDialog();

                loginUsuario();



            }
        });

        btnRegistroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);

                startActivity(intent);

            }
        });

        btnLoginUsuario.setOnTouchListener(new View.OnTouchListener() {
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

        btnRegistroUsuario.setOnTouchListener(new View.OnTouchListener() {
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


        txtClaveUsuario.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (txtClaveUsuario.getRight() - txtClaveUsuario.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here




                        txtClaveUsuario.setTransformationMethod(HideReturnsTransformationMethod.getInstance());



                        return true;
                    }
                }
                return false;
            }
        });
    }



    private void loginUsuario(){


       // String email=txtCorreoUsuario.getText().toString();
        String cedula=txtCedulaUsuario.getText().toString();
        final String clave=txtClaveUsuario.getText().toString();



        db.collection("Usuario").document(cedula).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()){

                            //Map<String,Object> usuario=documentSnapshot.getData();

                            String clave_firebase=documentSnapshot.getString(KEY_CLAVE);

                            if(clave_firebase.equals(clave)){



                                try {


                                    SharedPreferences.Editor editor = sharedpreferences.edit();

                                    editor.putString(Id,txtCedulaUsuario.getText().toString());

                                    editor.commit();



                                } catch (Exception e) {


                                    e.printStackTrace();
                                }

                                try {


                                    SharedPreferences.Editor editor = sharedpreferences.edit();

                                    editor.putString(Token,tokenFirebase);

                                    editor.commit();

                                    actualizarToken();



                                } catch (Exception e) {


                                    e.printStackTrace();
                                }

                                hideDialog();

                                Toast.makeText(LoginActivity.this,"¡Bienvenido a Control de Tiempo!",Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);


                                startActivity(intent);



                            }else{
                                Toast.makeText(LoginActivity.this,"La clave es incorrecta",Toast.LENGTH_LONG).show();
                                hideDialog();
                            }





                        }
                        else{

                            Toast.makeText(LoginActivity.this,"No existe un usuario con la cédula ingresada.",Toast.LENGTH_LONG).show();
                            hideDialog();

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,"Error: Verifique su conexión a Internet",Toast.LENGTH_LONG).show();
                        Log.d(TAG,e.toString());
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

    private void actualizarToken(){
        db.collection("Usuario").document(txtCedulaUsuario.getText().toString()).update(KEY_TOKEN, tokenFirebase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                       // Toast.makeText(LoginActivity.this,"Error al registrar asistencia, verifique su conexión a Internet",Toast.LENGTH_LONG).show();
                        Log.d(TAG,e.toString());

                    }
                });
    }
}
