package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Combat.Bullet;
import io.AlejandroMiCo.IsalandsSurvivors.Scenes.Hud;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Enemy;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.TorchGobling;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.B2WorldCreator;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.VirtualJoystick;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.WorldContactListener;

public class PlayScreen implements Screen {
    private IslandsSurvivors game;
    private OrthographicCamera gameCamera;
    private Viewport gamePort;
    private Hud hud;

    private VirtualJoystick joystick;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Knight knight;

    public ArrayList<Bullet> bulletList = new ArrayList<Bullet>();

    private float bulletTimer = 0;
    private final float bulletDelay = 2f; // disparo cada 2 segundos

    private ArrayList<TorchGobling> goblingList = new ArrayList<>();

    private float gameTimer = 0;

    private int waveNumber = 1;
    private int enemiesPerWave = 10;
    private final float WAVE_INTERVAL = 10; // Cada 30 segundos hay una nueva oleada
    private final int MAX_ENEMIES = 150; // Máximo total de enemigos activos en pantalla

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

        new B2WorldCreator(this);
        joystick = new VirtualJoystick(50, 20);

        knight = new Knight(this, joystick);

        world.setContactListener(new WorldContactListener());
    }

    @Override
    public void show() {

    }

    public void update(float dt) {
        gameTimer += dt;

        // 🔸 Actualizar temporizador de disparo automático
        bulletTimer += dt;
        if (bulletTimer >= bulletDelay) {
            fireBullet();
            bulletTimer = 0; // Reiniciar el temporizador
        }

        // 🔹 Lista temporal para almacenar balas que deben eliminarse
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();

        // 🔹 Actualizar balas y marcar para eliminación
        for (Bullet bullet : bulletList) {
            if (bullet.getBody() != null) {
                bullet.update(dt);
                if (bullet.isDead()) {
                    bulletsToRemove.add(bullet);
                }
            }
        }

        // 🔸 Realizar el step de Box2D (esto actualiza la física del mundo)
        world.step(1 / 60f, 6, 2);

        // 🔸 Ahora que el mundo ya ha actualizado su estado, eliminamos los cuerpos
        for (Bullet bullet : bulletsToRemove) {
            world.destroyBody(bullet.getBody()); // Destruir el body en Box2D
            bulletList.remove(bullet); // Eliminar la bala de la lista
        }
        bulletsToRemove.clear(); // Limpiar la lista temporal

        // 🔹 Actualizar temporizador de generación de enemigos

        // 🔹 Lista temporal para almacenar enemigos eliminados
        ArrayList<TorchGobling> enemiesToRemove = new ArrayList<>();

        if (gameTimer >= waveNumber * WAVE_INTERVAL) {
            updateWave();
        }
        if (goblingList.size() < MAX_ENEMIES) {
            spawnEnemies();
        }

        // 🔹 Actualizar enemigos y marcar los que deben eliminarse
        for (TorchGobling gobling : goblingList) {
            gobling.update(dt);
            if (gobling.deathAnimationFinished) {
                enemiesToRemove.add(gobling);
            }
        }
        goblingList.removeAll(enemiesToRemove);

        // 🔹 Actualizar otros elementos del juego
        knight.update(dt);
        hud.update(dt);

        // 🔹 Mantener la cámara centrada en el personaje
        gameCamera.position.x = knight.b2body.getPosition().x;
        gameCamera.position.y = knight.b2body.getPosition().y;
        gameCamera.update();

        renderer.setView(gameCamera);
    }

    private void updateWave() {
        waveNumber++;

        // Aumenta la dificultad de los enemigos
        Enemy.INITIAL_HEALTH += 5;
        Enemy.INITIAL_DAMAGE += 2;
        Enemy.INITIAL_SPEED += 10f;
        // Cada oleada aumenta la cantidad de enemigos
        enemiesPerWave += 3; // 🔥 Aumenta en 2 enemigos por oleada
        System.out.println("⚔️ ¡Nueva Oleada! Enemigos en esta oleada: " + enemiesPerWave);
    }

    private void spawnEnemies() {
        float spawnX, spawnY;
        float minX = 3, maxX = 23;
        float minY = 3, maxY = 23;

        while (goblingList.size() < enemiesPerWave && goblingList.size() < MAX_ENEMIES) {
            spawnX = MathUtils.clamp((float) (Math.random() * (maxX - minX) + minX), minX, maxX);
            spawnY = MathUtils.clamp((float) (Math.random() * (maxY - minY) + minY), minY, maxY);

            goblingList.add(new TorchGobling(this, spawnX, spawnY, knight));
        }
    }

    private void fireBullet() {
        if (goblingList.isEmpty())
            return; // No disparar si no hay enemigos

        float knightX = knight.b2body.getPosition().x;
        float knightY = knight.b2body.getPosition().y;

        // 🔹 Encontrar el enemigo más cercano
        TorchGobling closestEnemy = null;
        float minDistance = Float.MAX_VALUE;

        for (TorchGobling gobling : goblingList) {
            float enemyX = gobling.b2body.getPosition().x;
            float enemyY = gobling.b2body.getPosition().y;

            float distance = Vector2.dst(knightX, knightY, enemyX, enemyY);
            if (distance < minDistance) {
                minDistance = distance;
                closestEnemy = gobling;
            }
        }

        // 🔹 Si encontramos un enemigo más cercano, disparamos hacia él
        if (closestEnemy != null) {
            float enemyX = closestEnemy.b2body.getPosition().x;
            float enemyY = closestEnemy.b2body.getPosition().y;

            float dx = enemyX - knightX;
            float dy = enemyY - knightY;
            float shootAngle = (float) Math.atan2(dy, dx); // Calcular ángulo en radianes

            // 🔹 Crear la bala y añadirla a la lista
            bulletList.add(new Bullet(world, knightX, knightY, shootAngle));
        }
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void render(float delta) {
        update(delta);
        joystick.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        game.batch.setProjectionMatrix(gameCamera.combined);

        game.batch.begin();
        knight.draw(game.batch);

        // 🔹 Dibujar todos los enemigos
        for (TorchGobling gobling : goblingList) {
            gobling.draw(game.batch);
        }

        for (Bullet bullet : bulletList) {
            if (bullet.getBody() != null) {
                bullet.draw(game.batch);
            }
        }

        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        hud.stage.draw();
        b2dr.render(world, gameCamera.combined);

        game.batch.begin();
        if (joystick.isActive()) {
            joystick.render(game.batch);
        }
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
        for (Bullet bullet : bulletList) {
            if (bullet.getBody() != null) {
                world.destroyBody(bullet.getBody());
            }
        }
        bulletList.clear();

        world.dispose();
        b2dr.dispose();
        map.dispose();
        renderer.dispose();
        hud.dispose();
        joystick.dispose();
    }

}
