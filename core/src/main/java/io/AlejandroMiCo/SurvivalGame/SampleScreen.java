package io.AlejandroMiCo.SurvivalGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SampleScreen implements Screen {
    private Texture image;
    private OrthographicCamera camera;
    private Personaje pj;
    private Main game;
    private Viewport viewport;
    private World world;

    public SampleScreen(Main game) {
        super();
        Box2D.init();
        world = new World(new Vector2(0,0), true);
        Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
        
        this.game = game;
        image = new Texture("pj.png");
        camera = new OrthographicCamera();
        pj = new Personaje();
        viewport = new FitViewport(800, 480,camera);



    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1/60f, 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        image.dispose();
    }
}
