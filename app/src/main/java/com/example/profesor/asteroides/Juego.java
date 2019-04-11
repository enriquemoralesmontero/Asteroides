package com.example.profesor.asteroides;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

/**
 * Actividad que carga el layout del juego.
 *
 * @author Enrique Morales Montero
 * @version 1.0.0
 */
public class Juego extends Activity {

    private VistaJuego vistaJuego;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

        vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        vistaJuego.setNumFragmentos(Integer.parseInt(pref.getString("fragmentos", "3")));
    }

    @Override protected void onPause() {
        super.onPause();
        vistaJuego.getThread().pausar();
    }

    @Override protected void onResume() {
        super.onResume();
        vistaJuego.getThread().reanudar();
    }

    @Override protected void onDestroy() {
        vistaJuego.getThread().detener();
        super.onDestroy();
    }

}