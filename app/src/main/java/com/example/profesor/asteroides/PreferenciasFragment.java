package com.example.profesor.asteroides;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

/**
 * Fragment especial especializado en mostrar las preferencias de la aplicación.
 *
 * @author Enrique Morales Montero
 * @version 1.0.0
 */
public class PreferenciasFragment extends PreferenceFragment {

    /**
     * Procedimiento cuyas instrucciones se ejecutan al crearse la actividad.
     *
     * @param savedInstanceState Bundle.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ponemos como preferencias por defecto las de los recursos XML de preferencias.

        addPreferencesFromResource(R.xml.preferencias);
        final EditTextPreference fragmentos = (EditTextPreference) findPreference("fragmentos");

        // Vamos a controlar que los usuarios introducen bien el número de fragmentos con un listener de cambios de preferencias.

        fragmentos.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                int valor;

                // Controlamos que ponga sólo números.

                try {
                    valor = Integer.parseInt((String)newValue);
                } catch(Exception e) {
                    Toast.makeText(getActivity(), "Ha de ser un número", Toast.LENGTH_SHORT).show();
                    return false;
                }

                // Controlamos que sea entre 0 y 9.

                if (valor>=0 && valor<=9) {
                    fragmentos.setSummary("En cuantos trozos se divide un asteroide ("+valor+")");
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Máximo de fragmentos 9", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }
}