package io.AlejandroMiCo.SurvivalGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render(); // Llama al método render() de la pantalla actual
    }

    @Override
    public void dispose() {
        batch.dispose();
        getScreen().dispose();
    }
}
