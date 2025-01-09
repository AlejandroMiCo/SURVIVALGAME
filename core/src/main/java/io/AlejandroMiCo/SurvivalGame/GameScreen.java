package io.AlejandroMiCo.SurvivalGame;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private Personaje personaje;
    private VirtualJoystick joystick;

    @Override
    public void show() {
        batch = new SpriteBatch();
        joystick = new VirtualJoystick(100, 100, 50, 20);
        personaje = new Personaje(joystick);
    }

    @Override
    public void render(float delta) {
        joystick.update();
        personaje.update(delta);

        batch.begin();
        joystick.render(batch);
        personaje.render(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        personaje.dispose();
        joystick.dispose();
    }
}
