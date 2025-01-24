package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

public class PlayScreen implements Screen{
    private IslandsSurvivors game;
    private OrthographicCamera gameCamera;
    private Viewport gamePort;

    public PlayScreen(IslandsSurvivors game) {
        this.game = game;
        gameCamera = new OrthographicCamera();
        gamePort = new FitViewport(800, 480, gameCamera);
    }

    @Override
    public void show() {
        
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gameCamera.combined);

        game.batch.begin();

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {
        
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void hide() {
        
    }

    @Override
    public void dispose() {
       
    }
    
}
