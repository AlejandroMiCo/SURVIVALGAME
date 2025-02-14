package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.physics.box2d.World;

public class Coin extends CollectedItem {

    public Coin(World world, float x, float y, Knight knight) {
        super(world, x, y, knight, "img/G_Spawn.png");
    }
}
