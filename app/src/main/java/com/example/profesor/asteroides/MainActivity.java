package com.example.profesor.asteroides;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.prefs.Preferences;

/**
 * Actividad principal. Es un menú inicial.
 *
 * @author Enrique Morales Montero
 * @version 1.0.0
 */
public class MainActivity extends AppCompatActivity {

    public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionesArray();

    MediaPlayer media;

    /**
     * Procedimiento cuyas instrucciones se ejecutan al crearse la actividad.
     *
     * @param savedInstanceState Bundle.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        media = MediaPlayer.create(this, R.raw.audio);
        media.start();
    }

    // #######################################################################
    // MENÚ

    /**
     * Función cuyas instrucciones se ejecutan al crearse el menú.
     *
     * @param menu Menu
     * @return True / False
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);  // Inflamos. Usamos el recurso de menú.
        return true;                                        // true -> El menú ya está visible.
    }

    /**
     * Función que se ejecuta al seleccionar uno de los elementos del menú.
     *
     * @param item Elemento del menú.
     * @return True / False
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();          // Id del elemento seleccionado.

        if (id == R.id.action_settings) {   // "Preferencias" seleccionada. Se lanza.
            lanzarPreferencias(null);
            return true;
        }
        if (id == R.id.acercaDe) {          // "AcercaDe" seleccionado. Se lanza.
            lanzarAcercaDe(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Procedimiento del botón para empezar una NUEVA PARTIDA.
     *
     * @param view Vista
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    public void lanzarJuego(View view) {
        Intent i = new Intent(this, Juego.class);
        Toast.makeText(getApplicationContext(), "Abrió la ventana de JUEGO", Toast.LENGTH_LONG).show();
        startActivity(i);
    }

    /**
     * Procedimiento del botón para mostrar el ACERCADE.
     *
     * @param view Vista
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    public void lanzarAcercaDe(View view) {
        Intent i = new Intent(this, AcercaDeActivity.class);
        Toast.makeText(getApplicationContext(), "Abrió la ventana acercade", Toast.LENGTH_LONG).show();
        startActivity(i);
    }

    /**
     * Procedimiento para lanzar las PREFERENCIAS.
     *
     * @param view Vista
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    public void lanzarPreferencias(View view) {
        Intent i = new Intent(this, Preferencias.class);
        Toast.makeText(getApplicationContext(), "Abrió la ventana de preferencias", Toast.LENGTH_LONG).show();
        startActivity(i);
    }

    /**
     * Procedimiento para mostrar las preferencias.
     *
     * @param view Vista
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    public void mostrarPreferencias(View view){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String s = "música: " + pref.getBoolean("musica", true) + ", gráficos: " + pref.getString("graficos", "?");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    /**
     * Procedimiento del botón para mostrar las PUNTUACIONES.
     *
     * @param view Vista
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    public void lanzarPuntuaciones(View view) {
        Intent i = new Intent(this, Puntuaciones.class);
        startActivity(i);
    }

}