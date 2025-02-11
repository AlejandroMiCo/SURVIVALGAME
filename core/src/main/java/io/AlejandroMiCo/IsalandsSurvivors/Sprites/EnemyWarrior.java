package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class EnemyWarrior extends Enemy {
    public EnemyWarrior(PlayScreen screen, float x, float y, Knight knight) {
        super(screen, x, y, knight, "creatures/redWarrior.png");
        this.knight = knight;

        damage = 40;
        health = 100;
        speed = 120;
    }
}
