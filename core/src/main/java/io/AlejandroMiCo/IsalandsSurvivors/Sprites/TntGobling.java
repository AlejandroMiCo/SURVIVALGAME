package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class TntGobling extends Enemy {

    /**
     * Constructor de TntGobling.
     * 
     * @param screen Pantalla de juego.
     * @param x      Posición X.
     * @param y      Posición Y.
     * @param player Instancia del personaje principal.
     */
    public TntGobling(PlayScreen screen, float x, float y, Player player) {
        super(screen, x, y, player, "creatures/tntGobling.png", 50);

        // Cambiar estadisticas en funcion del enemigo en concreto
        damage = INITIAL_DAMAGE + 10;
        health = INITIAL_HEALTH + 40;
        speed = INITIAL_SPEED - 20f;
    }

    @Override
    public void reset() {
        super.reset();
    }
}
