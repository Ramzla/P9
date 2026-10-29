package com.example.practicas9;

public class Arma implements Inventariable {
    private final String nombre;
    private final int daño;
    private final double alcance;

    public Arma(String nombre, int daño, double alcance) {
        this.nombre = nombre;
        this.daño = daño;
        this.alcance = alcance;
    }

    public String getNombre() { return nombre; }
    public int getDaño() { return daño; }
    public double getAlcance() { return alcance; }

    @Override public void registrar() { System.out.println("Arma equipada: " + nombre); }
    @Override public void borrar() { System.out.println("Arma retirada: " + nombre); }
    @Override public String toString() { return nombre + " [daño " + daño + ", alcance " + alcance + "]"; }
}
