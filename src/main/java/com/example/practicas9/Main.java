package com.example.practicas9;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {
    private final int columnas = 12;
    private final int filas = 9;
    private final int casilla = 48;

    private Canvas tablero;
    private Label datos;
    private Label aviso;

    private Nivel nivel;
    private Personaje jugador;
    private Personaje[] drones;
    private Utileria robot;
    private Arma arma;
    private Recompensa[] piezas;
    private final int[][] lugaresPiezas = {{5, 1}, {9, 2}, {2, 7}};
    private boolean[] recogidas;
    private int cantidadPiezas;
    private boolean termino;
    private String mensaje;

    private final Random random = new Random();

    @Override
    public void start(Stage ventana) {
        iniciarJuego();
        tablero = new Canvas(columnas * casilla, filas * casilla);
        datos = new Label();
        datos.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        aviso = new Label();
        aviso.setWrapText(true);
        aviso.setMinHeight(70);
        aviso.setStyle("-fx-text-fill: #d8f3ff; -fx-font-size: 13px;");

        Label titulo = new Label("RESCATE MARINO");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: white;");

        Label subtitulo = new Label("Aventura submarina · JavaFX");
        subtitulo.setStyle("-fx-text-fill: #b5e7ff; -fx-font-size: 13px;");

        Label instrucciones = new Label(
                "Controles\n" +
                "• Flechas / WASD: moverse\n" +
                "• ESPACIO: lanzar arpón\n" +
                "• F: usar botiquín marino\n" +
                "• M: movimiento aleatorio\n" +
                "• R: reiniciar");
        instrucciones.setWrapText(true);
        instrucciones.setStyle("-fx-text-fill: #d8f3ff; -fx-font-size: 13px;");

        Label leyenda = new Label(
                "Leyenda\n" +
                "Buzo azul = jugador\n" +
                "3 tiburones rojos = enemigos\n" +
                "Botiquín verde = curación\n" +
                "Perlas moradas = recompensas\n" +
                "Boyas naranjas = checkpoints\n" +
                "Submarino amarillo = salida");
        leyenda.setWrapText(true);
        leyenda.setStyle("-fx-text-fill: #d8f3ff; -fx-font-size: 13px;");

        Button botonAleatorio = new Button("Movimiento aleatorio");
        Button botonReiniciar = new Button("Reiniciar");
        Button botonAtacar = new Button("Atacar");
        Button botonRobot = new Button("Usar botiquín");

        String estiloBoton = "-fx-background-color: #168aad; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-background-radius: 8;";
        botonAleatorio.setStyle(estiloBoton);
        botonReiniciar.setStyle(estiloBoton);
        botonAtacar.setStyle(estiloBoton);
        botonRobot.setStyle(estiloBoton);

        botonAleatorio.setOnAction(e -> moverAlAzar());
        botonReiniciar.setOnAction(e -> reiniciar());
        botonAtacar.setOnAction(e -> atacar());
        botonRobot.setOnAction(e -> usarRobot());

        botonAleatorio.setFocusTraversable(false);
        botonReiniciar.setFocusTraversable(false);
        botonAtacar.setFocusTraversable(false);
        botonRobot.setFocusTraversable(false);

        HBox botones = new HBox(8, botonAtacar, botonRobot);
        VBox lado = new VBox(14, titulo, subtitulo, datos, instrucciones, leyenda,
                botones, botonAleatorio, botonReiniciar, aviso);
        lado.setPadding(new Insets(18));
        lado.setPrefWidth(290);
        lado.setStyle("-fx-background-color: linear-gradient(to bottom, #034078, #0466c8);");

        BorderPane pantalla = new BorderPane();
        pantalla.setCenter(tablero);
        pantalla.setRight(lado);
        pantalla.setStyle("-fx-background-color: #023e8a;");

        Scene escena = new Scene(pantalla);
        escena.setOnKeyPressed(e -> {
            KeyCode tecla = e.getCode();
            if (tecla == KeyCode.UP || tecla == KeyCode.W) moverJugador("norte");
            if (tecla == KeyCode.DOWN || tecla == KeyCode.S) moverJugador("sur");
            if (tecla == KeyCode.LEFT || tecla == KeyCode.A) moverJugador("oeste");
            if (tecla == KeyCode.RIGHT || tecla == KeyCode.D) moverJugador("este");
            if (tecla == KeyCode.SPACE) atacar();
            if (tecla == KeyCode.F) usarRobot();
            if (tecla == KeyCode.M) moverAlAzar();
            if (tecla == KeyCode.R) reiniciar();
        });

        ventana.setTitle("Práctica 9 - Rescate Marino");
        ventana.setScene(escena);
        ventana.setResizable(false);
        ventana.show();
        actualizar();
    }

    private void iniciarJuego() {
        Inventario inventario = new Inventario(5);
        arma = new Arma("Arpón", 25, 1);
        inventario.agregarItem(arma);

        nivel = new Nivel("Arrecife profundo", 1, "Normal", inventario);
        jugador = new Personaje("Buzo", 100, 1, 1);

        drones = new Personaje[] {
                new Personaje("Tiburón 1", 50, 9, 5),
                new Personaje("Tiburón 2", 50, 8, 2),
                new Personaje("Tiburón 3", 50, 3, 5)
        };
        robot = new Utileria("Botiquín marino", "Recupera 20 de vida", 3, 6);
        nivel.agregarElementoDinamico(jugador);
        for (Personaje dron : drones) {
            nivel.agregarElementoDinamico(dron);
        }
        nivel.agregarElementoDinamico(robot);

        nivel.agregarCheckPoint(new CheckPoint("Boya 1", 3, 2));
        nivel.agregarCheckPoint(new CheckPoint("Boya 2", 8, 6));

        int[][] paredes = {{2,3}, {3,3}, {4,3}, {6,2}, {6,3},
                {6,4}, {8,4}, {4,6}, {5,6}, {7,7}};
        for (int[] p : paredes) {
            nivel.agregarObstaculo(new Obstaculo("Coral", 10, p[0], p[1]));
        }

        piezas = new Recompensa[] {
                new Recompensa("Perla azul", 100, "tesoro"),
                new Recompensa("Perla roja", 100, "tesoro"),
                new Recompensa("Perla verde", 100, "tesoro")
        };
        recogidas = new boolean[3];
        cantidadPiezas = 0;
        termino = false;
        mensaje = "Busca las tres perlas, activa las dos boyas y llega al submarino.";
    }

    private void reiniciar() {
        iniciarJuego();
        actualizar();
    }

    private boolean lugarLibre(int x, int y) {
        if (x < 1 || y < 1 || x >= columnas - 1 || y >= filas - 1) return false;
        for (Obstaculo obstaculo : nivel.getObstaculos()) {
            if (!obstaculo.isDestruido() && obstaculo.getPosicionX() == x
                    && obstaculo.getPosicionY() == y) return false;
        }
        return true;
    }

    private int[] siguienteLugar(String direccion, int x, int y) {
        return switch (direccion) {
            case "norte" -> new int[]{x, y - 1};
            case "sur" -> new int[]{x, y + 1};
            case "este" -> new int[]{x + 1, y};
            default -> new int[]{x - 1, y};
        };
    }

    private void moverJugador(String direccion) {
        if (termino) return;
        int[] lugar = siguienteLugar(direccion, jugador.getPosicionX(), jugador.getPosicionY());
        if (!lugarLibre(lugar[0], lugar[1])) {
            mensaje = "No puedes pasar por ahí.";
            actualizar();
            return;
        }
        jugador.mover(direccion, 1);
        mensaje = "Te moviste al " + direccion + ".";
        revisarObjetivos();
        moverDrones();
        revisarVida();
        actualizar();
    }

    private void moverDrones() {
        if (termino) return;
        String[] direcciones = {"norte", "sur", "este", "oeste"};
        for (Personaje dron : drones) {
            if (dron.getVida() <= 0) continue;
            String direccion = direcciones[random.nextInt(direcciones.length)];
            int[] lugar = siguienteLugar(direccion, dron.getPosicionX(), dron.getPosicionY());
            if (lugarLibre(lugar[0], lugar[1]) && !hayOtroDron(dron, lugar[0], lugar[1])) {
                dron.mover(direccion, 1);
            }
            if (mismaCasilla(dron, jugador.getPosicionX(), jugador.getPosicionY())) {
                jugador.recibirDaño(15);
                mensaje = "¡" + dron.getNombre() + " te mordió! -15 de vida.";
                if (jugador.getVida() <= 0) break;
            }
        }
    }

    private boolean hayOtroDron(Personaje actual, int x, int y) {
        for (Personaje otro : drones) {
            if (otro != actual && otro.getVida() > 0 && mismaCasilla(otro, x, y)) {
                return true;
            }
        }
        return false;
    }

    private void atacar() {
        if (termino) return;
        Personaje objetivo = null;
        int mejorDistancia = Integer.MAX_VALUE;
        for (Personaje dron : drones) {
            if (dron.getVida() <= 0) continue;
            int distancia = Math.abs(jugador.getPosicionX() - dron.getPosicionX())
                    + Math.abs(jugador.getPosicionY() - dron.getPosicionY());
            if (distancia <= arma.getAlcance() && distancia < mejorDistancia) {
                objetivo = dron;
                mejorDistancia = distancia;
            }
        }
        if (objetivo != null) {
            objetivo.recibirDaño(arma.getDaño());
            if (objetivo.getVida() == 0) {
                mensaje = "¡" + objetivo.getNombre() + " fue derrotado!";
            } else {
                mensaje = "Impactaste a " + objetivo.getNombre()
                        + ". Vida restante: " + objetivo.getVida();
            }
        } else {
            mensaje = "Para atacar, acércate a cualquiera de los tiburones.";
        }
        moverDrones();
        revisarVida();
        actualizar();
    }

    private void usarRobot() {
        if (termino) return;
        int distancia = Math.abs(jugador.getPosicionX() - robot.getPosicionX())
                + Math.abs(jugador.getPosicionY() - robot.getPosicionY());
        if (distancia <= 1 && !robot.isUsada()) {
            robot.usar();
            jugador.reparar(20);
            mensaje = "El botiquín te curó. Vida: " + jugador.getVida();
        } else {
            mensaje = "Acércate al botiquín verde. Solo puedes usarlo una vez.";
        }
        actualizar();
    }

    private void moverAlAzar() {
        if (termino) return;
        EntidadMapa[] objetos = new EntidadMapa[drones.length + 2];
        objetos[0] = jugador;
        for (int i = 0; i < drones.length; i++) {
            objetos[i + 1] = drones[i];
        }
        objetos[objetos.length - 1] = robot;

        int[][] antes = new int[objetos.length][2];
        for (int i = 0; i < objetos.length; i++) {
            antes[i][0] = objetos[i].getPosicionX();
            antes[i][1] = objetos[i].getPosicionY();
        }

        nivel.moverElementosDinamicos();
        for (int i = 0; i < objetos.length; i++) {
            EntidadMapa objeto = objetos[i];
            if (!lugarLibre(objeto.getPosicionX(), objeto.getPosicionY())
                    || (objeto instanceof Personaje && objeto != jugador
                    && hayOtroDron((Personaje) objeto, objeto.getPosicionX(), objeto.getPosicionY()))) {
                objeto.setPosicion(antes[i][0], antes[i][1]);
            }
        }

        mensaje = "El buzo, los tiburones y el botiquín se movieron al azar.";
        revisarObjetivos();
        for (Personaje dron : drones) {
            if (dron.getVida() > 0 && mismaCasilla(dron, jugador.getPosicionX(), jugador.getPosicionY())) {
                jugador.recibirDaño(15);
                mensaje = "¡" + dron.getNombre() + " te alcanzó! -15 de vida.";
            }
        }
        revisarVida();
        actualizar();
    }

    private void revisarObjetivos() {
        int x = jugador.getPosicionX();
        int y = jugador.getPosicionY();
        for (int i = 0; i < piezas.length; i++) {
            if (!recogidas[i] && lugaresPiezas[i][0] == x && lugaresPiezas[i][1] == y) {
                if (nivel.getInventario().agregarItem(piezas[i])) {
                    recogidas[i] = true;
                    cantidadPiezas++;
                    mensaje = "Recogiste " + piezas[i].getNombre() + ".";
                }
            }
        }
        for (CheckPoint baliza : nivel.getCheckPoints()) {
            if (!baliza.isActivado() && mismaCasilla(baliza, x, y)) {
                baliza.activar();
                mensaje = "Activaste " + baliza.getNombre() + ".";
            }
        }
        if (x == 10 && y == 7) {
            if (cantidadPiezas == 3 && balizasActivas() == 2) {
                termino = true;
                mensaje = "¡Ganaste! Completaste la misión submarina.";
            } else {
                mensaje = "El submarino sigue cerrado: faltan perlas o boyas.";
            }
        }
    }

    private int dronesActivos() {
        int total = 0;
        for (Personaje dron : drones) {
            if (dron.getVida() > 0) total++;
        }
        return total;
    }

    private int balizasActivas() {
        int total = 0;
        for (CheckPoint baliza : nivel.getCheckPoints()) {
            if (baliza.isActivado()) total++;
        }
        return total;
    }

    private void revisarVida() {
        if (jugador.getVida() <= 0) {
            termino = true;
            mensaje = "Se terminó tu vida. Presiona R para volver a jugar.";
        }
    }

    private boolean mismaCasilla(EntidadMapa objeto, int x, int y) {
        return objeto.getPosicionX() == x && objeto.getPosicionY() == y;
    }

    private void dibujarFondoMarino(GraphicsContext g) {
        g.setFill(Color.web("#90e0ef"));
        g.fillRect(0, 0, tablero.getWidth(), tablero.getHeight());

        g.setFill(Color.web("rgba(255,255,255,0.25)"));
        for (int i = 0; i < 8; i++) {
            g.fillOval(20 + i * 70, 15 + (i % 2) * 8, 55, 12);
        }

        g.setFill(Color.web("rgba(255,255,255,0.10)"));
        for (int i = 0; i < 20; i++) {
            double x = 10 + (i * 27) % (columnas * casilla - 20);
            double y = 20 + (i * 31) % (filas * casilla - 40);
            g.fillOval(x, y, 6, 6);
            g.fillOval(x + 8, y - 10, 4, 4);
        }
    }

    private void dibujarTablero(GraphicsContext g) {
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                double px = x * casilla;
                double py = y * casilla;
                Color colorBase;
                if (x == 0 || y == 0 || x == columnas - 1 || y == filas - 1) {
                    colorBase = Color.web("#0077b6");
                } else {
                    colorBase = (x + y) % 2 == 0 ? Color.web("#48cae4") : Color.web("#4cc9f0");
                }
                g.setFill(colorBase);
                g.fillRoundRect(px + 2, py + 2, casilla - 4, casilla - 4, 10, 10);

                g.setStroke(Color.web("rgba(0,60,90,0.35)"));
                g.setLineWidth(1.2);
                g.strokeRoundRect(px + 2, py + 2, casilla - 4, casilla - 4, 10, 10);
            }
        }

        g.setFill(Color.web("#d4a373"));
        g.fillRect(0, tablero.getHeight() - 22, tablero.getWidth(), 22);
    }

    private void dibujarSubmarino(GraphicsContext g, int x, int y) {
        double px = x * casilla;
        double py = y * casilla;
        g.setFill(Color.web("#ffd166"));
        g.fillRoundRect(px + 8, py + 18, 30, 14, 10, 10);
        g.fillRoundRect(px + 18, py + 12, 10, 8, 4, 4);
        g.setFill(Color.web("#8ecae6"));
        g.fillOval(px + 13, py + 21, 6, 6);
        g.fillOval(px + 21, py + 21, 6, 6);
        g.fillOval(px + 29, py + 21, 6, 6);
        g.setFill(Color.web("#f77f00"));
        g.fillPolygon(new double[]{px + 38, px + 43, px + 38}, new double[]{py + 21, py + 25, py + 29}, 3);
    }

    private void dibujarCoral(GraphicsContext g, int x, int y) {
        double px = x * casilla;
        double py = y * casilla;
        g.setStroke(Color.web("#ff6b6b"));
        g.setLineWidth(4);
        g.strokeLine(px + 24, py + 34, px + 24, py + 14);
        g.strokeLine(px + 24, py + 22, px + 15, py + 15);
        g.strokeLine(px + 24, py + 19, px + 33, py + 12);
        g.strokeLine(px + 21, py + 28, px + 13, py + 24);
        g.strokeLine(px + 27, py + 28, px + 35, py + 24);
        g.setFill(Color.web("#f4a261"));
        g.fillOval(px + 10, py + 33, 28, 8);
    }

    private void dibujarBoya(GraphicsContext g, int x, int y, boolean activa) {
        double px = x * casilla;
        double py = y * casilla;
        if (activa) {
            g.setFill(Color.web("rgba(110, 255, 160, 0.25)"));
            g.fillOval(px + 5, py + 5, 38, 38);
        }
        g.setStroke(activa ? Color.LIMEGREEN : Color.ORANGE);
        g.setLineWidth(3);
        g.strokeLine(px + 24, py + 12, px + 24, py + 34);
        g.setFill(activa ? Color.LIMEGREEN : Color.ORANGE);
        g.fillOval(px + 16, py + 10, 16, 16);
        g.setStroke(Color.web("rgba(255,255,255,0.6)"));
        g.setLineWidth(1.5);
        g.strokeArc(px + 12, py + 14, 24, 10, 0, 180, ArcType.OPEN);
    }

    private void dibujarPerla(GraphicsContext g, int x, int y) {
        double px = x * casilla;
        double py = y * casilla;
        g.setFill(Color.web("#7b2cbf"));
        g.fillArc(px + 12, py + 20, 24, 12, 0, 180, ArcType.ROUND);
        g.setFill(Color.web("#c77dff"));
        g.fillOval(px + 18, py + 14, 12, 12);
        g.setFill(Color.web("rgba(255,255,255,0.7)"));
        g.fillOval(px + 21, py + 17, 4, 4);
    }

    private void dibujarBotiquin(GraphicsContext g, int x, int y, boolean usado) {
        double px = x * casilla;
        double py = y * casilla;
        g.setFill(usado ? Color.web("#4b6b59") : Color.web("#2a9d8f"));
        g.fillRoundRect(px + 12, py + 16, 24, 18, 8, 8);
        g.setFill(Color.WHITE);
        g.fillRect(px + 22, py + 19, 4, 12);
        g.fillRect(px + 18, py + 23, 12, 4);
        g.setStroke(Color.web("#0b3d3b"));
        g.setLineWidth(2);
        g.strokeArc(px + 18, py + 12, 12, 8, 180, 180, ArcType.OPEN);
    }

    private void dibujarTiburon(GraphicsContext g, int x, int y, int numero, int vida) {
        double px = x * casilla;
        double py = y * casilla;
        g.setFill(Color.web("#d62828"));
        g.fillOval(px + 10, py + 16, 24, 14);
        g.fillPolygon(new double[]{px + 34, px + 42, px + 34}, new double[]{py + 23, py + 18, py + 30}, 3);
        g.fillPolygon(new double[]{px + 20, px + 24, px + 28}, new double[]{py + 16, py + 8, py + 16}, 3);
        g.setFill(Color.WHITE);
        g.fillOval(px + 16, py + 20, 4, 4);
        g.setFill(Color.BLACK);
        g.fillOval(px + 17, py + 21, 2, 2);
        g.setStroke(Color.WHITE);
        g.setLineWidth(1.2);
        g.strokeLine(px + 23, py + 27, px + 31, py + 27);

        g.setFill(Color.web("#114b5f"));
        g.fillRoundRect(px + 8, py + 4, 32, 4, 3, 3);
        g.setFill(Color.web("#ff7b72"));
        g.fillRoundRect(px + 8, py + 4, 32 * vida / 50.0, 4, 3, 3);
        g.setFill(Color.WHITE);
        g.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        g.fillText("" + numero, px + 21, py + 42);
    }

    private void dibujarBuzo(GraphicsContext g, int x, int y) {
        double px = x * casilla;
        double py = y * casilla;
        g.setFill(Color.web("#1d3557"));
        g.fillOval(px + 14, py + 8, 20, 20);
        g.setFill(Color.web("#8ecae6"));
        g.fillOval(px + 18, py + 12, 12, 10);
        g.setFill(Color.web("#264653"));
        g.fillRoundRect(px + 16, py + 28, 16, 10, 5, 5);
        g.fillRect(px + 12, py + 28, 4, 9);
        g.fillRect(px + 32, py + 28, 4, 9);
        g.fillRect(px + 19, py + 38, 4, 7);
        g.fillRect(px + 25, py + 38, 4, 7);
        g.setStroke(Color.web("#ffd166"));
        g.setLineWidth(2);
        g.strokeLine(px + 34, py + 18, px + 40, py + 14);
        g.strokeLine(px + 40, py + 14, px + 42, py + 16);
    }

    private void actualizar() {
        if (tablero == null) return;

        datos.setText("Vida: " + jugador.getVida() + "/100\n" +
                "Perlas: " + cantidadPiezas + "/3\n" +
                "Boyas activas: " + balizasActivas() + "/2\n" +
                "Tiburones activos: " + dronesActivos() + "/3\n" +
                "Inventario: " + nivel.getInventario().getItems().size() + "/5\n" +
                "Nivel: " + nivel.getNombre());
        aviso.setText(mensaje);

        GraphicsContext g = tablero.getGraphicsContext2D();
        dibujarFondoMarino(g);
        dibujarTablero(g);
        dibujarSubmarino(g, 10, 7);

        for (Obstaculo o : nivel.getObstaculos()) {
            if (!o.isDestruido()) dibujarCoral(g, o.getPosicionX(), o.getPosicionY());
        }
        for (CheckPoint b : nivel.getCheckPoints()) {
            dibujarBoya(g, b.getPosicionX(), b.getPosicionY(), b.isActivado());
        }
        for (int i = 0; i < piezas.length; i++) {
            if (!recogidas[i]) dibujarPerla(g, lugaresPiezas[i][0], lugaresPiezas[i][1]);
        }
        dibujarBotiquin(g, robot.getPosicionX(), robot.getPosicionY(), robot.isUsada());
        for (int i = 0; i < drones.length; i++) {
            Personaje dron = drones[i];
            if (dron.getVida() > 0) {
                dibujarTiburon(g, dron.getPosicionX(), dron.getPosicionY(), i + 1, dron.getVida());
            }
        }
        dibujarBuzo(g, jugador.getPosicionX(), jugador.getPosicionY());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
