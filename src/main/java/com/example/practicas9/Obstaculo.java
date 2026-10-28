package com.example.practicas9;

public class Obstaculo extends EntidadMapa implements Destruible {
    private final int daño;
    private boolean destruido;

    public Obstaculo(String nombre, int daño, int posicionX, int posicionY) {
        super(nombre, posicionX, posicionY);
        this.daño = daño;
    }

    public int getDaño() { return daño; }
    public boolean isDestruido() { return destruido; }
    @Override public void destruye() { destruido = true; }
    @Override public String getSimbolo() { return "#"; }
    @Override public String toString() {
        return getNombre() + " (" + getPosicionX() + "," + getPosicionY() + ") [destruido=" + destruido + "]";
    }
}
