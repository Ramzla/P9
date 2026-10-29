package com.example.practicas9;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Nivel {
    private final String nombre;
    private final int numero;
    private final String dificultad;
    private final List<Obstaculo> obstaculos = new ArrayList<>();
    private final List<CheckPoint> checkPoints = new ArrayList<>();
    private final List<ElementoDinamico> elementosDinamicos = new ArrayList<>();
    private final Inventario inventario;
    private final Random azar = new Random();

    public Nivel(String nombre, int numero, String dificultad, Inventario inventario) {
        this.nombre = Objects.requireNonNull(nombre);
        this.numero = numero;
        this.dificultad = Objects.requireNonNull(dificultad);
        this.inventario = Objects.requireNonNull(inventario);
    }

    public String getNombre() { return nombre; }
    public int getNumero() { return numero; }
    public String getDificultad() { return dificultad; }
    public Inventario getInventario() { return inventario; }
    public List<Obstaculo> getObstaculos() { return Collections.unmodifiableList(obstaculos); }
    public List<CheckPoint> getCheckPoints() { return Collections.unmodifiableList(checkPoints); }
    public List<ElementoDinamico> getElementosDinamicos() { return Collections.unmodifiableList(elementosDinamicos); }

    public void agregarObstaculo(Obstaculo obstaculo) { obstaculos.add(Objects.requireNonNull(obstaculo)); }
    public void agregarCheckPoint(CheckPoint checkPoint) { checkPoints.add(Objects.requireNonNull(checkPoint)); }
    public void agregarElementoDinamico(ElementoDinamico elemento) { elementosDinamicos.add(Objects.requireNonNull(elemento)); }

    public void moverElementosDinamicos(String direccion, int distancia) {
        for (ElementoDinamico elemento : elementosDinamicos) elemento.mover(direccion, distancia);
    }

    public void moverElementosDinamicos() {
        String[] direcciones = {"norte", "sur", "este", "oeste"};
        for (ElementoDinamico elemento : elementosDinamicos) {
            elemento.mover(direcciones[azar.nextInt(direcciones.length)], 1 + azar.nextInt(3));
        }
    }

    public void mostrarEstado() {
        System.out.println("NIVEL " + numero + " · " + nombre + " · " + dificultad);
        obstaculos.forEach(o -> System.out.println("Obstáculo: " + o));
        checkPoints.forEach(c -> System.out.println("Boya: " + c));
        elementosDinamicos.forEach(e -> System.out.println("Dinámico: " + e));
        inventario.listarItems();
    }
}
