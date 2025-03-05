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
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Meat;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Player;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Player.State;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.Assets;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.B2WorldCreator;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.PreferencesManager;
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
    private Player player;

    private ArrayList<Vector2> pendingCoins;
    private HashMap<Vector2, Integer> pendingExperience;
    private ArrayList<Vector2> pendingMeat;

    private float bulletTimer = 0;
    private float bulletDelay;

    public Array<Bullet> bulletList = new Array<Bullet>();
    private Array<CollectedItem> itemList = new Array<>();
    private Array<Enemy> enemyList = new Array<>();

    private int waveNumber = 1;
    private int enemiesPerWave = 10; // Cantidad de enemigos por oleada al principio del juego
    private final float WAVE_INTERVAL = 60; // Cada 30 segundos hay una nueva oleada

    private LevelUpScreen levelUpScreen;
    private int lastNivel = 1;

    private boolean isGameOver = false;

    private Music music;
    private Sound sonidoAtaque;
    float volume;

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

    /**
     * Constructor de la pantalla de juego. *(Donde sucede la magia)
     * 
     * @param game Referencia al juego principal.
     */
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

        new B2WorldCreator(this);
        joystick = new VirtualJoystick(50);

        player = new Player(this, joystick);
        hud = new Hud(game.batch, player);
        Gdx.input.setInputProcessor(hud.stage);

        world.setContactListener(new WorldContactListener(player));
        levelUpScreen = new LevelUpScreen(player, hud);
        pendingCoins = new ArrayList<>();
        pendingExperience = new HashMap<Vector2, Integer>();
        pendingMeat = new ArrayList<>();

        // Ajustamos el volumen de la música y el sonido de ataque desde
        // PreferencesManager
        music = Assets.manager.get("music/song.ogg");
        music.setLooping(true);
        music.setVolume(PreferencesManager.getMusicVolume()); // Ajustamos el volumen de la música
        music.play();

        sonidoAtaque = Assets.manager.get("sounds/attack.ogg");
        volume = PreferencesManager.getSoundVolume(); // Ajustamos el volumen de los sonidos

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
        enemyPool = new EnemyPool(this, player);

    }

    @Override
    public void show() {
    }

    /**
     * Añade una moneda a la lista de monedas pendientes.
     * 
     * @param position Posición de la moneda.
     */
    public void addCoin(Vector2 position) {
        pendingCoins.add(position);
    }

    /**
     * Añade experiencia a la lista de experiencia pendiente.
     * 
     * @param position Posición de la experiencia.
     * @param value    Valor de la experiencia.
     */
    public void addExperience(Vector2 position, int value) { /// Sumar en caso de posicion ocupada
        pendingExperience.put(position, value);
    }

    /**
     * Añade carne a la lista de carne pendiente.
     * 
     * @param position Posición de la carne.
     */
    public void addMeat(Vector2 position) {
        pendingMeat.add(position);
    }

    public void update(float dt) {
        player.update(dt);
        hud.update(dt);

        // Si el jugador muere, termina el juego
        if (player.getState() == State.DEAD) {
            isGameOver = true;

            if (player.getFrame(0).isFlipX()) {
                player.setFlip(false, false);
            }

            // Espera a que la animacion de muerte haya terminado antes de limpia el mundo
            if (player.deathAnimation.isAnimationFinished(player.stateTimer)) {
                enemyPool.clear();
                enemyList.clear();

                music.dispose();
                game.setScreen(new GameOverScreen(game));
                return;
            }
        }

        // Si el jugador no muere y han pasado 8 minutos, termina el juego
        if (hud.getWorldTimer() >= 480 && player.getState() != State.DEAD) {
            enemyPool.clear();
            enemyList.clear();
            music.dispose();
            game.setScreen(new VictoryScreen(game, player.getLevel(), player.getEnemiesDefeated(), player.getCoins()));
            return;
        }

        // Si el jugador no muere y el juego no ha terminado, actualiza el juego
        if (isGameOver)
            return;

        // Si la pantalla de subida de nivel esta visible, actualiza la pantalla
        if (levelUpScreen.isVisible()) {
            levelUpScreen.update(dt);
            return;
        }

        // Si el nivel del jugador ha superado el nivel anterior, muestra la pantalla de
        // subida de nivel
        if (player.getLevel() > lastNivel) {
            levelUpScreen.show();
            lastNivel = player.getLevel();

            if (PreferencesManager.isVibrationEnabled()) {
                Gdx.input.vibrate(250); // Vibrar por 250 milisegundos
            }
        }

        // Actualiza las entidades del juego
        updateEntityes(dt);

        // Si el jugador ha pasado el tiempo de la ola actual, actualiza la oleada
        if (hud.getWorldTimer() >= waveNumber * WAVE_INTERVAL) {
            updateWave();
        }

        // Actualiza el mundo
        updateWorld();
        joystick.update();
    }

    /**
     * Actualiza el mundo del juego.
     */
    public void updateWorld() {
        world.step(1 / 60f, 6, 2);
        gameCamera.position.x = player.getB2body().getPosition().x;
        gameCamera.position.y = player.getB2body().getPosition().y;
        gameCamera.update();
        renderer.setView(gameCamera);
    }

    /**
     * Actualiza las entidades del juego.
     *
     * @param dt Delta time (tiempo transcurrido desde el último frame).
     */
    public void updateEntityes(float dt) {
        updateEnemies(dt);
        updateBullets(dt);
        updateItems(dt);
    }

    /**
     * Actualiza las balas del juego.
     *
     * @param dt Delta time (tiempo transcurrido desde el último frame).
     */
    public void updateBullets(float dt) {
        // Si hay balas pendientes, actualiza el tiempo de espera obteniendo el tiempo
        // de
        // espera de la bala que puede ser mejorado por nivel
        if (!bulletList.isEmpty()) {
            bulletDelay = bulletList.get(0).getCooldown();
        }

        bulletTimer += dt;

        // Si ha pasao el tiempo necesario, dispara una nueva
        if (bulletTimer >= bulletDelay) {
            fireBullet();
            bulletTimer = 0;
        }

        // Actualiza todas las balas
        for (Iterator<Bullet> it = bulletList.iterator(); it.hasNext();) {
            Bullet bullet = it.next();
            if (bullet.getBody() != null) {
                bullet.update(dt);

                // Si la bala ha muerto, destruye el cuerpo físico y libera la bala
                if (bullet.isDead()) {
                    world.destroyBody(bullet.getBody());
                    bulletPool.free(bullet);
                    it.remove();
                }
            }
        }
    }

    /**
     * Actualiza los ítems del juego.
     * 
     * @param dt Delta time (tiempo transcurrido desde el último frame).
     */
    public void updateItems(float dt) {
        // Revisa qué ítems han sido recogidos y los marca para su eliminación
        for (CollectedItem item : itemList) {
            if (item.isCollected()) {
                itemsToRemove.add(item);
            }
        }

        // Eliminar los ítems marcados de la lista principal y del mundo
        for (CollectedItem item : itemsToRemove) {
            world.destroyBody(item.getBody());
            itemList.removeValue(item, true);
        }
        itemsToRemove.clear();

        // Agregar monedas pendientes
        for (Vector2 pos : pendingCoins) {
            itemList.add(new Coin(world, pos.x, pos.y, player));
        }
        pendingCoins.clear();

        // Agregar carne pendiente
        for (Vector2 pos : pendingMeat) {
            itemList.add(new Meat(world, pos.x, pos.y, player));
        }
        pendingMeat.clear();

        // Agregar experiencia pendiente
        for (var pos : pendingExperience.keySet()) {
            itemList.add(new Experience(world, pos.x, pos.y, player, pendingExperience.get(pos)));
        }
        pendingExperience.clear();

        // Actualizar solo los ítems que aún existen
        for (CollectedItem item : itemList) {
            if (item.getBody() != null) {
                item.update(dt);
            }
        }
    }

    /**
     * Actualiza los enemigos del juego.
     * 
     * @param dt Delta time (tiempo transcurrido desde el último frame).
     */
    private void updateEnemies(float dt) {
        // Si hay menos enemigos de los permitidos, spawnea nuevos
        if (enemyList.size < enemiesPerWave) {
            spawnEnemies(hud.getWorldTimer());
        }

        // Marcar enemigos muertos para ser eliminados
        for (Enemy enemy : enemyList) {
            if (enemy.isDead()) {
                toRemove.add(enemy);
            }
        }

        // Eliminar enemigos marcados
        for (Enemy enemy : toRemove) {
            player.addEnemyDefeated();

            // Añadir experiencia, monedas y carne dependiendo del nivel del enemigo
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

        // Actualiza solo los enemigos vivos
        for (Enemy enemy : enemyList) {
            if (enemy.getBody() != null) {
                enemy.update(dt);
            }
        }
    }

    /**
     * Actualiza la ola del juego.
     */
    private void updateWave() {
        waveNumber++;

        // Aumenta la dificultad de los enemigos
        Enemy.INITIAL_HEALTH += 10f;
        Enemy.INITIAL_DAMAGE += 1.5f;
        Enemy.INITIAL_SPEED += 5f;
        // Cada oleada aumenta la cantidad de enemigos
        enemiesPerWave += 5;
    }

    /**
     * Spawna enemigos en el mapa.
     *
     * @param gameTime Tiempo transcurrido desde el comienzo del juego.
     */
    private void spawnEnemies(float gameTime) {
        // Definimos el rango de posiciones donde se generaran enemigos (Dentro del
        // cuadrado del mapa)
        minX = 5;
        maxX = 22;
        minY = 5;
        maxY = 23;

        // Genera enemigos mientras no haya suficientes enemigos y no haya llegado al
        // límite de enemigos
        while (enemyList.size < enemiesPerWave) {
            spawnX = (float) (Math.random() * maxX + minX);
            spawnY = (float) (Math.random() * maxY + minY);

            // Cambiar el tipo de enemigo cada 2 minutos
            switch ((int) gameTime / 120) {
                case 0 -> enemyPool.setEnemyType(0);
                case 1 -> enemyPool.setEnemyType(1);
                case 2 -> enemyPool.setEnemyType(2);
                default -> enemyPool.setEnemyType(3);
            }

            // Intenta recuperar un enemigo de la pool y sino lo genera y luego lo agrega a
            // la lista
            Enemy enemy = enemyPool.obtain(spawnX, spawnY);
            enemyList.add(enemy);
        }
    }

    /**
     * 
     * Dispara una bala en el enemigo más cercano.
     */
    private void fireBullet() {
        if (enemyList.isEmpty())
            return; // No disparar si no hay enemigos

        float playerX = player.getB2body().getPosition().x;
        float playerY = player.getB2body().getPosition().y;

        // Encontrar el enemigo más cercano
        Enemy closestEnemy = null;
        float minDistance = Float.MAX_VALUE;

        // Recorrer todos los enemigos para encontrar el más cercano
        for (Enemy gobling : enemyList) {
            if (gobling.getBody() == null) // Evitar acceso a un body nulo
                continue;

            float enemyX = gobling.getBody().getPosition().x;
            float enemyY = gobling.getBody().getPosition().y;

            float distance = Vector2.dst(playerX, playerY, enemyX, enemyY);
            if (distance < minDistance) {
                minDistance = distance;
                closestEnemy = gobling;
            }
        }

        // Si encontramos un enemigo más cercano, disparamos hacia él
        if (closestEnemy != null && closestEnemy.getBody() != null) {
            float enemyX = closestEnemy.getBody().getPosition().x;
            float enemyY = closestEnemy.getBody().getPosition().y;

            float dx = enemyX - playerX;
            float dy = enemyY - playerY;
            float shootAngle = (float) Math.atan2(dy, dx); // Calcular ángulo en radianes

            // Intenta obtener una bala de la pool y sino la genera y la añade a la lista
            Bullet bullet = bulletPool.obtain();
            sonidoAtaque.play(volume);
            bullet.init(playerX, playerY, shootAngle); // Inicializar la bala con la posición y ángulo
            bulletList.add(bullet); // Añadirla a la lista de balas activas
        }
    }

    /**
     * Método auxiliar para obtener el mapa del juego.
     * 
     * @return Mapa del juego.
     */
    public TiledMap getMap() {
        return map;
    }

    /**
     * Método auxiliar para obtener el mundo del juego.
     * 
     * @return Mundo del juego.
     */
    public World getWorld() {
        return world;
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        game.batch.setProjectionMatrix(gameCamera.combined);

        game.batch.begin();

        // Dibujar todos los enemigos
        for (Enemy enemy : enemyList) {
            enemy.draw(game.batch);
        }

        // Dibujar todas las balas
        for (Bullet bullet : bulletList) {
            if (bullet.getBody() != null) {
                bullet.draw(game.batch);
            }
        }
        player.draw(game.batch);

        // Dibujar todos los ítems
        for (CollectedItem coin : itemList) {
            coin.render(game.batch);
        }

        game.batch.end();

        // Dibujar el escenario con la UI
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.render();
        levelUpScreen.render();
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

    /**
     * Libera los recursos de la pantalla.
     */
    @Override
    public void dispose() {
        for (Bullet bullet : bulletList) {
            if (bullet.getBody() != null) {
                world.destroyBody(bullet.getBody());
            }
        }
        bulletList.clear();

        world.dispose();
        map.dispose();
        renderer.dispose();
        hud.dispose();
        music.dispose();
    }

}
