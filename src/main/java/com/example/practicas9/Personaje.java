package com.example.practicas9;

public class Personaje extends EntidadMapa implements Destruible, ElementoDinamico {
    private int vida;

    public Personaje(String nombre, int vida, int posicionX, int posicionY) {
        super(nombre, posicionX, posicionY);
        this.vida = Math.max(0, vida);
    }

    public int getVida() { return vida; }

    public void recibirDaño(int daño) {
        if (daño < 0) throw new IllegalArgumentException("El daño no puede ser negativo");
        vida = Math.max(0, vida - daño);
        if (vida == 0) destruye();
    }

    public void reparar(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("Cantidad inválida");
        vida = Math.min(100, vida + cantidad);
    }

    @Override public void mover(String direccion, int distancia) { desplazar(direccion, distancia); }
    @Override public void destruye() { vida = 0; }
    @Override public String getSimbolo() { return "P"; }
    @Override public String toString() {
        return getNombre() + " (" + getPosicionX() + "," + getPosicionY() + ") [vida=" + vida + "]";
    }
}
