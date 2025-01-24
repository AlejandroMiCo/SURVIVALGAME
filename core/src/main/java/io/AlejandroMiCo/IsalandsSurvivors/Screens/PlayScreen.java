package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Scenes.Hud;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.B2WorldCreator;

public class PlayScreen implements Screen {
    private IslandsSurvivors game;
    private OrthographicCamera gameCamera;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Knight knight;

    public PlayScreen(IslandsSurvivors game) {
        this.game = game;
        gameCamera = new OrthographicCamera();
        gamePort = new FitViewport(IslandsSurvivors.V_WIDTH / IslandsSurvivors.PPM,
                IslandsSurvivors.V_HEIGHT / IslandsSurvivors.PPM, gameCamera);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/ProvisionalMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / IslandsSurvivors.PPM);
        gameCamera.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, map);

        knight = new Knight(world);
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && knight.b2body.getLinearVelocity().y <= 2) {
            knight.b2body.applyLinearImpulse(new Vector2( 0, 0.1f), knight.b2body.getWorldCenter(),true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && knight.b2body.getLinearVelocity().x <= 2) {
            knight.b2body.applyLinearImpulse(new Vector2( 0.1f, 0), knight.b2body.getWorldCenter(),true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && knight.b2body.getLinearVelocity().y >= -2) {
            knight.b2body.applyLinearImpulse(new Vector2( 0, -0.1f), knight.b2body.getWorldCenter(),true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && knight.b2body.getLinearVelocity().x >= -2) {
            knight.b2body.applyLinearImpulse(new Vector2( -0.1f, 0), knight.b2body.getWorldCenter(),true);
        }
    }

    public void update(float dt) {
        handleInput(dt);

        world.step(1 / 60f, 6, 2);
        gameCamera.position.x = knight.b2body.getPosition().x;
        gameCamera.position.y = knight.b2body.getPosition().y;

        gameCamera.update();
        renderer.setView(gameCamera);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gameCamera.combined);

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

}
