package com.example.controldetiempo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConfirmacionActivity extends AppCompatActivity {

    Button btnConfirmar;
    CheckBox checkBoxConfirmar;

    //FIRESTORE
    private static final String TAG="LoginActivity";
    private static final String KEY_LOGIN="login";
    private static final String KEY_CEDULA="cedula";
    private static final String KEY_FECHA="fecha";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreference
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Id = "idKey";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion);

        btnConfirmar=(Button)findViewById(R.id.btnConfirmacion);
        checkBoxConfirmar=(CheckBox)findViewById(R.id.checkBoxConfirmacion);

        //sharedpreferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        btnConfirmar.setOnTouchListener(new View.OnTouchListener() {
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

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkBoxConfirmar.isChecked()){

                    actualizarFirestore();

                }else
                {
                    Toast.makeText(ConfirmacionActivity.this,"Presione la casilla de confirmación",Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void actualizarFirestore(){

        String cedula=sharedpreferences.getString(Id,"");

        db.collection("Usuario").document(cedula).update(KEY_LOGIN, true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        registroAsistencia();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ConfirmacionActivity.this,"Error al registrar asistencia, verifique su conexión a Internet",Toast.LENGTH_LONG).show();
                        Log.d(TAG,e.toString());

                    }
                });
    }

    private void registroAsistencia(){

        Date currentTime = Calendar.getInstance().getTime();
        Long time= System.currentTimeMillis();

        String idRegistro=time.toString();

        final Map<String,Object> usuario=new HashMap<>();

        String cedula=sharedpreferences.getString(Id,"");

        usuario.put(KEY_CEDULA,cedula);
        usuario.put(KEY_FECHA,currentTime);





        db.collection("Registro").document(idRegistro).set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ConfirmacionActivity.this,"¡Asistencia Registrada!: ",Toast.LENGTH_LONG).show();

                        startActivity(new Intent(ConfirmacionActivity.this, MainActivity.class));


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ConfirmacionActivity.this,"Error al registrar asistencia",Toast.LENGTH_LONG).show();
                        Log.d(TAG,e.toString());

                    }
                });

    }
}
