package io.AlejandroMiCo.SurvivalGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private OrthographicCamera camera;
    private Personaje pj;
    private Viewport myViewport;
    final int VIRTUAL_WIDTH = 800;
    final int VIRTUAL_HEIGHT = 480;
    private Stage stage;
    Texture img;
    Sprite mysprite;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private VirtualJoystick joystick;

    @Override
    public void create() {
        batch = new SpriteBatch();
        joystick = new VirtualJoystick(100, 100, 50, 20);
        camera = new OrthographicCamera();
        myViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        stage = new Stage(myViewport);

        map = new TmxMapLoader().load("maps/mapa1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
       
        pj = new Personaje(joystick);
    }

    @Override
    public void resize(int width, int height) {
        //stage.getCamera().position.set(640 / 2, 480 / 2, 0);
        myViewport.update(width, height);
    }

    @Override
    public void render() {
        joystick.update();
        pj.update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        mapRenderer.setView(camera);
        
        
        mapRenderer.render(new int[]{0});
        
        batch.begin();
        joystick.render(batch);
        pj.render(batch);
        batch.end();

        mapRenderer.render(new int[]{1});

    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();

        map.dispose();
        mapRenderer.dispose();
        joystick.dispose();
    }
}
