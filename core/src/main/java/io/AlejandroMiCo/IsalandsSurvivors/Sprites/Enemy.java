package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public abstract class Enemy extends Sprite{
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public boolean setToDestroy;
    public boolean destroyed;

    public int health;

    public Enemy(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        defineEnemy();
        setPosition(x, y);
    }

    protected abstract void defineEnemy();
}
