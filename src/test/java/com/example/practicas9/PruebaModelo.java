package com.example.practicas9;

public class PruebaModelo {
    public static void main(String[] args) {
        Inventario inventario = new Inventario(3);
        Arma arpon = new Arma("Arpón", 25, 1);
        Recompensa perla = new Recompensa("Perla", 100, "tesoro");
        assert inventario.agregarItem(arpon);
        assert inventario.agregarItem(perla);
        assert inventario.getItems().size() == 2;
        assert inventario.eliminarItem(perla);
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
        Utileria botiquin = new Utileria("Botiquín", "Curación", 3, 3);
        botiquin.usar();
        assert botiquin.isUsada();

        Nivel nivel = new Nivel("Arrecife", 1, "Normal", inventario);
        nivel.agregarObstaculo(roca);
        nivel.agregarCheckPoint(baliza);
        nivel.agregarElementoDinamico(persona);
        nivel.agregarElementoDinamico(botiquin);
        nivel.moverElementosDinamicos("este", 2);
        assert persona.getPosicionX() == 6;
        assert botiquin.getPosicionX() == 5;
        nivel.moverElementosDinamicos();

        System.out.println("Pruebas de modelo terminadas correctamente.");
    }
}
