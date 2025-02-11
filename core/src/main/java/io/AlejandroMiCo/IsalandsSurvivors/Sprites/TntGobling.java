package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class TntGobling extends Enemy {

    public TntGobling(PlayScreen screen, float x, float y, Knight knight) {
        super(screen, x, y, knight, "creatures/tntGobling.png");
        this.knight = knight;

        damage = 10;
        health = 40;
        speed = 60f;
    }
}
