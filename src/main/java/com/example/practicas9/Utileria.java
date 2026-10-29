package com.example.practicas9;

public class Utileria extends EntidadMapa implements ElementoDinamico {
    private final String descripcion;
    private boolean usada;

    public Utileria(String nombre, String descripcion, int posicionX, int posicionY) {
        super(nombre, posicionX, posicionY);
        this.descripcion = descripcion;
    }

    public String getDescripcion() { return descripcion; }
    public boolean isUsada() { return usada; }
    public void usar() { usada = true; }
    @Override public void mover(String direccion, int distancia) { desplazar(direccion, distancia); }
    @Override public String getSimbolo() { return "S"; }
    @Override public String toString() {
        return getNombre() + " (" + getPosicionX() + "," + getPosicionY() + ") [usada=" + usada + "]";
    }
}
