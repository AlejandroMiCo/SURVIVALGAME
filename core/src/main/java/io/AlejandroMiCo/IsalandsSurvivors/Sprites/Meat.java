package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.physics.box2d.World;

public class Meat extends CollectedItem {

    public Meat(World world, float x, float y, Knight knight) {
        super(world, x, y, knight, "img/W_Spawn.png");
    }

}
