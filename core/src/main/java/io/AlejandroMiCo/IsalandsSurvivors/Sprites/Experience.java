package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.physics.box2d.World;

public class Experience extends CollectedItem {
    public Experience(World world, float x, float y, Knight knight) {
        super(world, x, y, knight, "img/W_Spawn.png");
    }
}
