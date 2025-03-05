package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.physics.box2d.World;

public class Experience extends CollectedItem {
    public int expValue;

    public Experience(World world, float x, float y, Player knight, int value) {
        super(world, x, y, knight, "img/W_Spawn.png");
        this.expValue = value;

    }

    public int getExpValue() {
        return expValue;
    }
}
