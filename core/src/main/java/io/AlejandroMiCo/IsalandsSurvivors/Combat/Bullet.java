package io.AlejandroMiCo.IsalandsSurvivors.Combat;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

/**
 * Clase que representa una bala en el juego.
 * Se gestiona con Box2D y se reutiliza con un sistema de Pooling.
 */
public class Bullet extends Sprite implements Poolable {

    private float angle; // Ángulo de disparo
    private float time; // Tiempo de vida de la bala
    private float size; // Tamaño de la bala
    private TextureRegion[][] tmp;
    private TextureRegion[] regionsMovimiento;
    private Texture animacionMovimiento; // Textura de la animación de la bala
    private boolean shouldRemove = false; // Bandera para marcar la bala como eliminada

    private static final Vector2 tmpPosition = new Vector2();
    private static final Vector2 tmpVelocity = new Vector2();

    float stateTime; // Tiempo de animación
    Body body; // Cuerpo de la bala en el mundo físico
    World world; // Mundo donde existe la bala

    // Atributos de la bala que pueden ser mejorados
    private static HashMap<String, Float> atributos = new HashMap<>() {
        {
            put("bullet_speed", 1f);
            put("bullet_cooldown", 2.0f);
        }
    };

    /**
     * Constructor de la bala.
     * 
     * @param world Mundo físico en el que se encuentra
     * @param x     Posición X inicial
     * @param y     Posición Y inicial
     * @param angle Ángulo de disparo
     */
    public Bullet(World world, float x, float y, float angle) {
        super();
        this.angle = angle;
        this.world = world;
        animacionMovimiento = new Texture("img/arrow.png");
    }

    // Getters
    public Body getBody() {
        return body;
    }

    public float getCooldown() {
        return atributos.get("bullet_cooldown");
    }

    /**
     * Actualiza la posición, animación de la bala y el tiempo de vida que le queda.
     * 
     * @param dt Delta time (tiempo transcurrido desde la última actualización)
     */
    public void update(float dt) {
        time -= dt;
        stateTime += dt;

        size = 20 / IslandsSurvivors.PPM;
        setSize(size, size);
        setOriginCenter();

        if (body != null) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setRotation((float) Math.toDegrees(angle));
        }
        setScale(size * 10);
        setRegion(animacionMovimiento);
        setRotation((float) Math.toDegrees(angle));
    }

    /**
     * Marca la bala para su eliminación.
     */
    public void markForRemoval() {
        shouldRemove = true;
    }

    /**
     * Comprueba si la bala debe ser eliminada.
     * 
     * @return true si la bala ha expirado o ha colisionado
     */
    public boolean isDead() {
        return time < 0 || body == null || shouldRemove;
    }

    /**
     * Define la física de la bala y la inicializa en el mundo.
     * 
     * @param x    Posición X inicial
     * @param y    Posición Y inicial
     * @param size Tamaño del cuerpo físico
     */
    public void defineBullet(float x, float y, float size) {
        float offset = 0.3f;

        BodyDef bdef = new BodyDef();
        tmpPosition.set(x, y).add((float) Math.cos(angle) * offset, (float) Math.sin(angle) * offset);
        bdef.position.set(tmpPosition);
        bdef.angle = angle;
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.bullet = true;

        body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(size / 2);

        FixtureDef fedef = new FixtureDef();
        fedef.shape = shape;
        fedef.density = 1f;
        fedef.friction = 0f;
        fedef.restitution = 0f;

        fedef.filter.categoryBits = IslandsSurvivors.BULLET_BIT;
        fedef.filter.maskBits = IslandsSurvivors.ENEMY_BIT | IslandsSurvivors.DEFAULT_BIT;

        body.createFixture(fedef).setUserData(this);
        shape.dispose();

        tmpVelocity.set((float) Math.cos(angle), (float) Math.sin(angle)).scl(atributos.get("bullet_speed"));
        body.setLinearVelocity(tmpVelocity);
    }

    /**
     * Devuelve la animación de la bala.
     * 
     * @param imagen Textura de la animación de la cual se extraerán los frames
     * @return Animación de la bala
     */
    public Animation<TextureRegion> getAnimation(Texture imagen) {
        tmp = TextureRegion.split(imagen, imagen.getWidth() / 8, imagen.getHeight());
        regionsMovimiento = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            regionsMovimiento[i] = tmp[0][i];
        }
        return new Animation<>(0.075f, regionsMovimiento);
    }

    /**
     * Mejora un atributo de la bala.
     * 
     * @param atributo Nombre del atributo a mejorar
     * @param cantidad Cantidad de mejora
     */
    public static void mejorarAtributo(String atributo, float cantidad) {
        if (atributos.containsKey(atributo)) {
            atributos.put(atributo, atributos.get(atributo) + cantidad);
        }
    }

    /**
     * Restablece los atributos de la bala a sus valores iniciales.
     */
    public static void resetBullet() {
        atributos.put("bullet_speed", 1.5f);
        atributos.put("bullet_cooldown", 2f);
    }

    /**
     * Inicializa la bala en una nueva posición.
     * 
     * @param x     Posición X
     * @param y     Posición Y
     * @param angle Ángulo de movimiento
     */
    public void init(float x, float y, float angle) {
        setPosition(x, y);
        this.angle = angle;
        time = 2f;
        stateTime = 0;
        shouldRemove = false;
        defineBullet(x, y, size);
    }

    /**
     * Restablece la bala para ser reutilizada en un pool.
     */
    @Override
    public void reset() {
        setPosition(0, 0);
        if (body != null) {
            body.setLinearVelocity(0, 0);
            body = null;
        }
    }
}
