package pacmanpc1;

import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author JENFIER ARCHILA - 202207231
 */
public class PacManPC1 {

    private static int filas = 0;
    private static int columnas = 0;

    private static final String FANTASMA = "@";
    private static final String PREMIO = "0";
    private static final String PREMIO_ESPECIAL = "$";
    private static final String PARED = "X";
    private static String PACMAN = "<";

    private static final double PORCENTAJE_PREMIOS = 0.4;
    private static final double PORCENTAJE_PAREDES = 0.2;
    private static final double PORCENTAJE_FANTASMAS = 0.2;

    private static int cantidadVidas = 3;
    private static int puntos = 0;
    private static int premiosRestantes = 0;
    private static String nombreUsuario;

    private static Scanner sc = new Scanner(System.in);
    private static Random rand = new Random();

    //Matriz para juego
    private static String[][] tablero;

    // Matriz para historial: [Fila][0] = Nombre, [Fila][1] = Puntos
    //2 columnas porque tiene que guardar usuario y punteo.
    private static String[][] historialPartidas = new String[100][2];
    private static int contadorHistorial = 0;

    public static void main(String[] args) {
        int opcionInicio;
        do {
            System.out.println("***********************************");
            System.out.println("Bienvenido a pacman:");
            System.out.println("***********************************");
            System.out.println("Elija alguna de las opciones:");
            System.out.println("1 para crear el tablero");
            System.out.println("2 para ver los puntos");
            System.out.println("3 para salir");
            System.out.println("***********************************");

            opcionInicio = sc.nextInt();
            opcionElegida(opcionInicio);

        } while (opcionInicio != 3);

        System.out.println("***********************************");
        System.out.println("Gracias por jugar PAC-MAN :)");
        System.out.println("***********************************");
    }

    public static void opcionElegida(int opcionInicio) {
        int numeroPremios, numeroParedes, numeroTrampas;
        String tipoTablero;

        switch (opcionInicio) {
            case 1:
                boolean validar = false;
                System.out.println("--------------------------------------------");
                System.out.println("Por favor, ingresa los siguientes valores:");
                System.out.print("Ingresar tu usuario: ");
                nombreUsuario = sc.next();

                //Tipo de tablero repetir mientras no se P o G
                //Validar tamaño del tablero
                do {
                    System.out.println("------ Ingresar tipo de tablero  -----");
                    System.out.println("P : Tablero pequenio de 5 X 6");
                    System.out.println("G : Tablero grande de 10 X 10");
                    tipoTablero = sc.next();

                    if ((tipoTablero.equalsIgnoreCase("P"))
                            || (tipoTablero.equalsIgnoreCase("G"))) {
                        validar = true;

                        if (tipoTablero.equalsIgnoreCase("P")) {
                            filas = 5;
                            columnas = 6;
                        } else {
                            filas = 10;
                            columnas = 10;
                        }

                    } else {
                        System.out.println("Valor incorrecto debe ser P o G ....");
                    }
                } while (!validar);

                validar = false;

                //Asigna premios
                numeroPremios = asignarCantidades("premios", PORCENTAJE_PREMIOS);

                //Asigna paredes
                numeroParedes = asignarCantidades("paredes", PORCENTAJE_PAREDES);

                //Asigna fantasmas
                numeroTrampas = asignarCantidades("fantasmas", PORCENTAJE_FANTASMAS);

                System.out.println("Creando tablero...");
                System.out.println(" *********************************");

                //Inicializamos tablero con base en la cantidad de filas y columnas
                tablero = new String[filas][columnas];

                //Llenando de espacios la matriz
                for (int i = 0; i < filas; i++) {
                    for (int j = 0; j < columnas; j++) {
                        tablero[i][j] = " ";
                    }
                }

                // Configuración de premios y elementos
                premiosRestantes = numeroPremios;

                //Math.max(a,b) toma el valor mas alto así garantizamos que
                //al menos exista un premio especial
                //dividimos el porcentaje total de premios entre 2
                int especiales = (int) Math.max(1, numeroPremios
                        * (PORCENTAJE_PREMIOS / 2));

                //Poniendo premios en la matriz
                colocarElemento(tablero, PREMIO, numeroPremios - especiales);

                //Poniendo premios en la matriz
                colocarElemento(tablero, PREMIO_ESPECIAL, especiales);

                //Poniendo las paredes en la matriz
                colocarElemento(tablero, PARED, numeroParedes);

                //Poniendo fantasmas
                colocarElemento(tablero, FANTASMA, numeroTrampas);

                System.out.println("... Tablero de " + filas + "X"
                        + columnas + " creado ...");

                imprimirTablero(tablero);
                System.out.println(); //*************************************

                int pFila,
                 pCol;
                do {
                    System.out.println(" *** Escoja donde desea colocar al personaje: *** ");
                    System.out.print("Fila: ");
                    pFila = sc.nextInt();
                    System.out.print("Columna: ");
                    pCol = sc.nextInt();
                } while (!verificacionPosicionPersonaje(pFila, pCol, tablero));

                tablero[pFila - 1][pCol - 1] = PACMAN;
                int[] posicion = {pFila - 1, pCol - 1};

                // Reiniciar valores de partida
                puntos = 0;
                cantidadVidas = 3;

                cicloJuego(posicion);
                break;

            case 2:
                System.out.println("\n--- HISTORIAL DE PARTIDAS ---");
                for (int i = contadorHistorial - 1; i >= 0; i--) {
                    System.out.println("Usuario: " + historialPartidas[i][0] + " | Puntos: " + historialPartidas[i][1]);
                }
                break;
        }
    }

    public static void cicloJuego(int[] posicion) {
        String movimiento;
        boolean jugando = true;

        while (jugando && cantidadVidas > 0 && premiosRestantes > 0) {
            System.out.println("**********************************");
            System.out.println("Usuario: " + nombreUsuario + " --- Puntos: " + puntos + " --- Vidas: " + cantidadVidas);
            System.out.println("**********************************");
            imprimirTablero(tablero);

            /*System.out.print("Movimientos (8, 5, 4, 6, F): ");*/
            System.out.println("Mueve tu personaje");
            System.out.println("8. Arriba");
            System.out.println("5. Abajo");
            System.out.println("6. Derecha");
            System.out.println("4. Izquierda");
            System.out.println("F. Pausa");
            System.out.print(">");
            movimiento = sc.next().toUpperCase();

            if (movimiento.equals("F")) {
                System.out.println("3. Regresar, 4. Terminar");
                if (sc.next().equals("4")) {
                    jugando = false;
                }
                continue;
            }

            moverPersonaje(tablero, posicion, movimiento);
        }

        // Guardar en la matriz de historial al terminar
        historialPartidas[contadorHistorial][0] = nombreUsuario;
        historialPartidas[contadorHistorial][1] = String.valueOf(puntos);
        contadorHistorial++;
    }

    public static boolean moverPersonaje(String[][] tablero, int[] posicion, String direccion) {
        int filaActual = posicion[0];
        int colActual = posicion[1];
        int nf = filaActual;
        int nc = colActual;

        if (direccion.equals("8")) {
            nf--;
        } else if (direccion.equals("5")) {
            nf++;
        } else if (direccion.equals("6")) {
            nc++;
        } else if (direccion.equals("4")) {
            nc--;
        } else {
            System.out.println("Movimiento incorrecto, ingrese un valor valido");
            return false;

        }

        // LÓGICA DE MOVIMIENTO CIRCULAR 
        if (nf < 0) {
            nf = filas - 1;
        } else if (nf >= filas) {
            nf = 0;
        }
        if (nc < 0) {
            nc = columnas - 1;
        } else if (nc >= columnas) {
            nc = 0;
        }

        // Verificar Paredes 
        if (tablero[nf][nc].equals(PARED)) {
            System.out.println("Choque contra una pared @_@");
            return false;
        }

        // Sistema de Premios y Fantasmas [cite: 51, 52, 61]
        if (tablero[nf][nc].equals(PREMIO)) {
            puntos += 10;
            premiosRestantes--;
        } else if (tablero[nf][nc].equals(PREMIO_ESPECIAL)) {
            puntos += 15;
            premiosRestantes--;
        } else if (tablero[nf][nc].equals(FANTASMA)) {
            cantidadVidas--;
            System.out.println("Un fantasma te quito una vida :(");
        }

        // Actualizar posiciones
        tablero[filaActual][colActual] = " ";
        tablero[nf][nc] = PACMAN;
        posicion[0] = nf;
        posicion[1] = nc;

        return true;
    }

    // --- MANTENIENDO TUS MÉTODOS ORIGINALES ---
    public static void imprimirTablero(String[][] tablero) {
        for (int j = 0; j < columnas + 2; j++) {
            System.out.print("--");
        }
        System.out.println();
        for (int i = 0; i < filas; i++) {
            System.out.print("|");
            for (int j = 0; j < columnas; j++) {
                System.out.print(tablero[i][j] + " ");
            }
            System.out.println("|");
        }
        for (int j = 0; j < columnas + 2; j++) {
            System.out.print("--");
        }
        System.out.println();
    }

    public static boolean verificacionPosicionPersonaje(int x, int y, String[][] tablero) {
        if ((x > 0) && (x <= filas) && (y > 0) && (y <= columnas)) {
            return tablero[x - 1][y - 1].equals(" ");
        } /*else {
            System.out.println("Posición ocupada, ingrese posicion valida");
        }*/
        return false;
    }

    public static int asignarCantidades(String objeto, double porcentaje) {
        int cantidad;
        int max = (int) (filas * columnas * porcentaje);
        do {
            System.out.print("Elige la cantidad de " + objeto + " (1 - " + max + "): ");
            cantidad = sc.nextInt();
        } while (cantidad < 1 || cantidad > max);
        /*System.out.println("Cantidad invalida, ingrese un valor dentro del rango");*/
        return cantidad;
    }

    public static void colocarElemento(String[][] tablero, String simbolo, int cantidad) {
        int colocados = 0;
        while (colocados < cantidad) {
            int f = rand.nextInt(filas);
            int c = rand.nextInt(columnas);
            if (tablero[f][c].equals(" ")) {
                tablero[f][c] = simbolo;
                colocados++;
            }
        }
    }
}
