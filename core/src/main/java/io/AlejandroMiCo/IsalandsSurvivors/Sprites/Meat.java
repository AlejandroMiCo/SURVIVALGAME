package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.physics.box2d.World;

public class Meat extends CollectedItem {

    /**
     * Constructor de la carne.
     * 
     * @param world  Mundo físico.
     * @param x      Posición X.
     * @param y      Posición Y.
     * @param player Instancia del personaje principal.
     */
    public Meat(World world, float x, float y, Player player) {
        super(world, x, y, player, "img/M_Spawn.png");
    }

}
