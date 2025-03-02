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

public class Bullet extends Sprite implements Poolable {

    private float angle;
    private float time;
    private float size;
    private TextureRegion[][] tmp;
    private TextureRegion[] regionsMovimiento;
    private Texture animacionMovimiento;
    private boolean shouldRemove = false;

    private static final Vector2 tmpPosition = new Vector2();
    private static final Vector2 tmpVelocity = new Vector2();

    float stateTime;
    TextureRegion texture;
    Body body;
    World world;

    private static HashMap<String, Float> atributos = new HashMap<>() {
        {
            put("bullet_speed", 1f);
            put("bullet_cooldown", 2.0f);
        }
    };

    public Bullet(World world, float x, float y, float angle) {
        super();
        // Son necesarios para el bdef
        this.angle = angle;
        this.world = world;
        animacionMovimiento = new Texture("img/arrow.png");
    }

    public void update(float dt) {
        // Resta el tiempo de vida
        time -= dt;
        stateTime += dt;

        size = 20 / IslandsSurvivors.PPM;
        setSize(size, size);
        setOriginCenter();

        // Sincroniza la posición del sprite con la del body (conversión de unidades del
        // mundo a píxeles)
        if (body != null) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setRotation((float) Math.toDegrees(angle));
        }
        setScale(size * 10);
        setRegion(animacionMovimiento);
        // Actualiza la rotación del sprite según el ángulo del body
        setRotation((float) Math.toDegrees(angle));
    }

    public void markForRemoval() {
        shouldRemove = true;
    }

    public boolean isDead() {
        return time < 0 || body == null || shouldRemove;
    }

    public Body getBody() {
        return body;
    }

    public void defineBullet(float x, float y, float size) {
        float offset = 0.3f;

        BodyDef bdef = new BodyDef();
        tmpPosition.set(x, y).add((float) Math.cos(angle) * offset, (float) Math.sin(angle) * offset); // Reutilizando
                                                                                                       // Vector2
        bdef.position.set(tmpPosition);
        bdef.angle = angle;
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.bullet = true;

        body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(size / 2); // Radio pequeño

        FixtureDef fedef = new FixtureDef();
        fedef.shape = shape;
        fedef.density = 1f;
        fedef.friction = 0f;
        fedef.restitution = 0f;

        fedef.filter.categoryBits = IslandsSurvivors.BULLET_BIT; // El Gobling pertenece a la categoría "enemigo"
        fedef.filter.maskBits = IslandsSurvivors.ENEMY_BIT | IslandsSurvivors.DEFAULT_BIT;

        body.createFixture(fedef).setUserData(this);
        shape.dispose();

        tmpVelocity.set((float) Math.cos(angle), (float) Math.sin(angle)).scl(atributos.get("bullet_speed"));
        body.setLinearVelocity(tmpVelocity);
    }

    public Animation<TextureRegion> getAnimation(Texture imagen) {
        tmp = TextureRegion.split(imagen, imagen.getWidth() / 8, imagen.getHeight());
        regionsMovimiento = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            regionsMovimiento[i] = tmp[0][i];
        }
        return new Animation<>(0.075f, regionsMovimiento);
    }

    // public int getDamage() {
    //     return atributos.get("daño_bala").intValue(); // Daño base de la bala
    // }

    // public float getCritChance() {
    //     return atributos.get("critico_bala"); // Daño base de la bala
    // }

    public float getCooldown() {
        return atributos.get("bullet_cooldown"); // Devuelve el valor del cooldown
    }

    public static void mejorarAtributo(String atributo, float cantidad) {
        if (atributos.containsKey(atributo)) {
            atributos.put(atributo, atributos.get(atributo) + cantidad);
        }
    }

    public static void resetBullet() {
        atributos.put("bullet_speed", 1.5f);
        atributos.put("bullet_cooldown", 2f);
    }

    public void init(float x, float y, float angle) {
        // Establecer la posición, ángulo y otros parámetros de la bala
        setPosition(x, y);
        this.angle = angle;
        time = 2f;
        stateTime = 0;
        shouldRemove = false;
        defineBullet(x, y, size);

        // Inicializa el cuerpo de Box2D o lo que necesites
    }

    @Override
    public void reset() {
        setPosition(0, 0);
        body.setLinearVelocity(0, 0);
        body = null;
    }
}
