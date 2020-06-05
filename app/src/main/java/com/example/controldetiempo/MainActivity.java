package com.example.controldetiempo;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnGenerate;
    private ImageView imgResult;
    public Bitmap qrImage;
    private ProgressBar loader;

    //MENU
    private ViewPager mViewPager;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mToggle;

    TextView tituloPas;
    TextView usuarioPas;

    //SharedPreference

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Id = "idKey";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnGenerate = (Button)findViewById(R.id.btnGenerarqr);

        imgResult   = (ImageView)findViewById(R.id.imageViewQR);

        //SharedPreferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

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
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                //intent.putExtras(basket);
                                startActivity(intent);



                                return true;

                            case R.id.perfil:
                                menuItem.setChecked(true);

                                Toast.makeText(MainActivity.this, "Pantalla: " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
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
}
