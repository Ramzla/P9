package com.example.practicas9;

public class CheckPoint extends EntidadMapa {
    private boolean activado;

    public CheckPoint(String nombre, int posicionX, int posicionY) {
        super(nombre, posicionX, posicionY);
    }

    public boolean isActivado() { return activado; }
    public void activar() { activado = true; }
    @Override public String getSimbolo() { return "B"; }
    @Override public String toString() {
        return getNombre() + " (" + getPosicionX() + "," + getPosicionY() + ") [activado=" + activado + "]";
    }
}
