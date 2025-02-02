package io.AlejandroMiCo.IsalandsSurvivors.Combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

public class Bullet {

    private Sprite sprite;
    private float angle;
    private float speed;
    private float time;
    float size;

    Texture texture;
    Body body;
    World world;

    public Bullet(World world, float x, float y, float angle) {
        this.time = 2f;
        this.speed = 1f;

        // Son necesarios para el bdef
        this.angle = angle;
        this.world = world;

        texture = new Texture("pruebas/bullet.png");
        sprite = new Sprite(texture);
        size = 10 / IslandsSurvivors.PPM;
        sprite.setSize(size, size);
        sprite.setOriginCenter();

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

        // Sincroniza la posición del sprite con la del body (conversión de unidades del
        // mundo a píxeles)
        sprite.setPosition(
                body.getPosition().x - sprite.getWidth() / 2,
                body.getPosition().y - sprite.getHeight() / 2);

        // Actualiza la rotación del sprite según el ángulo del body
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    public boolean isDead() {
        return time < 0 || body == null;
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
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
        // fedef.filter.categoryBits = MyGame.BULLET_BIT; //Futura implementeacion de
        // filtros de colisiones
        // fedef.filter.maskBits = MyGame.ENEMY_BIT | MyGame.WALL_BIT;
        body.createFixture(fedef).setUserData(this);
        shape.dispose();

        body.setLinearVelocity(speed * (float) Math.cos(angle), speed * (float) Math.sin(angle));
    }

}
