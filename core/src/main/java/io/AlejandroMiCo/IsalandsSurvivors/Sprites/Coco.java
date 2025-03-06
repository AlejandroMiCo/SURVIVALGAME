package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class Coco extends Enemy {
    /**
     * Constructor de Coco.
     * 
     * @param screen Pantalla de juego.
     * @param x      Posición X.
     * @param y      Posición Y.
     * @param player Instancia del personaje principal.
     */
    public Coco(PlayScreen screen, float x, float y, Player player) {
        // Inicializar estadisticas de la coco con las de un enemigo y establece la
        // imagen
        super(screen, x, y, player, "creatures/coco.png", 25);
        this.player = player;

        // Cambiar estadisticas en funcion del enemigo en concreto
        damage = INITIAL_DAMAGE + 5;
        health = (int) (INITIAL_HEALTH * 0.7f);
        speed = INITIAL_SPEED + 10f;
    }

    @Override
    public void reset() {
        super.reset();
    }
}
