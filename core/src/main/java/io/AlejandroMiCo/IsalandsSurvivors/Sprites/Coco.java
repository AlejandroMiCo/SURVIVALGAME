package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class Coco extends Enemy {
    public Coco(PlayScreen screen, float x, float y, Knight knight) {
        super(screen, x, y, knight, "creatures/coco.png");
        this.knight = knight;

        damage = INITIAL_DAMAGE + 5;
        health = (int) (INITIAL_HEALTH * 0.7f);
        speed = INITIAL_SPEED + 100f;
    }
}
