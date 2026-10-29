# Práctica 9 - Rescate Marino

Rescate Marino es un videojuego sencillo hecho con Java y JavaFX. 
Está basado en el diagrama de clases de la práctica.

## Objetivo

Controlas a un buzo que debe recoger tres perlas (azul, roja y verde) y llegar al submarino. 

En el mapa hay seis tiburones que se mueven y pueden quitarle vidas, corales que bloquean el camino y un botiquín para recuperar una vida.

El buzo empieza con 3 vidas. El mapa tiene 12 filas y 12 columnas y contiene 25 corales.

La boya funciona como un punto de guardado. 
Cuando el buzo pasa por ella, se guarda el estado de ese momento: su posición, vidas, perlas recogidas, posición y vida de los tiburones, y estado del botiquín. 
Si pierde sus vidas, regresa a ese punto con lo que tenía guardado. 
La boya solamente puede rescatarlo una vez. Si vuelve a quedarse sin vidas, pierde la partida.

Para ganar no es obligatorio usar la boya; basta con recoger las tres perlas y llegar al submarino.

## Controles

- W, A, S y D: mover.
- Espacio: atacar a un tiburón cercano.
- F: usar el botiquín cercano para recuperar una vida.
- R: reiniciar la partida.

## Explicación de las clases

Main.java
Es la clase principal. Abre la ventana con JavaFX, muestra el mapa y controla los movimientos, ataques, objetos y reglas del juego. 
Incluye EstadoBoya, que guarda el progreso del checkpoint.

EntidadMapa.java
Es una clase abstracta que contiene el nombre y la posición de los elementos. 
Permite reutilizar métodos y evitar código repetido.

Personaje.java
Representa al buzo y los seis tiburones. 
Hereda de EntidadMapa y permite moverse, recibir daño, recuperar vida y ser destruido.

Arma.java
Representa el arpón del buzo. Tiene nombre, daño y alcance. Implementa Inventariable.

Recompensa.java
Representa las tres perlas. Cada una tiene nombre, valor y tipo, y puede guardarse en el inventario.

Obstaculo.java
Representa los corales que bloquean el paso. Hereda de EntidadMapa e implementa Destruible.

CheckPoint.java
Representa la boya que permite guardar el progreso y recuperarlo una sola vez después de morir.

Utileria.java
Representa el botiquín marino. Permite recuperar una vida y solo puede utilizarse una vez.

Inventario.java
Administra los objetos recogidos, como el arpón y las perlas. Permite agregar, eliminar y consultar objetos.

Nivel.java
Organiza los elementos del escenario, como obstáculos, checkpoints, personajes e inventario. 
También permite mover los elementos dinámicos.

## Interfaces
- Inventariable.java: define registrar() y borrar(). La usan Arma y Recompensa.
- Destruible.java: define destruye(). La usan Personaje y Obstaculo.
- ElementoDinamico.java: define mover(direccion, distancia). La usan Personaje y Utileria.