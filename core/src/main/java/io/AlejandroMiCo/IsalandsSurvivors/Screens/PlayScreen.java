package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
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
    private ArrayList<Vector2> pendingCoins;
    private HashMap<Vector2, Integer> pendingExperience;
    private ArrayList<Vector2> pendingMeat;
    private ArrayList<CollectedItem> itemList = new ArrayList<>();

    private float bulletTimer = 0;
    private float bulletDelay; // disparo cada 2 segundos

    private ArrayList<Enemy> enemyList = new ArrayList<>();

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

    private EnemyPool enemyPool;

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

        // bulletList.add(new Bullet(world, 0, 0, 0));
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
            world.setGravity(new Vector2(0, 0));

            if (knight.getFrame(0).isFlipX()) {
                knight.setFlip(false, false);
            }

            if (knight.deathAnimation.isAnimationFinished(knight.stateTimer)) {
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
        gameCamera.position.x = knight.b2body.getPosition().x;
        gameCamera.position.y = knight.b2body.getPosition().y;
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

        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : bulletList) {
            if (bullet.getBody() != null) {
                bullet.update(dt);
                if (bullet.isDead()) {
                    bulletsToRemove.add(bullet);
                }
            }
        }

        for (Bullet bullet : bulletsToRemove) {
            if (bullet.getBody() != null) {
                world.destroyBody(bullet.getBody()); // Elimina el cuerpo de Box2D
            }
            bulletList.remove(bullet);
            bulletPool.free(bullet); // Liberar la bala de vuelta a la Pool
        }
        bulletsToRemove.clear();
    }

    public void updateItems(float dt) {
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

        for (CollectedItem item : itemList) {
            item.update(dt);
        }

        ArrayList<CollectedItem> itemsToRemove = new ArrayList<>();
        for (CollectedItem item : itemList) {
            if (item.isCollected()) {
                itemsToRemove.add(item);
            }
        }
        itemList.removeAll(itemsToRemove);
    }

    private void updateEnemies(float dt) {
        ArrayList<Enemy> enemiesToRemove = new ArrayList<>();

        // Si la cantidad de enemigos activos es menor que el máximo permitido, se
        // generan nuevos enemigos
        if (enemyList.size() < MAX_ENEMIES) {
            spawnEnemies(hud.getWorldTimer());
        }

        // Actualizamos los enemigos existentes
        for (Enemy gobling : enemyList) {
            gobling.update(dt);

            // Si el enemigo ha terminado su animación de muerte, lo agregamos a la lista
            // para ser eliminado
            if (gobling.deathAnimationFinished) {
                enemiesToRemove.add(gobling);
            }
        }

        // Eliminamos los enemigos que han muerto
        for (Enemy enemy : enemiesToRemove) {
            if (enemy.b2body != null) {
                world.destroyBody(enemy.b2body); // Elimina el cuerpo de Box2D
                enemy.b2body = null; // Evita referencias a cuerpos ya eliminados
            }
            // En lugar de remover el enemigo de la lista, lo devolvemos al pool
            enemyPool.free(enemy); // Libera el enemigo de vuelta al pool
        }

        // Elimina los enemigos muertos de la lista
        enemyList.removeAll(enemiesToRemove);
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
        float spawnX, spawnY;
        float minX = 3, maxX = 23;
        float minY = 3, maxY = 23;

        while (enemyList.size() < enemiesPerWave && enemyList.size() < MAX_ENEMIES) {
            spawnX = MathUtils.clamp((float) (Math.random() * (maxX - minX) + minX), minX, maxX);
            spawnY = MathUtils.clamp((float) (Math.random() * (maxY - minY) + minY), minY, maxY);

            Enemy enemy = null;
            switch ((int) gameTime / 120) {
                case 0 -> enemy = enemyPool.obtain(spawnX, spawnY, 0); // 2min
                case 1 -> enemy = enemyPool.obtain(spawnX, spawnY, 1); // 4min
                case 2 -> enemy = enemyPool.obtain(spawnX, spawnY, 2); // 6min
                case 3 -> enemy = enemyPool.obtain(spawnX, spawnY, 3); // 8min
                //case 4 -> enemy = enemyPool.obtain(spawnX, spawnY, 0); // 10min
                default -> {
                    enemy = enemyPool.obtain(spawnX, spawnY, 0);
                    enemyList.add(enemyPool.obtain(spawnX, spawnY, 1));
                    enemyList.add(enemyPool.obtain(spawnX, spawnY, 2));
                    enemyList.add(enemyPool.obtain(spawnX, spawnY, 3));
                }
            }
            enemyList.add(enemy);
        }
    }

    private void fireBullet() {
        if (enemyList.isEmpty())
            return; // No disparar si no hay enemigos

        float knightX = knight.b2body.getPosition().x;
        float knightY = knight.b2body.getPosition().y;

        // 🔹 Encontrar el enemigo más cercano
        Enemy closestEnemy = null;
        float minDistance = Float.MAX_VALUE;

        for (Enemy gobling : enemyList) {
            if (gobling.b2body == null) // Evitar acceso a un body nulo
                continue;

            float enemyX = gobling.b2body.getPosition().x;
            float enemyY = gobling.b2body.getPosition().y;

            float distance = Vector2.dst(knightX, knightY, enemyX, enemyY);
            if (distance < minDistance) {
                minDistance = distance;
                closestEnemy = gobling;
            }
        }

        // 🔹 Si encontramos un enemigo más cercano, disparamos hacia él
        if (closestEnemy != null && closestEnemy.b2body != null) {
            float enemyX = closestEnemy.b2body.getPosition().x;
            float enemyY = closestEnemy.b2body.getPosition().y;

            float dx = enemyX - knightX;
            float dy = enemyY - knightY;
            float shootAngle = (float) Math.atan2(dy, dx); // Calcular ángulo en radianes

            // 🔹 Crear la bala y añadirla a la lista
            Bullet bullet = bulletPool.obtain();
            bullet.init(knightX, knightY, shootAngle); // Inicializar la bala con la posición y ángulo
            bulletList.add(bullet); // Añadirla a la lista de balas activas

            sonidoAtaque.play();
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
        // b2dr.render(world, gameCamera.combined);
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
        music.dispose();
    }

}
