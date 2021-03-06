package com.example.controldetiempo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnGenerate, btnIngresarCodigo;
    private ImageView imgResult;
    public Bitmap qrImage;
    private ProgressBar loader;

    private static final String TAG="MainActivity";
    private static final String KEY_LOGIN="login";
    private static final String KEY_OTP="otp";


    private static final String KEY_CEDULA="cedula";
    private static final String KEY_FECHA="fecha";

    //MENU
    private ViewPager mViewPager;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mToggle;

    TextView tituloPas;
    TextView usuarioPas;

    //Popup

    Dialog myDialog;

    //Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreference

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Id = "idKey";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnGenerate = (Button)findViewById(R.id.btnGenerarqr);

        btnIngresarCodigo=(Button)findViewById(R.id.btnIngresarCodigo);

        imgResult   = (ImageView)findViewById(R.id.imageViewQR);

        //SharedPreferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //Popup
        myDialog = new Dialog(this);

        //MENU

        //Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.abrir, R.string.cerrar);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Navigation View



        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);


        tituloPas = (TextView) headerView.findViewById(R.id.menuTitulo);
        usuarioPas = (TextView) headerView.findViewById(R.id.menuUsuario);

        if(sharedpreferences.getString(Id,"")==""){

            tituloPas.setText("TODAVÍA NO SE HA REGISTRADO");
            usuarioPas.setText("Elija la opción Registrarse ");

        }else{
            tituloPas.setText("BIENVENIDO");
            usuarioPas.setText(sharedpreferences.getString(Id,""));

        }


        setupNavigationDrawerContent(navigationView);

        btnGenerate.setOnTouchListener(new View.OnTouchListener() {
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

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                generarImagen();

            }
        });

        btnIngresarCodigo.setOnTouchListener(new View.OnTouchListener() {
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

        btnIngresarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresarCodigo();
            }
        });





    }



    //MENU



    public String getValuePreference(){

        return sharedpreferences.getString(Id,"");
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {



                        switch (menuItem.getItemId()) {
                            case R.id.inicio:
                                menuItem.setChecked(true);
                                Toast.makeText(MainActivity.this, "Pantalla: " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                mDrawerLayout.closeDrawer(GravityCompat.START);

                              /*
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                //intent.putExtras(basket);
                                startActivity(intent);
                            */


                                return true;

                            case R.id.perfil:
                                menuItem.setChecked(true);

                                Toast.makeText(MainActivity.this, "Pantalla: " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent2 = new Intent(MainActivity.this, PerfilActivity.class);
                                //intent.putExtras(basket);
                                startActivity(intent2);


                                return true;

                            case R.id.logout:
                                menuItem.setChecked(true);

                                Toast.makeText(MainActivity.this, "Cerrando Sesión..." , Toast.LENGTH_SHORT).show();
                                mDrawerLayout.closeDrawer(GravityCompat.START);

                                try {


                                    SharedPreferences.Editor editor3 = sharedpreferences.edit();

                                    editor3.putString(Id,"");

                                    editor3.commit();



                                } catch (Exception e) {


                                    e.printStackTrace();
                                }

                                try {


                                    SharedPreferences.Editor editor = sharedpreferences.edit();

                                    editor.putString(Id,"");

                                    editor.commit();



                                } catch (Exception e) {


                                    e.printStackTrace();
                                }

                                Intent intent4 = new Intent(MainActivity.this,LoginActivity.class);
                                //intent.putExtras(basket);
                                startActivity(intent4);

                                finish();


                                return true;


                        }
                        return true;





                    }
                });
    }

    private void generarImagen(){

        btnGenerate.setText("VOLVER A GENERAR QR");
        String cedula=sharedpreferences.getString(Id,"");



        //final String text = txtQRText.getText().toString();
        Long time= System.currentTimeMillis();
        final String text =cedula+"@"+time.toString();
      /*  if(text.trim().isEmpty()){
            alert("Ketik dulu data yang ingin dibuat QR Code");
            return;
        }
        */


        endEditing();
        showLoadingVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = imgResult.getMeasuredWidth();
                if( size > 1){
                   // Log.e(tag, "size is set manually");
                    size = 260;
                }

                Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
                hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                hintMap.put(EncodeHintType.MARGIN, 1);
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                try {
                    BitMatrix byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size,
                            size, hintMap);
                    int height = byteMatrix.getHeight();
                    int width = byteMatrix.getWidth();
                   qrImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++){
                        for (int y = 0; y < height; y++){
                            qrImage.setPixel(x, y, byteMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showImage(qrImage);
                            showLoadingVisible(false);
                            //this.snackbar("QRCode telah dibuat");
                        }
                    });
                } catch (WriterException e) {
                    e.printStackTrace();
                   Log.d("ErrorMain",e.getMessage());
                }
            }
        }).start();
    }

    private void showLoadingVisible(boolean visible){
        if(visible){
            showImage(null);
        }

       /* loader.setVisibility(
                (visible) ? View.VISIBLE : View.GONE
        );
        */

    }

    public void showImage(Bitmap bitmap) {
        if (bitmap == null) {
            imgResult.setImageResource(android.R.color.transparent);
            qrImage = null;
            //txtSaveHint.setVisibility(View.GONE);
        } else {
            imgResult.setImageBitmap(bitmap);
            //txtSaveHint.setVisibility(View.VISIBLE);
        }
    }

    private void endEditing(){
        //txtQRText.clearFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        // imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }



    private void ingresarCodigo(){


        Toast.makeText(MainActivity.this, "Ingrese el código de la Tablet", Toast.LENGTH_SHORT).show();


        Button btnAceptarPopupEmp;
        ImageButton btnCancelarPopupEmp;

        final EditText filtroCodigo;

        myDialog.setContentView(R.layout.popup_codigo);


        btnAceptarPopupEmp = (Button)myDialog.findViewById(R.id.btnPopupEmpAceptar);
        btnCancelarPopupEmp = (ImageButton)myDialog.findViewById(R.id.btnPopupEmpCancelar);
        filtroCodigo=(EditText)myDialog.findViewById(R.id.filtroCodigo);

        btnAceptarPopupEmp.setOnTouchListener(new View.OnTouchListener() {
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

        btnCancelarPopupEmp.setOnTouchListener(new View.OnTouchListener() {
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

        btnCancelarPopupEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        btnAceptarPopupEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String codigoIngresado=filtroCodigo.getText().toString();

                final String cedula=sharedpreferences.getString(Id,"");

                db.collection("Usuario").document(cedula).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists()){

                                    //Map<String,Object> usuario=documentSnapshot.getData();

                                    String otp_firebase=documentSnapshot.getString(KEY_OTP);

                                    if(otp_firebase.equals(codigoIngresado)){


                                        db.collection("Usuario").document(cedula).update(KEY_LOGIN, true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        registroAsistencia();


                                                        myDialog.dismiss();


                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        Toast.makeText(MainActivity.this,"Error al registrar asistencia, verifique su conexión a Internet",Toast.LENGTH_LONG).show();
                                                        Log.d(TAG,e.toString());

                                                    }
                                                });


                                    }else{
                                        Toast.makeText(MainActivity.this,"El código es Incorrecto",Toast.LENGTH_LONG).show();

                                    }

                                }
                                else{

                                    Toast.makeText(MainActivity.this,"Error, verifique su conexión a Internet",Toast.LENGTH_LONG).show();


                                }


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Error: Verifique su conexión a Internet",Toast.LENGTH_LONG).show();
                                Log.d(TAG,e.toString());

                            }
                        });






            }
        });



        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }


    private void registroAsistencia(){

        Date currentTime = Calendar.getInstance().getTime();
        Long time= System.currentTimeMillis();

        String idRegistro=time.toString();

        final  Map<String,Object> usuario=new HashMap<>();

        String cedula=sharedpreferences.getString(Id,"");

        usuario.put(KEY_CEDULA,cedula);
        usuario.put(KEY_FECHA,currentTime);





        db.collection("Registro").document(idRegistro).set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this,"¡Asistencia Registrada!: ",Toast.LENGTH_LONG).show();



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this,"Error al registrar asistencia",Toast.LENGTH_LONG).show();
                        Log.d(TAG,e.toString());

                    }
                });

    }
}
