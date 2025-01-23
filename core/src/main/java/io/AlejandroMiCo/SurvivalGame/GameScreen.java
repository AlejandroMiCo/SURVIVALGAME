package io.AlejandroMiCo.SurvivalGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

    private float MAX_X = 24f;
    private float MAX_Y = 25f;
    private float MIN_X = 3f;
    private float MIN_Y = 3f;

    private Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Personaje pj;
    private Enemigo enemigo;
    private Viewport myViewport;
    private Stage stage;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private VirtualJoystick joystick;
    private World world;
    private Box2DDebugRenderer debugRenderer;

    public GameScreen(Main game) {
        super();

        this.game = game;
        batch = new SpriteBatch();
        joystick = new VirtualJoystick(50, 20);
        camera = new OrthographicCamera();
        myViewport = new FitViewport(800, 420, camera);
        stage = new Stage(myViewport);
        Gdx.input.setInputProcessor(stage);

        map = new TmxMapLoader().load("maps/ProvisionalMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        world = new World(new Vector2(0, 0), true); // Mundo Box2D sin gravedad
        pj = new Personaje(world, joystick);

        // float mapWidth = map.getProperties().get("width", Integer.class)
        //         * map.getProperties().get("tilewidth", Integer.class);
        // float mapHeight = map.getProperties().get("height", Integer.class)
        //         * map.getProperties().get("tileheight", Integer.class);

        enemigo = new Enemigo(world, pj, MIN_X, MIN_Y, MAX_X, MAX_Y);

        debugRenderer = new Box2DDebugRenderer();

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
        enemigo.update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(pj.getBody().getPosition().x * PPM, pj.getBody().getPosition().y * PPM, 0);
        camera.update();
        mapRenderer.setView(camera);

        batch.begin();
        mapRenderer.render(); // Renderiza las primeras capas del mapa
        pj.render(batch);

        if (enemigo.isAlive()) {
            enemigo.render(batch);
        } else {
            world.destroyBody(enemigo.getBody());
            enemigo = new Enemigo(world, pj, MIN_X, MIN_Y, MAX_X, MAX_Y);
        }
        batch.end();

        batch.begin();
        if (joystick.isActive()) {
            joystick.render(batch);
        }

        // System.out.println(pj.getBody().getPosition());
        // System.out.println(enemigo.getBody().getPosition());
        // System.out.println(20 / PPM);

        if (pj.getBody().getPosition().dst(enemigo.getBody().getPosition()) < 50 / PPM) { // Distancia de colisión
            pj.takeDamage(10);
            System.out.println(pj.getHealth());
            enemigo.takeDamage(10);
        }
        batch.end();

        world.step(1 / 60f, 6, 2);

        debugRenderer.render(world, camera.combined.scl(PPM));
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
        enemigo.dispose();
        joystick.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
