package libro.ejemplos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Añadir la navegación entre activdades aquí
        int id = item.getItemId();

        if (id == R.id.ejemplo_1) {
            Intent ejempo1 = new Intent(this, Ejemplo1.class);
            startActivity(ejempo1);
        } else if (id == R.id.ejemplo_3) {
            Intent ejempo3 = new Intent(this, Ejemplo3.class);
            startActivity(ejempo3);
        } else if (id == R.id.ejemplo_9) {
            Intent ejempo9 = new Intent(this, Ejemplo9.class);
            startActivity(ejempo9);
        } else if (id == R.id.ejemplo_10) {
            Intent ejempo10 = new Intent(this, Ejemplo10.class);
            startActivity(ejempo10);
        } else if (id == R.id.ejemplo_11) {
            Intent ejempo11 = new Intent(this, Ejemplo11.class);
            startActivity(ejempo11);
        } else if (id == R.id.ejemplo_12) {
            Intent ejempo12 = new Intent(this, Ejemplo12.class);
            startActivity(ejempo12);
        } else if (id == R.id.ejemplo_14) {
            Intent ejempo14 = new Intent(this, Ejemplo14.class);
            startActivity(ejempo14);
        } else if (id == R.id.ejemplo_21) {
            Intent ejempo21 = new Intent(this, Ejemplo21.class);
            startActivity(ejempo21);
        } else if (id == R.id.ejemplo_22) {
            Intent ejempo22 = new Intent(this, Ejemplo22.class);
            startActivity(ejempo22);
        } else if (id == R.id.ejemplo_23) {
            Intent ejempo22 = new Intent(this, Ejemplo23.class);
            startActivity(ejempo22);
        } else if (id == R.id.ejemplo_24) {
            Intent ejempo22 = new Intent(this, Ejemplo24.class);
            startActivity(ejempo22);
        } else if (id == R.id.ejemplo_25) {
            Intent ejempo22 = new Intent(this, Ejemplo25.class);
            startActivity(ejempo22);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
