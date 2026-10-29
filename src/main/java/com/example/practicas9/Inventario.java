package com.example.practicas9;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventario {
    private final int capacidadMaxima;
    private final List<Inventariable> items = new ArrayList<>();

    public Inventario(int capacidadMaxima) {
        if (capacidadMaxima < 0) throw new IllegalArgumentException("Capacidad inválida");
        this.capacidadMaxima = capacidadMaxima;
    }

    public int getCapacidadMaxima() { return capacidadMaxima; }
    public List<Inventariable> getItems() { return Collections.unmodifiableList(items); }

    public boolean agregarItem(Inventariable item) {
        if (item == null || items.size() >= capacidadMaxima || items.contains(item)) return false;
        items.add(item);
        item.registrar();
        return true;
    }

    public boolean eliminarItem(Inventariable item) {
        if (!items.remove(item)) return false;
        item.borrar();
        return true;
    }

    public void listarItems() {
        System.out.println("Inventario (" + items.size() + "/" + capacidadMaxima + "):");
        for (Inventariable item : items) System.out.println(" • " + item);
    }
}
