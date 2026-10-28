package com.example.practicas9;

public class PruebaModelo {
    public static void main(String[] args) {
        Inventario inventario = new Inventario(3);
        Arma laser = new Arma("Laser", 25, 1);
        Recompensa bateria = new Recompensa("Bateria", 100, "energia");
        assert inventario.agregarItem(laser);
        assert inventario.agregarItem(bateria);
        assert inventario.getItems().size() == 2;
        assert inventario.eliminarItem(bateria);
        assert inventario.getItems().size() == 1;

        Personaje persona = new Personaje("Buzo", 100, 4, 4);
        persona.mover("norte", 2);
        assert persona.getPosicionY() == 2;
        persona.recibirDaño(30);
        assert persona.getVida() == 70;
        persona.reparar(20);
        assert persona.getVida() == 90;

        Obstaculo roca = new Obstaculo("Roca", 10, 5, 5);
        roca.destruye();
        assert roca.isDestruido();
        CheckPoint baliza = new CheckPoint("Boya", 1, 1);
        baliza.activar();
        assert baliza.isActivado();
        Utileria robot = new Utileria("Robot", "Reparacion", 3, 3);
        robot.usar();
        assert robot.isUsada();

        Nivel nivel = new Nivel("Estacion", 1, "Normal", inventario);
        nivel.agregarObstaculo(roca);
        nivel.agregarCheckPoint(baliza);
        nivel.agregarElementoDinamico(persona);
        nivel.agregarElementoDinamico(robot);
        nivel.moverElementosDinamicos("este", 2);
        assert persona.getPosicionX() == 6;
        assert robot.getPosicionX() == 5;
        nivel.moverElementosDinamicos();

        System.out.println("Pruebas de modelo terminadas correctamente.");
    }
}
