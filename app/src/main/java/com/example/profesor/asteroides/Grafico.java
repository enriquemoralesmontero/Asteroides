package com.example.profesor.asteroides;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Clase del objeto gráfico con el que interactuaremos en el juego.
 * Ejemplos de este objeto son: la nave espacial, los asteroides, el misil...
 *
 * @author Enrique Morales Montero
 * @version 1.0.3
 */
class Grafico {

    private Drawable drawable;   	//Imagen que dibujaremos.

    private double posX, posY;   	//Posición.
    private double incX, incY;   	//Velocidad desplazamiento.

    private int angulo, rotacion;	// Ángulo y velocidad rotación.
    private int ancho, alto;     	// Dimensiones de la imagen.
    private int radioColision;   	// Para determinar colisión.

    private View view;				// Donde dibujamos el gráfico (usada en view.ivalidate).

    // Para determinar el espacio a borrar (view.ivalidate)

    public static final int MAX_VELOCIDAD = 20;

    /**
     * Constructor.
     *
     * @param view View
     * @param drawable Imagen
     */
    public Grafico(View view, Drawable drawable) {

        this.view = view;
        this.drawable = drawable;

        ancho = drawable.getIntrinsicWidth();
        alto = drawable.getIntrinsicHeight();
        radioColision = (alto + ancho) / 4;
    }

    /**
     * Procedimiento que "rerenderiza" la imagen en el lienzo.
     *
     * @param canvas Aquí dibujaremos nuestro gráfico.
     *
     * @author Enrique Morales Montero
     * @version 1.0.1
     */
    public void dibujaGrafico(Canvas canvas) {

        canvas.save();

        int x = (int) (posX + ancho / 2);
        int y = (int) (posY + alto / 2);

        canvas.rotate((float) angulo, (float) x, (float) y); // Rota el lienzo.

        drawable.setBounds((int) posX, (int) posY,(int) posX + ancho, (int) posY + alto);
        drawable.draw(canvas); // Mueve la imagen a las nuevas coordenadas.

        canvas.restore();

        int rInval = (int) Math.hypot(ancho, alto) / 2 + MAX_VELOCIDAD;

        view.invalidate(x - rInval, y - rInval, x + rInval, y + rInval);
    }

    /**
     * Mueve el objeto gráfico y controla que no se salga de la pantalla.
     *
     * @param factor Factor.
     *
     * @author Enrique Morales Montero
     * @version 1.0.1
     */
    public void incrementaPos(double factor) {

        posX += incX * factor;

        if (posX < -ancho / 2) {posX = view.getWidth() - ancho / 2;}	// Si la nave (u otro objeto) sale de la pantalla, corregimos la posición.
        if (posX > view.getWidth() - ancho / 2) {posX = -ancho / 2;}

        posY += incY * factor;

        if (posY < -alto / 2) {posY = view.getHeight() - alto / 2;}		// Idem.
        if (posY > view.getHeight() - alto / 2) {posY = -alto / 2;}

        angulo += rotacion * factor; 									//Actualizamos ángulo.
    }

    /**
     * Función que calcula la distancia que hay entre dos objetos gráficos.
     *
     * @param g Gráfico con el que compararemos la distancia.
     * @return True / False
     *
     * @author Enrique Morales Montero
     * @version 1.0.1
     */
    public double distancia(Grafico g) {
        return Math.hypot(posX - g.posX, posY - g.posY);
    }

    /**
     * Función que controla si dos objetos gráficos han colisionado.
     *
     * @author Enrique Morales Montero
     * @version 1.0.1
     */
    public boolean verificaColision(Grafico g) {
        return (distancia(g) < (radioColision + g.radioColision));
    }

    // Getters y setter que nuestro colega del tutorial no se ha dignado a darnos...

    public int getAncho() {return ancho;}
    public int getAlto() {return alto;}
    public int getCenX() {return (int) this.posX + this.ancho / 2;} // Esto no me convence. Pero funciona.
    public int getCenY() {return (int) this.posY + this.alto / 2;} // Esto no me convence.
    public double getAngulo() {return this.angulo;}
    public double getIncX() {return this.incX;}
    public double getIncY() {return this.incY;}

    public void setIncX(double incX) {this.incX = incX;}
    public void setIncY(double incY) {this.incY = incY;}
    public void setAngulo(int angulo) {this.angulo = angulo;}
    public void setRotacion(int rotacion) {this.rotacion = rotacion;}
    public void setPosX(double posX) {this.posX = posX;}
    public void setPosY(double posY) {this.posY = posY;}
    public void setCenX(int cenX) {this.posX = cenX-(ancho/2);}
    public void setCenY(int cenY) {this.posY = cenY-(alto/2);}

    /**
     * Este método lo he creado para cambiar la imagen de la nave.
     *
     * @param drawable Imagen.
     */
    public void setDrawable(Drawable drawable) {this.drawable = drawable;}

    public Drawable getDrawable() {
        return drawable;
    }

    public double getPosX() {
        return this.posX;
    }
    public double getPosY() {
        return this.posY;
    }

    public int getRotacion() {return rotacion;}
}