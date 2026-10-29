package com.example.practicas9;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class Main extends Application {
    private final int columnas = 12, filas = 12, casilla = 48, maxVidas = 3;
    private final int[][] lugaresPerlas = {{5, 1}, {9, 2}, {2, 9}};
    private final int[][] lugaresTiburones = {
            {8, 2}, {9, 4}, {3, 6}, {9, 8}, {5, 9}, {2, 7}
    };
    private final int[][] corales = {
            {2, 3}, {3, 3}, {4, 3}, {6, 2}, {6, 3}, {6, 4},
            {8, 4}, {4, 6}, {5, 6}, {7, 7}, {2, 5}, {3, 5},
            {7, 5}, {9, 5}, {2, 8}, {4, 8}, {6, 8}, {7, 8},
            {4, 9}, {6, 9}, {2, 10}, {6, 10}, {8, 10}, {9, 7}, {8, 3}
    };
    private final Random azar = new Random();

    private Canvas tablero;
    private Label aviso;
    private Label[] corazones = new Label[3];
    private ImageView[] iconosPerlas = new ImageView[3];
    private ImageView iconoBoya;
    private Tooltip ayudaBoya;
    private int vidasAnteriores = 3;

    private Image boya, buzo, botiquinImagen, submarino, coral, tiburonImagen;
    private Image[] imagenesPerlas;
    private Nivel nivel;
    private Personaje jugador;
    private Personaje[] tiburones;
    private Utileria botiquin;
    private Arma arma;
    private Recompensa[] perlas;
    private boolean[] recogidas;
    private int cantidadPerlas;
    private boolean termino, boyaUsada;
    private String mensaje;
    private EstadoBoya estadoBoya;

    @Override
    public void start(Stage ventana) {
        iniciarJuego();
        cargarImagenes();
        tablero = new Canvas(columnas * casilla, filas * casilla);

        BorderPane pantalla = new BorderPane();
        pantalla.setCenter(tablero);
        pantalla.setRight(crearPanel());

        Scene escena = new Scene(pantalla);
        escena.setOnKeyPressed(e -> {
            KeyCode tecla = e.getCode();
            switch (tecla) {
                case W -> moverJugador("norte");
                case S -> moverJugador("sur");
                case A -> moverJugador("oeste");
                case D -> moverJugador("este");
                case SPACE -> atacar();
                case F -> usarBotiquin();
                case R -> reiniciar();
                default -> { }
            }
        });

        ventana.setTitle("Práctica 9 - Rescate Marino");
        ventana.setResizable(false);
        ventana.setScene(escena);
        ventana.show();
        actualizar();
    }

    private VBox crearPanel() {
        Label titulo = new Label("Rescate Marino");
        titulo.setFont(Font.font("Arial", 22));
        Label objetivo = new Label("Recoge 3 perlas y llega al submarino. La boya guarda tu progreso una sola vez.");
        objetivo.setWrapText(true);

        HBox filaVidas = fila("Vidas:", 5);
        ((Label) filaVidas.getChildren().get(0)).setStyle("-fx-font-size: 16px;");
        for (int i = 0; i < corazones.length; i++) {
            Label corazon = new Label("♥");
            corazon.setFont(Font.font("Arial", 25));
            corazon.setMinWidth(23);
            corazones[i] = corazon;
            filaVidas.getChildren().add(corazon);
        }

        HBox filaPerlas = fila("Perlas:", 8);
        for (int i = 0; i < perlas.length; i++) {
            ImageView icono = icono(imagenesPerlas[i], 37);
            icono.setFitHeight(35);
            Tooltip.install(icono, new Tooltip(perlas[i].getNombre()));
            iconosPerlas[i] = icono;
            filaPerlas.getChildren().add(icono);
        }

        HBox filaBoya = fila("Boya:", 8);
        iconoBoya = icono(boya, 32);
        iconoBoya.setFitHeight(38);
        ayudaBoya = new Tooltip();
        Tooltip.install(iconoBoya, ayudaBoya);
        filaBoya.getChildren().add(iconoBoya);

        Label tituloControles = new Label("Controles");
        tituloControles.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        VBox controles = new VBox(4, tituloControles,
                control("teclas", 105, "Mover", 12),
                control("espacio", 110, "Atacar", 7),
                control("f", 48, "Curar", 12),
                control("r", 48, "Reiniciar", 12));

        aviso = new Label();
        aviso.setWrapText(true);
        aviso.setMinHeight(48);
        aviso.setStyle("-fx-font-size: 13px; -fx-text-fill: #235f7b;");

        VBox panel = new VBox(7, titulo, objetivo, filaVidas, filaPerlas,
                filaBoya, controles, aviso);
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(305);
        panel.setMinHeight(filas * casilla);
        panel.setStyle("-fx-background-color: #eaf7ff;");
        return panel;
    }

    private HBox fila(String texto, int separacion) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-size: 15px;");
        HBox fila = new HBox(separacion, etiqueta);
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    private HBox control(String imagen, int ancho, String accion, int espacio) {
        HBox fila = new HBox(espacio, icono(cargarImagen(imagen), ancho), new Label(accion));
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    private ImageView icono(Image imagen, int ancho) {
        ImageView vista = new ImageView(imagen);
        vista.setFitWidth(ancho);
        vista.setPreserveRatio(true);
        return vista;
    }

    private void iniciarJuego() {
        Inventario inventario = new Inventario(5);
        arma = new Arma("Arpón", 25, 1);
        inventario.agregarItem(arma);

        nivel = new Nivel("Arrecife profundo", 1, "Normal", inventario);
        jugador = new Personaje("Buzo", maxVidas, 1, 1);
        nivel.agregarElementoDinamico(jugador);

        tiburones = new Personaje[lugaresTiburones.length];
        for (int i = 0; i < tiburones.length; i++) {
            tiburones[i] = new Personaje("Tiburón " + (i + 1), 50,
                    lugaresTiburones[i][0], lugaresTiburones[i][1]);
            nivel.agregarElementoDinamico(tiburones[i]);
        }
        botiquin = new Utileria("Botiquín marino", "Recupera 1 vida", 3, 7);
        nivel.agregarElementoDinamico(botiquin);
        nivel.agregarCheckPoint(new CheckPoint("Boya de guardado", 8, 9));

        for (int[] p : corales) {
            nivel.agregarObstaculo(new Obstaculo("Coral", 10, p[0], p[1]));
        }

        perlas = new Recompensa[] {
                new Recompensa("Perla azul", 100, "tesoro"),
                new Recompensa("Perla roja", 100, "tesoro"),
                new Recompensa("Perla verde", 100, "tesoro")
        };
        recogidas = new boolean[3];
        cantidadPerlas = 0;
        termino = false;
        boyaUsada = false;
        estadoBoya = null;
        mensaje = "Recoge las 3 perlas. Puedes usar la boya una vez para guardar tu progreso.";
    }

    private void reiniciar() {
        iniciarJuego();
        vidasAnteriores = maxVidas;
        actualizar();
    }

    private boolean lugarLibre(int x, int y) {
        if (x < 1 || y < 1 || x >= columnas - 1 || y >= filas - 1) return false;
        for (Obstaculo obstaculo : nivel.getObstaculos()) {
            if (!obstaculo.isDestruido() && mismaCasilla(obstaculo, x, y)) return false;
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

    private boolean mismaCasilla(EntidadMapa objeto, int x, int y) {
        return objeto.getPosicionX() == x && objeto.getPosicionY() == y;
    }

    private int distancia(EntidadMapa a, EntidadMapa b) {
        return Math.abs(a.getPosicionX() - b.getPosicionX())
                + Math.abs(a.getPosicionY() - b.getPosicionY());
    }

    private void moverJugador(String direccion) {
        if (termino) return;
        int[] nuevo = siguienteLugar(direccion, jugador.getPosicionX(), jugador.getPosicionY());
        if (!lugarLibre(nuevo[0], nuevo[1])) {
            mensaje = "No puedes pasar por ahí.";
        } else {
            jugador.mover(direccion, 1);
            mensaje = "Te moviste al " + direccion + ".";
            revisarObjetivos();
            moverTiburones();
            revisarVida();
        }
        actualizar();
    }

    private boolean hayOtroTiburon(Personaje actual, int x, int y) {
        for (Personaje otro : tiburones) {
            if (otro != actual && otro.getVida() > 0 && mismaCasilla(otro, x, y)) return true;
        }
        return false;
    }

    private boolean morder(Personaje tiburon) {
        if (!mismaCasilla(tiburon, jugador.getPosicionX(), jugador.getPosicionY())) return false;
        jugador.recibirDaño(1);
        mensaje = "¡" + tiburon.getNombre() + " te mordió! Perdiste 1 vida.";
        return true;
    }

    private void moverTiburones() {
        if (termino) return;
        String[] direcciones = {"norte", "sur", "este", "oeste"};
        boolean mordido = false;
        for (Personaje tiburon : tiburones) {
            if (tiburon.getVida() == 0) continue;
            if (!mordido) mordido = morder(tiburon);
            String direccion = direcciones[azar.nextInt(4)];
            int[] nuevo = siguienteLugar(direccion, tiburon.getPosicionX(), tiburon.getPosicionY());
            if (lugarLibre(nuevo[0], nuevo[1]) && !hayOtroTiburon(tiburon, nuevo[0], nuevo[1])) {
                tiburon.mover(direccion, 1);
            }
            if (!mordido) mordido = morder(tiburon);
        }
    }

    private void atacar() {
        if (termino) return;
        Personaje objetivo = null;
        int menorDistancia = Integer.MAX_VALUE;
        for (Personaje tiburon : tiburones) {
            int d = distancia(jugador, tiburon);
            if (tiburon.getVida() > 0 && d <= arma.getAlcance() && d < menorDistancia) {
                objetivo = tiburon;
                menorDistancia = d;
            }
        }
        if (objetivo == null) {
            mensaje = "Para atacar, acércate a cualquiera de los tiburones.";
        } else {
            objetivo.recibirDaño(arma.getDaño());
            mensaje = objetivo.getVida() == 0
                    ? "¡" + objetivo.getNombre() + " fue derrotado!"
                    : "Impactaste a " + objetivo.getNombre() + ". Vida restante: " + objetivo.getVida();
        }
        moverTiburones();
        revisarVida();
        actualizar();
    }

    private void usarBotiquin() {
        if (termino) return;
        if (distancia(jugador, botiquin) > 1 || botiquin.isUsada()) {
            mensaje = "Acércate al botiquín verde. Solo puedes usarlo una vez.";
        } else if (jugador.getVida() == maxVidas) {
            mensaje = "Ya tienes las 3 vidas completas.";
        } else {
            botiquin.usar();
            jugador.reparar(1);
            mensaje = "Recuperaste una vida. Vidas: " + jugador.getVida() + "/" + maxVidas;
        }
        actualizar();
    }

    private void revisarObjetivos() {
        int x = jugador.getPosicionX(), y = jugador.getPosicionY();
        for (int i = 0; i < perlas.length; i++) {
            if (!recogidas[i] && lugaresPerlas[i][0] == x && lugaresPerlas[i][1] == y
                    && nivel.getInventario().agregarItem(perlas[i])) {
                recogidas[i] = true;
                cantidadPerlas++;
                mensaje = "Recogiste " + perlas[i].getNombre() + ".";
            }
        }

        CheckPoint boyaGuardado = nivel.getCheckPoints().get(0);
        if (!boyaGuardado.isActivado() && !boyaUsada && mismaCasilla(boyaGuardado, x, y)) {
            estadoBoya = new EstadoBoya(jugador, tiburones, botiquin, recogidas);
            boyaGuardado.activar();
            mensaje = "¡Checkpoint guardado! La boya podrá salvarte una vez.";
        }

        if (x == 10 && y == 10) {
            if (cantidadPerlas == 3) {
                termino = true;
                mensaje = "¡Ganaste! Llegaste al submarino con las tres perlas.";
            } else {
                mensaje = "Faltan perlas para subir al submarino.";
            }
        }
    }

    private static class EstadoBoya {
        final int x, y, vidas, botiquinX, botiquinY;
        final boolean botiquinUsado;
        final boolean[] perlas;
        final int[][] tiburones;

        EstadoBoya(Personaje buzo, Personaje[] enemigos, Utileria botiquin, boolean[] recogidas) {
            x = buzo.getPosicionX();
            y = buzo.getPosicionY();
            vidas = buzo.getVida();
            perlas = recogidas.clone();
            botiquinX = botiquin.getPosicionX();
            botiquinY = botiquin.getPosicionY();
            botiquinUsado = botiquin.isUsada();
            tiburones = new int[enemigos.length][3];
            for (int i = 0; i < enemigos.length; i++) {
                tiburones[i][0] = enemigos[i].getPosicionX();
                tiburones[i][1] = enemigos[i].getPosicionY();
                tiburones[i][2] = enemigos[i].getVida();
            }
        }
    }

    private void regresarAlCheckpoint() {
        EstadoBoya guardado = estadoBoya;
        iniciarJuego();
        jugador.setPosicion(guardado.x, guardado.y);
        jugador.recibirDaño(maxVidas - guardado.vidas);
        for (int i = 0; i < tiburones.length; i++) {
            tiburones[i].setPosicion(guardado.tiburones[i][0], guardado.tiburones[i][1]);
            tiburones[i].recibirDaño(50 - guardado.tiburones[i][2]);
        }
        botiquin.setPosicion(guardado.botiquinX, guardado.botiquinY);
        if (guardado.botiquinUsado) botiquin.usar();
        for (int i = 0; i < perlas.length; i++) {
            if (guardado.perlas[i]) {
                nivel.getInventario().agregarItem(perlas[i]);
                recogidas[i] = true;
                cantidadPerlas++;
            }
        }
        nivel.getCheckPoints().get(0).activar();
        boyaUsada = true;
        estadoBoya = null;
        mensaje = "¡La boya te salvó! Volviste al estado guardado. Ya no puedes usarla otra vez.";
    }

    private void revisarVida() {
        if (jugador.getVida() > 0) return;
        if (estadoBoya != null && !boyaUsada) {
            regresarAlCheckpoint();
        } else {
            termino = true;
            mensaje = "Te quedaste sin vidas. Presiona R para volver a jugar.";
        }
    }

    private void cargarImagenes() {
        boya = cargarImagen("boya");
        buzo = cargarImagen("buzo");
        botiquinImagen = cargarImagen("botiquin");
        submarino = cargarImagen("submarino");
        coral = cargarImagen("coral");
        tiburonImagen = cargarImagen("tiburon");
        imagenesPerlas = new Image[] {
                cargarImagen("perlaazul"), cargarImagen("perlaroja"), cargarImagen("perlaverde")
        };
    }

    private Image cargarImagen(String nombre) {
        return new Image(Main.class.getResource("/imagenes/" + nombre + ".png").toExternalForm());
    }

    private void dibujar(GraphicsContext g, Image imagen, int x, int y, int ancho, int alto) {
        double px = x * casilla + (casilla - ancho) / 2.0;
        double py = y * casilla + (casilla - alto) / 2.0;
        g.drawImage(imagen, px, py, ancho, alto);
    }

    private void dibujarTablero(GraphicsContext g) {
        g.setFill(Color.web("#87d4ed"));
        g.fillRect(0, 0, tablero.getWidth(), tablero.getHeight());
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                Color color = x == 0 || y == 0 || x == columnas - 1 || y == filas - 1
                        ? Color.web("#167caa")
                        : (x + y) % 2 == 0 ? Color.web("#c5f0fb") : Color.web("#ade6f5");
                g.setFill(color);
                g.fillRect(x * casilla + 1, y * casilla + 1, casilla - 2, casilla - 2);
            }
        }
    }

    private void dibujarTiburon(GraphicsContext g, Personaje enemigo, int numero) {
        int x = enemigo.getPosicionX(), y = enemigo.getPosicionY();
        dibujar(g, tiburonImagen, x, y, 45, 23);
        g.setFill(Color.DARKRED);
        g.fillRect(x * casilla + 9, y * casilla + 5, 30, 4);
        g.setFill(Color.LIMEGREEN);
        g.fillRect(x * casilla + 9, y * casilla + 5, 30 * enemigo.getVida() / 50.0, 4);
        g.setFill(Color.BLACK);
        g.setFont(Font.font("Arial", 11));
        g.fillText(String.valueOf(numero), x * casilla + 22, y * casilla + 44);
    }

    private void actualizar() {
        if (tablero == null) return;

        int vidas = jugador.getVida();
        for (int i = 0; i < corazones.length; i++) {
            Label corazon = corazones[i];
            corazon.setTextFill(i < vidas ? Color.web("#ce304c") : Color.web("#b1c7d1"));
            if (vidas != vidasAnteriores && i == (vidas < vidasAnteriores ? vidas : vidas - 1)) {
                ScaleTransition efecto = new ScaleTransition(Duration.millis(180), corazon);
                efecto.setFromX(1.6);
                efecto.setFromY(1.6);
                efecto.setToX(1);
                efecto.setToY(1);
                efecto.play();
            }
        }
        vidasAnteriores = vidas;

        for (int i = 0; i < iconosPerlas.length; i++) {
            iconosPerlas[i].setOpacity(recogidas[i] ? 1 : 0.25);
        }
        iconoBoya.setOpacity(boyaUsada ? 0.35 : estadoBoya != null ? 1 : 0.25);
        ayudaBoya.setText(boyaUsada ? "Checkpoint utilizado. Ya no queda ningún rescate."
                : estadoBoya != null ? "Progreso guardado. Te rescatará una vez si mueres."
                : "Toca la boya para guardar tu progreso una vez.");
        aviso.setText(mensaje);

        GraphicsContext g = tablero.getGraphicsContext2D();
        dibujarTablero(g);
        dibujar(g, submarino, 10, 10, 45, 42);
        for (Obstaculo o : nivel.getObstaculos()) {
            if (!o.isDestruido()) dibujar(g, coral, o.getPosicionX(), o.getPosicionY(), 38, 43);
        }
        for (CheckPoint b : nivel.getCheckPoints()) {
            int x = b.getPosicionX(), y = b.getPosicionY();
            dibujar(g, boya, x, y, 31, 43);
            if (boyaUsada) {
                g.setFill(Color.rgb(90, 90, 90, 0.35));
                g.fillRect(x * casilla + 3, y * casilla + 3, casilla - 6, casilla - 6);
            } else if (b.isActivado()) {
                g.setStroke(Color.LIMEGREEN);
                g.setLineWidth(3);
                g.strokeOval(x * casilla + 3, y * casilla + 3, casilla - 6, casilla - 6);
            }
        }
        for (int i = 0; i < perlas.length; i++) {
            if (!recogidas[i]) {
                dibujar(g, imagenesPerlas[i], lugaresPerlas[i][0], lugaresPerlas[i][1], 41, 39);
            }
        }
        dibujar(g, botiquinImagen, botiquin.getPosicionX(), botiquin.getPosicionY(), 42, 37);
        if (botiquin.isUsada()) {
            g.setFill(Color.rgb(70, 70, 70, 0.45));
            g.fillRect(botiquin.getPosicionX() * casilla + 3,
                    botiquin.getPosicionY() * casilla + 3, casilla - 6, casilla - 6);
        }
        for (int i = 0; i < tiburones.length; i++) {
            if (tiburones[i].getVida() > 0) dibujarTiburon(g, tiburones[i], i + 1);
        }
        dibujar(g, buzo, jugador.getPosicionX(), jugador.getPosicionY(), 43, 40);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
