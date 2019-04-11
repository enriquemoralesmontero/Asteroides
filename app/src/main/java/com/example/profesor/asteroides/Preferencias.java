package com.example.profesor.asteroides;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Actividad especial diseñada específicamente para mostrar preferencias del usuario.
 *
 * @author Enrique Morales Montero
 * @version 1.0.0
 */
public class Preferencias extends PreferenceActivity {

    /**
     * Procedimiento cuyas instrucciones se ejecutan al crearse la actividad.
     *
     * @param savedInstanceState Bundle
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
    }
}