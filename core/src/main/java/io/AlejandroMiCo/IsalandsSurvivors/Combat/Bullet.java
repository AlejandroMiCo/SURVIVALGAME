package io.AlejandroMiCo.IsalandsSurvivors.Combat;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

public class Bullet extends Sprite {

    private float stateTime;
    private float angle;
    // private float speed;
    private float time;
    private float size;
    private TextureRegion[][] tmp;
    private TextureRegion[] regionsMovimiento;
    private Animation<TextureRegion> animacionMovimiento;
    private boolean shouldRemove = false;

    TextureRegion texture;
    Body body;
    World world;

    private static HashMap<String, Float> atributos = new HashMap<>() {
        {
            put("daño_bala", 10f);
            put("velocidad_bala", 1f);
            put("cooldown_bala", 2f);
            put("critico_bala", 0f);
        }
    };

    public Bullet(World world, float x, float y, float angle) {
        super();
        time = 2f;
        // speed = 1f;
        stateTime = 0;

        // Son necesarios para el bdef
        this.angle = angle;
        this.world = world;
        animacionMovimiento = getAnimation(new Texture("img/purplesun.png"));

        size = 20 / IslandsSurvivors.PPM;
        setSize(size, size);
        setOriginCenter();

        setRotation((float) Math.toDegrees(angle));

        defineBullet(x, y, size);
        // Si se necesitara rotar el sprite para que apunte en la dirección del disparo
        // sprite.setRotation((float) Math.toDegrees(angle));
    }

    // public Rectangle getHitbox() {
    // return hitbox;
    // }

    public void update(float dt) {
        // Resta el tiempo de vida
        time -= dt;
        stateTime += dt;

        // Sincroniza la posición del sprite con la del body (conversión de unidades del
        // mundo a píxeles)
        setPosition(
                body.getPosition().x - getWidth() / 2,
                body.getPosition().y - getHeight() / 2);

        setRegion(animacionMovimiento.getKeyFrame(stateTime, true));
        // Actualiza la rotación del sprite según el ángulo del body
        // sprite.setRotation((float) Math.toDegrees(body.getAngle()));
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
        bdef.position.set(x + offset * (float) Math.cos(angle), y + offset * (float) Math.sin(angle));
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

        body.setLinearVelocity(atributos.get("velocidad_bala") * (float) Math.cos(angle),
                atributos.get("velocidad_bala") * (float) Math.sin(angle));
    }

    public Animation<TextureRegion> getAnimation(Texture imagen) {
        tmp = TextureRegion.split(imagen, imagen.getWidth() / 8, imagen.getHeight());
        regionsMovimiento = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            regionsMovimiento[i] = tmp[0][i];
        }
        return new Animation<>(0.075f, regionsMovimiento);
    }

    public int getDamage() {
        return atributos.get("daño_bala").intValue(); // Daño base de la bala
    }

    public float getCritChance() {
        return atributos.get("critico_bala"); // Daño base de la bala
    }

    public float getCooldown() {
        return atributos.get("cooldown_bala"); // Devuelve el valor del cooldown
    }

    public static void mejorarAtributo(String atributo, float cantidad) {
        if (atributos.containsKey(atributo)) {
            System.out.println("entro");
            atributos.put(atributo, atributos.get(atributo) + cantidad);
            System.out.println("Se mejoró " + atributo + " en " + cantidad);
        }
    }

}
