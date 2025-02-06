package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public boolean setToDestroy;
    public boolean destroyed;

    public static int INITIAL_HEALTH = 20;
    public static int INITIAL_DAMAGE = 5;
    public static float INITIAL_SPEED = 75;

    protected int health;
    protected int damage;
    protected float speed;

    public Enemy(PlayScreen screen, float x, float y) {
        this.health = INITIAL_HEALTH;
        this.damage = INITIAL_DAMAGE;
        this.speed = INITIAL_SPEED;
        this.world = screen.getWorld();
        this.screen = screen;


        defineEnemy();
        setPosition(x, y);
    }

    protected abstract void defineEnemy();

    public abstract void takeDamage(int dmg);
}
