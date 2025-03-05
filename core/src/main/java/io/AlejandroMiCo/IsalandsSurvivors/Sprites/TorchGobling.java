package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class TorchGobling extends Enemy {
    public TorchGobling(PlayScreen screen, float x, float y, Player knight) {
        super(screen, x, y, knight, "creatures/torchGobling.png", 30);
        this.knight = knight;

        damage = INITIAL_DAMAGE + 10;
        health = INITIAL_HEALTH + 20;
    }

    @Override
    public void reset() {
        super.reset();
    }
}
