package io.AlejandroMiCo.SurvivalGame;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    private static final float PPM = 100;

    private Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Personaje pj;
    private Viewport myViewport;
    private Stage stage;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private VirtualJoystick joystick;
    private World world;
    private Box2DDebugRenderer dubbug;

    public GameScreen(Main game) {
        super();

        this.game = game;
        batch = new SpriteBatch();
        joystick = new VirtualJoystick(100, 100, 50, 20);
        camera = new OrthographicCamera();
        myViewport = new FitViewport(800, 480, camera);
        stage = new Stage(myViewport);
        Gdx.input.setInputProcessor(stage);

        map = new TmxMapLoader().load("maps/mapa1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        dubbug = new Box2DDebugRenderer();

        world = new World(new Vector2(0, 0), true); // Mundo Box2D sin gravedad
        pj = new Personaje(world, joystick);

        createCollisionObjects();
    }

    private void createCollisionObjects() {
        MapObjects objects = map.getLayers().get("CollisionLayer").getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                createStaticBody(rect);
            }
        }
    }

    private void createStaticBody(Rectangle rect) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((rect.x + rect.width / 2) / PPM, (rect.y + rect.height / 2) / PPM);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.width / 2 / PPM, rect.height / 2 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        joystick.update();
        pj.update(delta);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        camera.update();
        mapRenderer.setView(camera);

        mapRenderer.render(new int[] { 0 }); // Renderiza las primeras capas del mapa

        batch.begin();
        pj.render(batch);
        batch.end();

        mapRenderer.render(new int[] { 1 }); // Renderiza las capas superiores del mapa

        batch.begin();
        joystick.render(batch);
        batch.end();

        
        dubbug.render(world, camera.combined);
        world.step(1 / 60f, 6, 2);

    }

    @Override
    public void resize(int width, int height) {
        myViewport.update(width, height);
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
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
        pj.dispose();
        joystick.dispose();
        world.dispose();
    }
}
