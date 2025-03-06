package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class EnemyWarrior extends Enemy {
    /**
     * Constructor de EnemyWarrior.
     * 
     * @param screen Pantalla de juego.
     * @param x      Posición X.
     * @param y      Posición Y.
     * @param player Instancia del personaje principal.
     */
    public EnemyWarrior(PlayScreen screen, float x, float y, Player player) {
        // Inicializar estadisticas de la coco con las de un enemigo y establece la
        // imagen
        super(screen, x, y, player, "creatures/redWarrior.png", 200);
        this.player = player;

        // Cambiar estadisticas en funcion del enemigo en concreto
        damage = INITIAL_DAMAGE + 40;
        health = INITIAL_HEALTH + 100;
        speed = INITIAL_SPEED * 1.1F;
    }

    @Override
    public void reset() {
        super.reset();
    }
}
