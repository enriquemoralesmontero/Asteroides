package com.example.profesor.asteroides;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase con el array de puntuaciones del juego.
 *
 * @author Enrique Morales Montero
 * @version 1.0.0
 */
public class AlmacenPuntuacionesArray implements AlmacenPuntuaciones {

    private ArrayList puntuaciones; // Array de puntuaciones.

    /**
     * Constructor que rellena el array.
     */
    public AlmacenPuntuacionesArray() {
        puntuaciones= new ArrayList<>();
        puntuaciones.add("123000 Pepito Domingez");
        puntuaciones.add("111000 Pedro Martinez");
        puntuaciones.add("011000 Paco Pérez");
    }

    /**
     * Procedimiento que añade al array una nueva puntuación.
     *
     * @param puntos Puntos.
     * @param nombre Nombre del jugador.
     * @param fecha Fecha de la partida.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        puntuaciones.add(0, puntos + " "+ nombre);
    }

    /**
     * Función que devuelve el array de puntuaciones.
     *
     * @param cantidad
     * @return Array de puntuaciones.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override public List<String> listaPuntuaciones(int cantidad) {
        return puntuaciones;
    }
}