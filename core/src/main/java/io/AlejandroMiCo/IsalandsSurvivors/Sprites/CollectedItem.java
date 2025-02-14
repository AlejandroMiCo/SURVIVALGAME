package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

public class CollectedItem extends Sprite {
    private World world;
    private Body body;
    private boolean collected = false;

    public Animation<TextureRegion> animation;
    private float stateTimer;
    private Knight knight;

    public CollectedItem(World world, float x, float y, Knight knight, String file) {
        this.world = world;
        this.knight = knight;

        stateTimer = 0;

        // Cargar textura de la moneda
        animation = getAnimation(new Texture(file));

        setSize(64 / IslandsSurvivors.PPM, 64 / IslandsSurvivors.PPM); // Tamaño ajustado al PPM

        // Definición del cuerpo de la moneda
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(x, y));
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        body = world.createBody(bodyDef);

        // Definición de la colisión (círculo pequeño)
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / 100f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true; // 🔹 Sensor para solo detectar colisiones
        fixtureDef.filter.categoryBits = IslandsSurvivors.ITEM_BIT;
        fixtureDef.filter.maskBits = IslandsSurvivors.PLAYER_BIT;
        body.createFixture(fixtureDef).setUserData(this); // `this` para identificar la moneda

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        shape.dispose();
    }

    public void update(float dt) {
        setRegion(getFrame(dt));
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        // Posiciones del jugador
        float knightX = knight.b2body.getPosition().x;
        float knightY = knight.b2body.getPosition().y;

        // Distancia entre moneda y jugador
        float dx = body.getPosition().x - knightX;
        float dy = body.getPosition().y - knightY;
        float distance = Vector2.dst(body.getPosition().x, body.getPosition().y, knightX, knightY);

        // Movimiento hacia el jugador si está cerca
        if (distance < knight.getAbsorptionRadius()) { // 🔹 Radio de atracción
            float attractionSpeed = 2f; // 🔹 Ajusta esto según el comportamiento deseado
            body.setLinearVelocity(
                    -attractionSpeed * (dx / distance),
                    -attractionSpeed * (dy / distance));
        } else {
            body.setLinearVelocity(0, 0); // 🔹 Si está lejos, la moneda se queda quieta
        }

        // Destrucción si se recoge
        if (collected && body != null) {
            world.destroyBody(body);
            body = null;
        }

        // Actualiza posición del Sprite
    }

    public TextureRegion getFrame(float dt) {

        TextureRegion region;
        region = animation.getKeyFrame(stateTimer, false);

        stateTimer += dt;
        return region;
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
            draw(batch);
        }
    }

    public void collect() {
        collected = true;
    }

    public boolean isCollected() {
        return collected;
    }

    public void dispose() {
        if (body != null) {
            world.destroyBody(body);
        }
    }

    public Animation<TextureRegion> getAnimation(Texture imagen) {
        TextureRegion[][] tmp;
        TextureRegion[] regionsMovimiento;

        tmp = TextureRegion.split(imagen, imagen.getWidth() / 7, imagen.getHeight());
        regionsMovimiento = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            regionsMovimiento[i] = tmp[0][i];
        }
        return new Animation<>(0.125f, regionsMovimiento);
    }
}
