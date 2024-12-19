package io.AlejandroMiCo.SurvivalGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SampleScreen implements Screen {
    private Texture image;
    private OrthographicCamera camera;
    private Personaje pj;
    private Panda panda;
    private Main game;
    private Viewport viewport;
    private World world;
    private float acumulator;

    public SampleScreen(Main game) {
        super();
        Box2D.init();
        world = new World(new Vector2(0,0), true);
        Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
        
        this.game = game;
        image = new Texture("pj.png");
        camera = new OrthographicCamera();
        pj = new Personaje();
        panda = new Panda(100, 280);
        viewport = new FitViewport(800, 480,camera);



    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // game.batch.begin();
        // game.batch.setProjectionMatrix(camera.combined);
        // camera.update();
        // pj.render(game.batch);
        // panda.render(game.batch);
        // game.batch.end();

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


    private void stepFunction(float deltaTime){
        float frameTime = Math.min(deltaTime, 0.25f);
        acumulator += frameTime;
        
    }
}
