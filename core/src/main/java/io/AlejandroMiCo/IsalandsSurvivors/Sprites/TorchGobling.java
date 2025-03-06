package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class TorchGobling extends Enemy {
    /**
     * Constructor de TorchGobling.
     * 
     * @param screen Pantalla de juego.
     * @param x      Posición X.
     * @param y      Posición Y.
     * @param player Instancia del personaje principal.
     */
    public TorchGobling(PlayScreen screen, float x, float y, Player player) {
        super(screen, x, y, player, "creatures/torchGobling.png", 30);
        this.player = player;

        // Cambiar estadisticas en funcion del enemigo en concreto
        damage = INITIAL_DAMAGE + 10;
        health = INITIAL_HEALTH + 20;
    }

    @Override
    public void reset() {
        super.reset();
    }
}
