package com.example.profesor.asteroides;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * Actividad especial (lista) que recoge el layout de puntuaciones.
 *
 * @author Enrique Morales Montero
 * @version 1.0.0
 */
public class Puntuaciones extends ListActivity {

    /**
     * Procedimiento cuyas instrucciones se ejecutan al crearse la actividad.
     *
     * @param savedInstanceState Bundle.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.puntuaciones);

        // Añadimos el adaptador creado.

        setListAdapter(new MiAdaptador(this, MainActivity.almacen.listaPuntuaciones(10)));
    }
}