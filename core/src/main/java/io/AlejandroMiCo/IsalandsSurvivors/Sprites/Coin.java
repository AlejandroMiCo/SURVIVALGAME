package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.physics.box2d.World;

public class Coin extends CollectedItem {
    /**
     * Constructor de la moneda.
     *
     * @param world Mundo físico.
     * @param x Posición X.
     * @param y Posición Y.
     * @param player Instancia del personaje principal.
     */
    public Coin(World world, float x, float y, Player player) {
        super(world, x, y, player, "img/G_Spawn.png");
    }
}
