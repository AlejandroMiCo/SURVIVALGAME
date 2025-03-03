package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.Assets;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.PreferencesManager;

public abstract class Enemy extends Sprite implements Poolable {
    protected World world;
    protected PlayScreen screen;
    private Body b2body;
    public Knight knight;

    public static int INITIAL_HEALTH = 20;
    public static int INITIAL_DAMAGE = 5;
    public static float INITIAL_SPEED = 45;

    protected int health;
    protected int damage;
    protected float speed;

    private boolean active; // Flag para saber si el enemigo está en uso

    public boolean deathAnimationFinished;
    public Random rdn;

    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    private float stateTime;
    public boolean shouldFaceRight;

    public float damageTimer;
    private Sound getHit;
    private Vector2 direction;
    private int value;
    private static final Vector2 tempVector = new Vector2(); // Vector reutilizables
    float volume;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Enemy(PlayScreen screen, float x, float y, Knight knight, String walkFile, int value) {
        this.health = INITIAL_HEALTH;
        this.damage = INITIAL_DAMAGE;
        this.speed = INITIAL_SPEED;
        this.world = screen.getWorld();
        this.screen = screen;
        this.knight = knight;
        this.value = value;
        this.active = false; // Se inicia inactivo

        direction = new Vector2();

        walkAnimation = getAnimation(new Texture(walkFile));
        deathAnimation = getAnimation(Assets.manager.get("img/Dead_custom.png"));
        stateTime = 0;
        deathAnimationFinished = false;

        defineEnemy();
        setBounds(getX(), getY(), 96 / IslandsSurvivors.PPM, 96 / IslandsSurvivors.PPM);
        setPosition(x, y);
        getHit = Assets.manager.get("sounds/pupa.ogg");
        volume = PreferencesManager.getSoundVolume();
    }

    public void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(0, 0); // (float) ((Math.random() * 22) + 5), (float) ((Math.random() * 23) + 5)
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        FixtureDef fedef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(12 / IslandsSurvivors.PPM, 12 / IslandsSurvivors.PPM);
        fedef.shape = shape;

        fedef.friction = 1;
        fedef.density = 100;

        fedef.filter.categoryBits = IslandsSurvivors.ENEMY_BIT; // El Gobling pertenece a la categoría "enemigo"
        fedef.filter.maskBits = IslandsSurvivors.BULLET_BIT | IslandsSurvivors.PLAYER_BIT | IslandsSurvivors.DEFAULT_BIT
                | IslandsSurvivors.ENEMY_BIT;
        b2body.createFixture(fedef).setUserData(this);
    }

    public void takeDamage(int dmg) {
        if (!active)
            return;

        getHit.play(volume);
        health -= dmg;

        // Si la vida llega a 0, destruir el enemigo
        if (health <= 0) {
            active = false;
        }
        flashDamage();
    };

    public Animation<TextureRegion> getAnimation(Texture imagen) {
        TextureRegion[][] tmp;
        TextureRegion[] regionsMovimiento;

        tmp = TextureRegion.split(imagen, imagen.getWidth() / 6, imagen.getHeight());
        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[0][i];
        }
        return new Animation<>(0.125f, regionsMovimiento);
    }

    public void draw(Batch batch) {
        if (!deathAnimationFinished) {
            super.draw(batch);
        }
    }

    public void update(float dt) {
        if (!active)
            return;
        stateTime += dt;

        // Reutilizar un Vector2 temporal para evitar crear objetos innecesarios
        tempVector.set(knight.getB2body().getPosition()).sub(b2body.getPosition()).nor();
        direction.set(tempVector); // Actualizar 'direction' sin generar nuevas instancias
        b2body.setLinearVelocity(direction.scl(speed * dt));

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(walkAnimation.getKeyFrame(stateTime, true));

        if (b2body.getLinearVelocity().x < 0) {
            flip(true, false);
        }

        if (damageTimer > 0) {
            damageTimer -= dt;
            if (damageTimer <= 0) {
                setColor(1, 1, 1, 1); // Volver al color normal
            }
        }
    }

    public int getDamage() {
        return damage;
    }

    public int getHealth() {
        return health;
    }

    private void flashDamage() {
        setColor(1, 0, 0, 0.8f);
        damageTimer = 0.1f;
    }

    public static void resetEnemiesStats() {
        INITIAL_HEALTH = 20;
        INITIAL_DAMAGE = 5;
        INITIAL_SPEED = 45;
    }

    public void setBody(Body b2body) {
        this.b2body = b2body;
    }

    public Body getBody() {
        return b2body;
    }

    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public void reset() {
        // Restablece todos los campos relevantes del enemigo para que pueda ser
        // reutilizado desde la pool
        active = false;
        health = INITIAL_HEALTH;
        damage = INITIAL_DAMAGE;
        speed = INITIAL_SPEED;
        stateTime = 0;
        setRegion(deathAnimation.getKeyFrame(stateTime, false));
        deathAnimationFinished = false;
        direction.set(0, 0); // Limpiar el vector
        setPosition(0, 0); // Puedes restablecer la posición a cualquier valor que sea necesario
        b2body.setLinearVelocity(0, 0);// Reiniciar otras propiedades del enemigo según sea necesario.
    }

    public void reinitialize(float x, float y) {
        setPosition(x, y);
        active = true;
        deathAnimationFinished = false;
        stateTime = 0;
        damageTimer = 0;
        setColor(1, 1, 1, 1);
        b2body.setTransform(x, y, 0);
        b2body.setLinearVelocity(0, 0);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
