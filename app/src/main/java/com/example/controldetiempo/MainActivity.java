package com.example.controldetiempo;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {


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
}
