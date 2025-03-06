package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.physics.box2d.World;

public class Experience extends CollectedItem {
    public int expValue;

    /**
     * Constructor de la experiencia.
     * 
     * @param world  Mundo físico.
     * @param x      Posición X.
     * @param y      Posición Y.
     * @param player Instancia del personaje principal.
     * @param value  Valor de la experiencia.
     */
    public Experience(World world, float x, float y, Player player, int value) {    
        super(world, x, y, player, "img/W_Spawn.png");
        this.expValue = value;

    }

    public int getExpValue() {
        return expValue;
    }
}
