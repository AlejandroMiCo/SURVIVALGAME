package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class TorchGobling extends Enemy {

    private Knight knight;

    public TorchGobling(PlayScreen screen, float x, float y, Knight knight) {
        super(screen, x, y, knight, "creatures/torchGobling.png");
        this.knight = knight;

        damage = 10;
        health = 20;
    }

    @Override
    public void takeDamage(int dmg) {
        health -= dmg;
        System.out.println("¡Gobling recibió " + dmg + " de daño! Vida restante: " + health);

        // Si la vida llega a 0, destruir el enemigo
        if (health <= 0) {
            setToDestroy = true;
            knight.gainXP(20); // 🔥 Da 20 XP al jugador al morir
        }
    }

}
