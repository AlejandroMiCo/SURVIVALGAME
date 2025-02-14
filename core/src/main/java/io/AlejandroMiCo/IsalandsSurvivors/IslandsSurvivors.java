package io.AlejandroMiCo.IsalandsSurvivors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.MainMenuScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class IslandsSurvivors extends Game {
    public static final float V_WIDTH = 820;
    public static final float V_HEIGHT = 400;
    public static final float PPM = 100;
    public static final short DEFAULT_BIT = 1;
    public static final short PLAYER_BIT = 2;
    public static final short ENEMY_BIT = 4;
    public static final short BULLET_BIT = 8;
    public static final short ITEM_BIT = 16;

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
