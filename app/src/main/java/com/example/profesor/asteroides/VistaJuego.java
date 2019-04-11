package com.example.profesor.asteroides;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.Vector;

/**
 * Vista que engloba toda la mecánica del juego.
 *
 * @author Enrique Morales Montero
 * @version 1.0.9
 *
 * Inclusiones propias (lo introducido entre paréntesis puede variar):
 *      - Cambio de fondos (en el menú de inicio y en el juego).
 *      - Cambio de gráficos "handmade" (nave, misil...).
 *      - Añadidos otros muchos.
 *      - La nave, al colisionar con un asteroide, destruye el asteroide.
 *      - La nave, al colisionar contra un asteroide, pierde una vida.
 *      - La nave, mientras va perdiendo vidas, se va deteriorando visualmente.
 *      - Sin vidas, aparece el clásico "Game over" con fondo negro y se sale del juego (al menú principal) después de un tiempo corto.
 *      - Los asteroides, al ser destruidos, genera una explosión.
 *      - Los asteroides, al ser destruidos por un misil, se dividen en X fragmentos.
 *      - La cantidad de fragmentos en los que se divide un asteroide se especifica en las preferencias de la aplicación. El usuario puede modificar este valor.
 *      - Por cada asteroide grande destruido, se crean otros 2 grandes después de X segundos (10 seg).
 *		- El límite de asteroides grandes que puede haber en el juego es de 20.
 *      - Las explosiones duran X tiempo (1 seg).
 *      - Destruir un asteroide con un misil da 10 puntos.
 *      - Al conseguir X puntos (400) el armamento de la nave mejora.
 *      - La nave con armamento mejorado dispara 3 misiles.
 *      - Los dos misiles adicionales del armamento mejorado tienen un poco de ángulo, de tal manera aumenta la amplitud del disparo.
 *      - Hay un límite de disparos (munición: 10 misiles).
 *      - El límite (10) de disparos - la munición - aumenta al destruir asteroides.
 *		- A partir de los 500 puntos, por cada 500 puntos que se consiguen, la nave dropea una mina.
 * 		- La mina es estática, pero gira sobre sí misma.
 *		- Si un asteroide colisiona con una mina, ésta desaparece y crea una explosión azul que destruye los asteroides cercanos.
 * 		- La explosión azul desaparece después de un tiempo.
 * 		- Los misiles y la nave no se ven afectados por las explosiones y las minas.
 *      - Añadidos diversos controles de errores al tratar con hilos que modifican los índices de los vectores, dando errores cuando hay muchos asteroides y misiles.
 *      - Música. (Tutorial)
 *      - Creación de sincronización de hilos para que el juego no consuma demasiados recursos del dispositivo. Los eventos están en la clase "Juego" y el hilo del juego se ha modificado. (Tutorial)
 *      - Paso del recolector de basura de Java para quitar los objetos muertos.
 */
public class VistaJuego extends View {

    // ########################################################################################################
    //	    ATRIBUTOS
    // ########################################################################################################

    // Nave.

    private Grafico nave;                   // Objeto gráfico de la nave.
    private Drawable nave1, nave2, nave3;	// Imagenes.
    private int giroNave;                   // Incremento de dirección.
    private float aceleracionNave;          // Aumento de velocidad.
    private int vidas = 3;                  // Vidas de la nave.
    private int puntos = 0;                 // Puntuación.

    // Incremento estándar de giro y aceleración.

    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;
    private static final int MAX_VELOCIDAD_NAVE = 5;

    // Misiles.

    private Vector <Grafico> misiles = new Vector();// Vector con los misiles en juego.
    private int municion = 10;                      // Cantidad de misiles que se pueden disparar a la vez. Irá aumentando.
    private static int PASO_VELOCIDAD_MISIL = 20;	// Velocidad.
    private float mX=0, mY=0;                       // Coordenadas del misil.
    private boolean disparo = false;
    private Drawable drawableMisil;                 // Imagen.
    private final int PUNTOS_POWER_UP = 200;        // Al llegar a esta puntuación, se dispara de 3 en 3.
    private boolean armamentoMejorado = false;

    // Minas.

    private Vector<Grafico> minas = new Vector();   // Vector de minas.
    private boolean tienesMina = false;             // Sólo se puede almacenar una mina. Conviene utilizarlas rápido.
    private Drawable ondaDrawable;                  // Imagen de la onda expansiva.
    private Grafico onda;                           // Objeto de la onda expansiva.
    private boolean ondaEnAccion = false;           // Está explosionando True/False.
    private int minasUsadas = 0;

    // Asteroides.

    private Drawable drawableAsteroide, drawableExplos, drawableMediano, drawableChico;	// Objeto gráfico de los asteroides y sus explosiones.
    private Vector<Grafico> Asteroides;					// Vector con los asteroides.
    private int numAsteroides = 5;       				// Número inicial de asteroides.
    private int numFragmentos = 2;       				// Fragmentos en los que se divide.
    private final int TIEMPO_RESPAWN_ASTEROIDE = 5;   	// Tiempo en los que reaparecen.
    private final int TIEMPO_QUITAR_EXPLOSION = 1;		// Tiempo en que desaparecen las explosiones.
    private final int LIMITE_ASTEROIDES_GRANDES = 20;   // Límite de asteroides que pueden aparecer.

    // Dimensiones del campo.

    private int alt;
    private int anc;

    // Hilo y tiempo.

    private ThreadJuego thread = new ThreadJuego();		// Hilo encargado de procesar el juego.

    private static int PERIODO_PROCESO = 50; 			// Cada cuanto queremos procesar cambios (en milisegundos). Menos de 50 petamos la tarjeta gráfica.
    private long ultimoProceso = 0; 					// Cuando se realizó el último proceso.
    protected long ahora = 0;

    // Mensajes.

    private boolean hayMensaje = false;                 // Mostramos un mensaje si es verdadero.
    private Drawable mensDrawable;                      // Su imagen.
    private Grafico mensaje;                            // Su gráfico.
    private boolean finDelJuego = false;                // Mostramos un mensaje GAME OVER si es verdadero.
    private Drawable mensajeGameOverDrawable;           // Su imagen.
    private Grafico mensajeGameOver;                    // Su gráfico.
    private Drawable fondoGameOverDrawable;             // Su imagen de fondo.
    private Grafico fondoGameOver;                      // Su gráfico de fondo.

    private View vista = this;

    // ########################################################################################################
    //	    CONSTRUCTOR
    // ########################################################################################################

    /**
     * Constructor.
     *
     * @param context Contexto
     * @param attrs Set de atributos.
     *
     * @author Enrique Morales Montero
     * @version 1.0.7
     */
    public VistaJuego(Context context, AttributeSet attrs) {

        super(context, attrs);

        // Instanciamos la nave.

        nave3 = context.getResources().getDrawable(R.drawable.nave3);
        nave2 = context.getResources().getDrawable(R.drawable.nave2);
        nave1 = context.getResources().getDrawable(R.drawable.nave1);
        nave = new Grafico(this, nave3);

        drawableMisil = context.getResources().getDrawable(R.drawable.laser);

        // Instanciamos los asteroides.

        drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide1);
        drawableMediano = context.getResources().getDrawable(R.drawable.asteroide2);
        drawableChico = context.getResources().getDrawable(R.drawable.asteroide3);
        drawableExplos = context.getResources().getDrawable(R.drawable.explos);

        Asteroides = new Vector();

        for (int i = 0; i < numAsteroides; i++) {

            Grafico asteroide = new Grafico(this, drawableAsteroide);

            asteroide.setIncY(Math.random() * 4 - 2);				// Velocidad aleatoria.
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));		// Rotación aleatoria.
            asteroide.setRotacion((int) (Math.random() * 8 - 4));

            Asteroides.add(asteroide);                              // Asteroide añadido.
        }

        // Y el mensaje.

        mensDrawable = getContext().getDrawable(R.drawable.mensajearmamento);
        mensaje = new Grafico(this, mensDrawable);
        mensajeGameOverDrawable = getContext().getDrawable(R.drawable.mensajefinal);
        mensajeGameOver = new Grafico(this, mensajeGameOverDrawable);
        fondoGameOverDrawable = getContext().getDrawable(R.drawable.fondonegro);
        fondoGameOver = new Grafico(this, fondoGameOverDrawable);
        fondoGameOver.setPosY(0);
        fondoGameOver.setPosX(0);

        // Y las ondas espansivas de las minas.

        ondaDrawable = getContext().getDrawable(R.drawable.onda);
        onda = new Grafico(vista, ondaDrawable);
    }

    // ########################################################################################################
    //	    EVENTOS
    // ########################################################################################################

    /**
     * Procedimiento que se llama cuando el tamaño cambia y Android lo calcula.
     *
     * @param ancho
     * @param alto
     * @param ancho_anter
     * @param alto_anter
     *
     * @author Enrique Morales Montero
     * @version 1.0.2
     */
    @Override protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {

        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);

        // Una vez que conocemos nuestro ancho y alto.

        anc = ancho;
        alt = alto;

        // Posicionamos la nave.

        nave.setPosX((ancho/2)-(nave.getAncho()/2));
        nave.setPosY((alto/2)-(nave.getAlto()/2));

        // Posicionamos los asteroides (lejos de la nave).

        for (Grafico asteroide: Asteroides) {

            do{
                asteroide.setPosX(Math.random()*(ancho-asteroide.getAncho()));
                asteroide.setPosY(Math.random()*(alto-asteroide.getAlto()));

            } while(asteroide.distancia(nave) < (ancho+alto)/5);
        }

        // Posicionamos el mensaje, game over...

        mensaje.setPosX((ancho/2)-(mensaje.getAncho()/2));
        mensaje.setPosY(100);

        mensajeGameOver.setPosX((ancho/2)-(mensajeGameOver.getAncho()/2));
        mensajeGameOver.setPosY((alto/2)-(mensajeGameOver.getAlto()/2));

        ultimoProceso = System.currentTimeMillis();
        thread.start(); // ¡Empezamos!
    }

    /**
     * Procedimiento de "re-renderización" de los objetos gráficos en el lienzo.
     *
     * @param canvas
     *
     * @author Enrique Morales Montero
     * @version 1.0.2
     */
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        nave.dibujaGrafico(canvas);				// Nave dibujada.

        try {
            if (!(misiles.isEmpty())) {
                for (int m = 0; m < misiles.size(); m++) {
                    misiles.elementAt(m).dibujaGrafico(canvas);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            for (Grafico asteroide : Asteroides) {	// Asteroides dibujados.
                asteroide.dibujaGrafico(canvas);
            }
        } catch (Exception e) {
            System.err.println("Error en el onDraw...");
            System.err.println(e.getMessage());
        }

        if (hayMensaje) {                       // Mensaje dibujado si se requiere.
            mensaje.dibujaGrafico(canvas);
        }

        try {
            for (Grafico mina : minas) {	    // Minas dibujados.
                mina.dibujaGrafico(canvas);
            }
        } catch (Exception e) {
            System.err.println("Error en el MINAS onDraw...");
            System.err.println(e.getMessage());
        }

        if (ondaEnAccion) {                     // Está explotando una mina.
            onda.dibujaGrafico(canvas);         // La dibujamos.
        }

        if (finDelJuego) {                      // Se acabó el juego.
            fondoGameOver.dibujaGrafico(canvas);// Fondo negro + texto "Game over".
            mensajeGameOver.dibujaGrafico(canvas);
        }
    }

    /**
     * Función para jugar con las teclas. Controla sus presiones.
     *
     * @param codigoTecla
     * @param evento
     * @return True / False
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override public boolean onKeyDown(int codigoTecla, KeyEvent evento) {

        super.onKeyDown(codigoTecla, evento);
        boolean procesada = true; // Suponemos que vamos a procesar la pulsación.

        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = +PASO_ACELERACION_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                giroNave = -PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = +PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_P:
                if (thread.isPaused()) {
                    thread.reanudar();
                } else {
                    thread.pausar();
                }
            case KeyEvent.KEYCODE_ENTER:
                activaMisil();
                break;
            case KeyEvent.KEYCODE_M:
                colocarMina();
            default:
                procesada = false; // Si estamos aquí, no hay pulsación que nos interese
                break;
        }

        return procesada;
    }

    /**
     * Función para jugar con las teclas. Controla cuando se sueltan.
     *
     * @param codigoTecla
     * @param evento
     * @return True / False
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {

        super.onKeyUp(codigoTecla, evento);

        // Suponemos que vamos a procesar la pulsación.

        boolean procesada = true;

        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = 0;
                break;
            default:
                procesada = false; // Si estamos aquí, no hay pulsación que nos interese.
                break;
        }
        return procesada;
    }

    /**
     * Función para jugar con los dedos.
     *
     * @param event
     * @return True / False
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    @Override
    public boolean onTouchEvent (MotionEvent event) {

        super.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                disparo=true;
                break;
            case MotionEvent.ACTION_MOVE:

                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);

                if (dy<6 && dx>6){
                    giroNave = Math.round((x - mX) / 2);
                    disparo = false;
                } else if (dx<6 && dy>6){
                    aceleracionNave = Math.round((mY - y) / 25);
                    disparo = false;
                }

                break;

            case MotionEvent.ACTION_UP:

                giroNave = 0;
                aceleracionNave = 0;

                if (disparo){
                    activaMisil();}
                break;
        }
        mX=x; mY=y;
        return true;
    }

    // ########################################################################################################
    //	    MÉTODOS AUXILIARES
    // ########################################################################################################

    /**
     * Procedimiento que controla la física del juego.
     *
     * @author Enrique Morales Montero
     * @version 1.0.6
     */
    synchronized private void actualizaFisica() {

        ahora = System.currentTimeMillis();

        if (ultimoProceso + PERIODO_PROCESO > ahora) {return;}

        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO; // Para una ejecución en tiempo real, calculamos retardo.
        ultimoProceso = ahora;                                      // Para la próxima vez.

        // Actualizamos velocidad y dirección de la nave a partir de
        // giroNave y aceleracionNave (según la entrada del jugador)

        nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));

        double nIncX = nave.getIncX() + aceleracionNave * Math.cos(Math.toRadians(nave.getAngulo())) * retardo;
        double nIncY = nave.getIncY() + aceleracionNave * Math.sin(Math.toRadians(nave.getAngulo())) * retardo;

        // Actualizamos si el módulo de la velocidad no excede el máximo.

        if (Math.hypot(nIncX,nIncY) <= MAX_VELOCIDAD_NAVE){
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }

        // Actualizamos posiciones X e Y.

        nave.incrementaPos(retardo);

        try {
            for (Grafico asteroide : Asteroides) {      // Movemos los asteroides.
                asteroide.incrementaPos(retardo);
            }
        } catch (Exception e) {
            System.err.println("Error en el actualizaFisica al querer mover los asteroides.");
            System.err.println(e.getMessage());
        }

        // Actualizamos posición de misil.
        // Verificamos si ha destruido algún asteroide.

        try {
            if (!(misiles.isEmpty())) {
                for (int m = 0; m < misiles.size(); m++) {          // Por cada misil.

                    misiles.elementAt(m).incrementaPos(retardo);    // Se mueve.

                    for (int i = 0; i < Asteroides.size(); i++) {
                        if (misiles.elementAt(m).verificaColision(Asteroides.elementAt(i)) && (Asteroides.elementAt(i).getDrawable() == drawableAsteroide || Asteroides.elementAt(i).getDrawable() == drawableMediano || Asteroides.elementAt(i).getDrawable() == drawableChico)) {
                            destruyeAsteroide(i, m);                // Asteroide destruido.
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {System.err.println(e.getMessage());}

        // Si la nave o una mina choca contra un asteroide...

        try {
            for (int i = 0; i < Asteroides.size(); i++) {
                if (nave.verificaColision(Asteroides.elementAt(i)) && (Asteroides.elementAt(i).getDrawable() == drawableAsteroide || Asteroides.elementAt(i).getDrawable() == drawableMediano || Asteroides.elementAt(i).getDrawable() == drawableChico)) {

                    destruyeAsteroide(i);           // Asteroide destruido sin fragmentos.

                    vidas--;                        // 1 vida menos.

                    if (vidas == 2) {               // La nave se va deteriorando con los golpes...
                        nave.setDrawable(nave2);
                    } else if (vidas == 1) {
                        nave.setDrawable(nave1);
                    } else if (vidas == 0) {        // Si no le quedan vidas. GAME OVER!
                        new ThreadGameOver().start();
                    }

                    break;
                }

                // Explosión de minas.

                try {
                    if (minas.size() > 0) {
                        for (int j = 0; j < minas.size(); j++) {

                            minas.elementAt(j).incrementaPos(retardo); // Giramos la mina.

                            if (minas.elementAt(j).verificaColision(Asteroides.elementAt(i))) { // El asteroide choca con la mina.

                                onda.setCenX(minas.elementAt(j).getCenX()); // Se posiciona la onda expansiva.
                                onda.setCenY(minas.elementAt(j).getCenY());

                                new ThreadOnda().start();                   // Aparece la onda expansiva.
                                minas.remove(j);                            // La mina se pulveriza.

                                for (Grafico aster : Asteroides) {          // Por cada asteroide envuelto en la onda expansiva...
                                    if (aster.verificaColision(onda)) {
                                        Asteroides.remove(aster);           // ...pulverizamos el asteroide.
                                        puntos += 10;
                                    }
                                }

                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error en la física de la mina.");
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            System.err.println("Error de índice de array en actualizaFísica...");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Procedimiento para colocar una mina donde se encuentre la nave.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    private void colocarMina() {
        Drawable minaDrawable = getContext().getDrawable(R.drawable.mina);
        Grafico mina = new Grafico(vista, minaDrawable);

        mina.setAngulo(1);		        // Rotación.
        mina.setRotacion(1);

        mina.setCenX(nave.getCenX());   // Posicionamiento (con la nave).
        mina.setCenY(nave.getCenY());

        minas.add(mina);                // Añadida.
        minasUsadas++;
    }

    /**
     * Procedimiento que ejecutamos al destruir un asteroide chocando contra él.
     *
     * @param i Índice donde se encuentra el asteroide dentro del vector de asteroides.
     *
     * @author Enrique Morales Montero
     * @version 1.0.4
     */
    private void destruyeAsteroide(int i) {

        boolean eraUnoGordo = false;

        if (Asteroides.elementAt(i).getDrawable() == drawableAsteroide) {
            eraUnoGordo = true;
        }

        Grafico explos = new Grafico(this, drawableExplos); // Provocamos una explosión.

        explos.setCenX(Asteroides.get(i).getCenX());             // Posicionamos la explosión donde estaba el asteroide.
        explos.setCenY(Asteroides.get(i).getCenY());

        Asteroides.add(explos);
        Asteroides.remove(i);

        new ThreadQuitarExplosion(explos).start();

        /*
         Si era un asteroide grande, tenemos una buena noticia y otra mala:

            - La buena es que conseguimos más misiles para disparar.
            - La mala es que hacemos que aparezcan otros dos después de un tiempo.

         Así, aumentamos tanto la potencia de fuego de la nave como la dificultad del juego.
          */

        if (eraUnoGordo) {
            municion++;                         // Munición +1.
            new ThreadNuevoAsteroide().start(); // Después de X segundos, reaparecen dos asteroides.
            if (Asteroides.size() < LIMITE_ASTEROIDES_GRANDES) {
                new ThreadNuevoAsteroide().start();
            }
        }
    }

    /**
     * Procedimiento que se ejecuta cuando se destruye un asteroide por colisión con un misil.
     * @param i Índice del asteroide.
     * @param m Índice del misil.
     *
     * @author Enrique Morales Montero
     * @version 1.0.1
     */
    private void destruyeAsteroide(int i, int m) {

        boolean eraUnoGordo = false;

        if (Asteroides.elementAt(i).getDrawable() == drawableAsteroide) {
            eraUnoGordo = true;
        }

        puntos += 10;                       // Puntuación +10.

        if (puntos == PUNTOS_POWER_UP) {       // A una cierta cantidad de puntos, disparamos de 3 en 3.
            armamentoMejorado = true;
            new ThreadMensaje().start();
        }

        // Por cada 200 puntos, a partir de los 400 (incluidos), conseguimos una mina.

        if (puntos == (500 * minasUsadas + 500)) {
            colocarMina();
            new ThreadMensaje().start();
        }

        Grafico explos = new Grafico(this, drawableExplos); // Provocamos una explosión.

        explos.setCenX(Asteroides.get(i).getCenX());             // Posicionamos la explosión donde estaba el asteroide.
        explos.setCenY(Asteroides.get(i).getCenY());

        // Dividimos el asteroide en X fragmentos más pequeños.

        if (eraUnoGordo) {
            for (int f = 0; f < numFragmentos; f++) {
                Grafico fragmento = new Grafico(this, drawableMediano);
                fragmento.setIncY(Math.random() * 4 - 2);                     // Velocidad aleatoria.
                fragmento.setIncX(Math.random() * 4 - 2);
                fragmento.setAngulo((int) (Math.random() * 360));             // Rotación aleatoria.
                fragmento.setRotacion((int) (Math.random() * 8 - 4));
                fragmento.setCenX(Asteroides.get(i).getCenX());               // Posicionamos la explosión donde estaba el asteroide.
                fragmento.setCenY(Asteroides.get(i).getCenY());
                Asteroides.add(fragmento);                                    // Añadimos el fragmento.
            }
        } else if (Asteroides.elementAt(i).getDrawable() == drawableMediano) {
            for (int f = 0; f < numFragmentos; f++) {
                Grafico fragmento = new Grafico(this, drawableChico);
                fragmento.setIncY(Math.random() * 4 - 2);                     // Velocidad aleatoria.
                fragmento.setIncX(Math.random() * 4 - 2);
                fragmento.setAngulo((int) (Math.random() * 360));             // Rotación aleatoria.
                fragmento.setRotacion((int) (Math.random() * 8 - 4));
                fragmento.setCenX(Asteroides.get(i).getCenX());               // Posicionamos la explosión donde estaba el asteroide.
                fragmento.setCenY(Asteroides.get(i).getCenY());
                Asteroides.add(fragmento);                                    // Añadimos el fragmento.
            }
        }

        Asteroides.add(explos);                     // Añadimos la explisión.
        Asteroides.remove(i);                       // Eliminamos el asteroide destruido.
        misiles.remove(m);                          // Eliminamos el misil.

        //tiempoMisiles.remove(m);

        new ThreadQuitarExplosion(explos).start();  // Pasado X tiempo, desaparece la explosión.

        /*
         Si era un asteroide grande, tenemos una buena noticia y otra mala:

            - La buena es que conseguimos más misiles para disparar.
            - La mala es que hacemos que aparezcan otros dos después de un tiempo.

         Así, aumentamos tanto la potencia de fuego de la nave como la dificultad del juego (y el número de puntos).
          */
        System.out.println(puntos);
        if (eraUnoGordo) {
            municion++;                         // Munición +1.
            new ThreadNuevoAsteroide().start(); // Después de X segundos, reaparecen dos asteroides.
            if (Asteroides.size() < LIMITE_ASTEROIDES_GRANDES) {
                new ThreadNuevoAsteroide().start();
            }
        }
    }

    /**
     * Procedimiento de disparo del misil.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    private void activaMisil() {

        // Instanciamos su armamento.
        try {
            if (misiles.size() <= municion) {

                Grafico misil = new Grafico(this, drawableMisil);

                misil.setCenX(nave.getCenX());
                misil.setCenY(nave.getCenY());
                misil.setAngulo((int) nave.getAngulo());
                misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
                misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);

                misiles.add(misil);

                // Si tenemos más de ??? puntos, ¡disparamos 3 misiles!

                try {
                    if (armamentoMejorado) {

                        Grafico misil2 = new Grafico(this, drawableMisil);

                        misil2.setCenX(nave.getCenX());
                        misil2.setCenY(nave.getCenY());
                        misil2.setAngulo((int) nave.getAngulo()-2);
                        misil2.setIncX((Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL)-2);
                        misil2.setIncY((Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL)-2);

                        misiles.add(misil2);

                        Grafico misil3 = new Grafico(this, drawableMisil);

                        misil3.setCenX(nave.getCenX());
                        misil3.setCenY(nave.getCenY());
                        misil3.setAngulo((int) nave.getAngulo()+2);
                        misil3.setIncX((Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL)+2);
                        misil3.setIncY((Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL)+2);

                        misiles.add(misil3);
                    }
                } catch (Exception e) {System.err.println(e.getMessage());}

                //Integer tiempoMisil = (int) Math.min(this.getWidth() / Math.abs(misil.getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2;
                //tiempoMisiles.add(tiempoMisil);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // ########################################################################################################
    //	    HILOS
    // ########################################################################################################

    /**
     * Hilo del juego. Se para cuando se sale del juego.
     *
     * @author Enrique Morales Montero
     * @version 1.0.2
     */
    class ThreadJuego extends Thread {

        private boolean pausa, corriendo;

        public boolean isPaused() {return pausa;}

        public synchronized void pausar() {pausa = true;}

        public synchronized void reanudar() {
            pausa = false;
            notify();
        }

        public void detener() {
            corriendo = false;
            if (pausa) reanudar();
        }

        @Override public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this) {
                    while (pausa)
                        try {
                            wait();
                        } catch (Exception e) {}
                }
            }
        }
    }

    /**
     * Hilo que añade un nuevo asteroide después de un tiempo.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    class ThreadNuevoAsteroide extends Thread {
        @Override
        public void run() {

            int tiempo = TIEMPO_RESPAWN_ASTEROIDE;

            while (tiempo > 0) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {e.printStackTrace();}
                tiempo--;
            }

            Grafico asteroide = new Grafico(vista, drawableAsteroide);

            do {
                asteroide.setIncY(Math.random() * 4 - 2);
                asteroide.setIncX(Math.random() * 4 - 2);
                asteroide.setAngulo((int) (Math.random() * 360));
                asteroide.setRotacion((int) (Math.random() * 8 - 4));
                asteroide.setPosX(Math.random()*(anc-asteroide.getAncho()));
                asteroide.setPosY(Math.random()*(alt-asteroide.getAlto()));

            } while (asteroide.distancia(nave) < (anc+alt)/5);

            Asteroides.add(asteroide);  // Nuevo asteroide.
            System.gc();                // Limpiamos los objetos perdidos con el recolector de basura de Java.
        }
    }

    /**
     * Hilo que hace desaparecer una explosión después de un tiempo.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    class ThreadQuitarExplosion extends Thread {

        private Grafico ex;

        public ThreadQuitarExplosion(Grafico grafico) {
            this.ex = grafico;
        }

        @Override
        public void run() {

            int tiempo = TIEMPO_QUITAR_EXPLOSION;

            while (tiempo > 0) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {e.printStackTrace();}
                tiempo--;
            }

            for (int i = 0; i < Asteroides.size(); i++) {
                if (Asteroides.get(i) == ex) {
                    Asteroides.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * Hilo que hace aparecer un mensaje ("Armamento mejorado") durante 3 segundos para que luego desaparezca.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    class ThreadMensaje extends Thread {

        @Override
        public void run() {

            hayMensaje = true;          // Mostramos el mensaje.

            int tiempo = 3;

            while (tiempo > 0) {        // Esperamos 3 segundos.
                try {
                    sleep(1000);
                } catch (InterruptedException e) {e.printStackTrace();}
                tiempo--;
            }

            hayMensaje = false;         // Quitamos el mensaje.
        }
    }

    /**
     * Hilo que hace aparecer la onda expansiva de una mina y, después de 2 segundos, la hace desaparecer.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    class ThreadOnda extends Thread {

        @Override
        public void run() {

            ondaEnAccion = true;        // Mostramos la onda expansiva.

            int tiempo = 2;

            while (tiempo > 0) {        // Esperamos 3 segundos.
                try {
                    sleep(1000);
                } catch (InterruptedException e) {e.printStackTrace();}
                tiempo--;
            }

            ondaEnAccion = false;       // Quitamos la onda expansiva.
        }
    }

    /**
     * Hilo que muestra la pantalla de "Game over" un tiempo y luego termina el juego, devolviendo al menú principal.
     *
     * @author Enrique Morales Montero
     * @version 1.0.0
     */
    class ThreadGameOver extends Thread {
        @Override
        public void run() {

            Asteroides.clear();
            misiles.clear();
            minas.clear();

            System.gc();                // Limpiamos los objetos perdidos con el recolector de basura de Java.

            int tiempo = 1;
            finDelJuego = true;

            while (tiempo > 0) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {e.printStackTrace();}
                tiempo--;
            }

            ((Activity) getContext()).finish();
        }
    }

    public ThreadJuego getThread() {
        return thread;
    }
    public int getNumFragmentos() {return numFragmentos;}
    public void setNumFragmentos(int numFragmentos) {this.numFragmentos = numFragmentos;}
}