package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class EnemyWarrior extends Enemy {
    public EnemyWarrior(PlayScreen screen, float x, float y, Knight knight) {
        super(screen, x, y, knight, "creatures/redWarrior.png", 200);
        this.knight = knight;

        damage = INITIAL_DAMAGE + 40;
        health = INITIAL_HEALTH + 100;
        speed = INITIAL_SPEED *1.2F;
    }
}
