package com.example.profesor.asteroides;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Actividad que lanza el layout Acercade.
 *
 * @author Enrique Morales Montero
 * @version 1.0.0
 */
public class AcercaDeActivity extends Activity {

    /**
     * Procedimiento cuyas instrucciones se ejecutan al crearse la actividad.
     *
     * @param savedInstanceState Bundle
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acercade);
    }
}
