package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class TntGobling extends Enemy {

    public TntGobling(PlayScreen screen, float x, float y, Knight knight) {
        super(screen, x, y, knight, "creatures/tntGobling.png");
        this.knight = knight;

        damage = INITIAL_DAMAGE + 10;
        health = INITIAL_HEALTH + 40;
        speed = INITIAL_SPEED - 20f;
    }
}
