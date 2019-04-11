package com.example.profesor.asteroides;

import java.util.List;

/**
 * Interfaz para implementar las puntuaciones.
 *
 * @author Enrique Morales Montero
 * @version 1.0.0
 */
public interface AlmacenPuntuaciones {

    public void guardarPuntuacion(int puntos, String nombre, long fecha);
    public List<String> listaPuntuaciones(int cantidad);
}