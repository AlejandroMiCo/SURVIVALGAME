package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class TorchGobling extends Enemy {
    public TorchGobling(PlayScreen screen, float x, float y, Knight knight) {
        super(screen, x, y, knight, "creatures/torchGobling.png");
        this.knight = knight;

        damage = 10;
        health = 20;
    }
}
