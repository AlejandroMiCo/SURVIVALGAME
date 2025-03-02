package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Combat.Bullet;
import io.AlejandroMiCo.IsalandsSurvivors.Scenes.Hud;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Coin;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.CollectedItem;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Enemy;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.EnemyPool;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Experience;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight.State;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Meat;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.Assets;
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

    private ArrayList<Vector2> pendingCoins;
    private HashMap<Vector2, Integer> pendingExperience;
    private ArrayList<Vector2> pendingMeat;

    private float bulletTimer = 0;
    private float bulletDelay; // disparo cada 2 segundos

    public Array<Bullet> bulletList = new Array<Bullet>();
    private Array<CollectedItem> itemList = new Array<>();
    private Array<Enemy> enemyList = new Array<>();

    private int waveNumber = 1;
    private int enemiesPerWave = 10;
    private final float WAVE_INTERVAL = 60; // Cada 30 segundos hay una nueva oleada
    private final int MAX_ENEMIES = 100; // Máximo total de enemigos activos en pantalla

    private LevelUpScreen levelUpScreen;
    private int lastNivel = 1;

    private boolean isGameOver = false;

    private Music music;
    private Sound sonidoAtaque;

    private final Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet(world, 0, 0, 0);
        }
    };
    private static Array<Enemy> toRemove = new Array<>(); // Lista temporal para eliminaciones
    private ArrayList<CollectedItem> itemsToRemove = new ArrayList<>();

    private EnemyPool enemyPool;

    private float spawnX, spawnY;

    private float minX, minY, maxX, maxY;

    public PlayScreen(IslandsSurvivors game) {
        this.game = game;
        gameCamera = new OrthographicCamera();
        gamePort = new FitViewport(IslandsSurvivors.V_WIDTH / IslandsSurvivors.PPM,
                IslandsSurvivors.V_HEIGHT / IslandsSurvivors.PPM, gameCamera);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/ProvisionalMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / IslandsSurvivors.PPM);

        gameCamera.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(this);
        joystick = new VirtualJoystick(50);

        knight = new Knight(this, joystick);
        hud = new Hud(game.batch, knight);
        Gdx.input.setInputProcessor(hud.stage);

        world.setContactListener(new WorldContactListener(knight));
        levelUpScreen = new LevelUpScreen(knight, hud);
        pendingCoins = new ArrayList<>();
        pendingExperience = new HashMap<Vector2, Integer>();
        pendingMeat = new ArrayList<>();

        music = Gdx.audio.newMusic(Gdx.files.internal("music/song.ogg"));
        music.setLooping(true);
        music.play();
        sonidoAtaque = Gdx.audio.newSound(Gdx.files.internal("sounds/attack.ogg"));

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hud.stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (!joystick.isTouched()) {
                    joystick.update();
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);

        bulletList.add(bulletPool.obtain());
        enemyPool = new EnemyPool(this, knight);

    }

    @Override
    public void show() {

    }

    public void addCoin(Vector2 position) {
        pendingCoins.add(position);
    }

    public void addExperience(Vector2 position, int value) { /// Sumar en caso de posicion ocupada
        pendingExperience.put(position, value);
    }

    public void addMeat(Vector2 position) {
        pendingMeat.add(position);
    }

    public void update(float dt) {
        knight.update(dt);

        if (knight.getState() == State.DEAD) {
            isGameOver = true;

            if (knight.getFrame(0).isFlipX()) {
                knight.setFlip(false, false);
            }

            if (knight.deathAnimation.isAnimationFinished(knight.stateTimer)) {
                enemyPool.clear();
                enemyList.clear();

                music.dispose();
                game.setScreen(new GameOverScreen(game));
                return;
            }
        }

        if (isGameOver)
            return;

        if (levelUpScreen.isVisible()) {
            levelUpScreen.update(dt);
            return;
        }

        if (knight.getLevel() > lastNivel) {
            levelUpScreen.show();
            lastNivel = knight.getLevel();
            Gdx.input.vibrate(250);
        }

        if (hud.isPaused()) {
            hud.update(dt);
            return;
        }

        updateEntityes(dt);

        if (hud.getWorldTimer() >= waveNumber * WAVE_INTERVAL) {
            updateWave();
        }

        hud.update(dt);
        updateWorld();
        joystick.update();

    }

    public void updateWorld() {
        world.step(1 / 60f, 6, 2);
        gameCamera.position.x = knight.getB2body().getPosition().x;
        gameCamera.position.y = knight.getB2body().getPosition().y;
        gameCamera.update();
        renderer.setView(gameCamera);
    }

    public void updateEntityes(float dt) {

        updateEnemies(dt);

        updateBullets(dt);

        updateItems(dt);

    }

    public void updateBullets(float dt) {
        if (!bulletList.isEmpty()) {
            bulletDelay = bulletList.get(0).getCooldown();
        }

        bulletTimer += dt;
        if (bulletTimer >= bulletDelay) {
            fireBullet();
            bulletTimer = 0;
        }

        for (Iterator<Bullet> it = bulletList.iterator(); it.hasNext();) {
            Bullet bullet = it.next();
            if (bullet.getBody() != null) {
                bullet.update(dt);
                if (bullet.isDead()) {
                    world.destroyBody(bullet.getBody()); // Limpieza de memoria
                    bulletPool.free(bullet);
                    it.remove();
                }
            }
        }
    }

    public void updateItems(float dt) {
        // 1. Revisar qué ítems han sido recogidos y marcarlos para eliminación
        for (CollectedItem item : itemList) {
            if (item.isCollected()) {
                itemsToRemove.add(item);
            }
        }

        // 2. Eliminar los ítems marcados de la lista principal y del mundo
        for (CollectedItem item : itemsToRemove) {
            world.destroyBody(item.getBody()); // Eliminar el cuerpo de Box2D
            itemList.removeValue(item, true); // Remover de la lista
        }
        itemsToRemove.clear(); // Ahora sí, limpiar la lista de objetos eliminados

        // 3. Agregar nuevos ítems pendientes
        for (Vector2 pos : pendingCoins) {
            itemList.add(new Coin(world, pos.x, pos.y, knight));
        }
        pendingCoins.clear();

        for (Vector2 pos : pendingMeat) {
            itemList.add(new Meat(world, pos.x, pos.y, knight));
        }
        pendingMeat.clear();

        for (var pos : pendingExperience.keySet()) {
            itemList.add(new Experience(world, pos.x, pos.y, knight, pendingExperience.get(pos)));
        }
        pendingExperience.clear();

        // 4. Actualizar solo los ítems que aún existen
        for (CollectedItem item : itemList) {
            if (item.getBody() != null) { // Verificar que el objeto aún tiene un body válido
                item.update(dt);
            }
        }
    }

    private void updateEnemies(float dt) {
        if (enemyList.size < MAX_ENEMIES) {
            spawnEnemies(hud.getWorldTimer());
        }

        for (Enemy enemy : enemyList) {
            if (enemy.isDead()) {
                toRemove.add(enemy);
            }
        }

        // 2. Eliminar enemigos marcados
        for (Enemy enemy : toRemove) {
            knight.addEnemyDefeated();

            addExperience(enemy.getBody().getPosition(), enemy.getValue());

            if (Math.random() > 0.9) {
                addCoin(enemy.getBody().getPosition());
            }

            if (Math.random() >= 0.99) {
                addMeat(enemy.getBody().getPosition());
            }

            enemyPool.free(enemy); // Liberar en el pool de objetos
            world.destroyBody(enemy.getBody()); // Eliminar del mundo físico
            enemyList.removeValue(enemy, true); // Remover de la lista principal
        }
        toRemove.clear(); // Limpiar lista de eliminados

        // 3. Ahora solo actualizamos enemigos vivos
        for (Enemy enemy : enemyList) {
            if (enemy.getBody() != null) {
                enemy.update(dt);
            }
        }
    }

    private void updateWave() {
        waveNumber++;

        // Aumenta la dificultad de los enemigos
        Enemy.INITIAL_HEALTH += 5;
        Enemy.INITIAL_DAMAGE += 1.5f;
        Enemy.INITIAL_SPEED += 5f;
        // Cada oleada aumenta la cantidad de enemigos
        enemiesPerWave += 3; // 🔥 Aumenta en 2 enemigos por oleada
    }

    private void spawnEnemies(float gameTime) {
        minX = 5;
        maxX = 22;
        minY = 5;
        maxY = 23;

        while (enemyList.size < enemiesPerWave && enemyList.size < MAX_ENEMIES) {
            spawnX = (float) (Math.random() * maxX + minX);
            spawnY = (float) (Math.random() * maxY + minY);

            switch ((int) gameTime / 120) {
                case 0 -> enemyPool.setEnemyType(0);
                case 1 -> enemyPool.setEnemyType(1);
                case 2 -> enemyPool.setEnemyType(2);
                default -> enemyPool.setEnemyType(3);
            }

            Enemy enemy = enemyPool.obtain(spawnX, spawnY);
            enemyList.add(enemy);
        }
    }

    private void fireBullet() {
        if (enemyList.isEmpty())
            return; // No disparar si no hay enemigos

        float knightX = knight.getB2body().getPosition().x;
        float knightY = knight.getB2body().getPosition().y;

        // 🔹 Encontrar el enemigo más cercano
        Enemy closestEnemy = null;
        float minDistance = Float.MAX_VALUE;

        for (Enemy gobling : enemyList) {
            if (gobling.getBody() == null) // Evitar acceso a un body nulo
                continue;

            float enemyX = gobling.getBody().getPosition().x;
            float enemyY = gobling.getBody().getPosition().y;

            float distance = Vector2.dst(knightX, knightY, enemyX, enemyY);
            if (distance < minDistance) {
                minDistance = distance;
                closestEnemy = gobling;
            }
        }

        // 🔹 Si encontramos un enemigo más cercano, disparamos hacia él
        if (closestEnemy != null && closestEnemy.getBody() != null) {
            float enemyX = closestEnemy.getBody().getPosition().x;
            float enemyY = closestEnemy.getBody().getPosition().y;

            float dx = enemyX - knightX;
            float dy = enemyY - knightY;
            float shootAngle = (float) Math.atan2(dy, dx); // Calcular ángulo en radianes

            // 🔹 Crear la bala y añadirla a la lista
            Bullet bullet = bulletPool.obtain();
            sonidoAtaque.play();
            bullet.init(knightX, knightY, shootAngle); // Inicializar la bala con la posición y ángulo
            bulletList.add(bullet); // Añadirla a la lista de balas activas
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
        if (!hud.isPaused()) {
            update(delta);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        game.batch.setProjectionMatrix(gameCamera.combined);

        game.batch.begin();

        // 🔹 Dibujar todos los enemigos
        for (Enemy enemy : enemyList) {
            enemy.draw(game.batch);
        }

        for (Bullet bullet : bulletList) {
            if (bullet.getBody() != null) {
                bullet.draw(game.batch);
            }
        }
        knight.draw(game.batch);

        for (CollectedItem coin : itemList) {
            coin.render(game.batch);
        }

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.render();
        levelUpScreen.render();
        b2dr.render(world, gameCamera.combined);
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
        music.dispose();
    }

}
