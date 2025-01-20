package io.AlejandroMiCo.SurvivalGame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class Enemigo {
    private Animation<TextureRegion> animacion;
    private float tiempo;
    private TextureRegion[] regionsMovimiento;
    private Texture imagen;
    private TextureRegion frameActual;
    private Body body;
    private int health;
    private Personaje personaje;
    private static final float PPM = 100; // Pixeles por metro

    public Enemigo(World world, Personaje personaje, float minX, float minY, float maxX, float maxY) {
        this.personaje = personaje;
        imagen = new Texture("img/enemy.png");
        TextureRegion[][] tmp = TextureRegion.split(imagen, imagen.getWidth() / 7, imagen.getHeight() / 5);

        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[1][i];
        }

        health = 50; // Vida inicial del enemigo

        animacion = new Animation<>(0.1f, regionsMovimiento);
        tiempo = 0f;

        // Crear el cuerpo del enemigo en Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Generar una posición aleatoria dentro del mapa
        Random rand = new Random();
        float x = rand.nextFloat(minX,maxX);
        float y = rand.nextFloat(minY,maxY);
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / PPM, 16 / PPM); // Ajusta el tamaño según sea necesario

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void update(float delta) {
        tiempo += delta;
        // Lógica de movimiento del enemigo (si es necesario)
        Vector2 direction = new Vector2(personaje.getBody().getPosition()).sub(body.getPosition()).nor();
        body.setLinearVelocity(direction.scl(100 * delta));

        if (personaje.getBody().getPosition().x < body.getPosition().x) {
            for (TextureRegion region : regionsMovimiento) {
                if (!region.isFlipX()) {
                    region.flip(true, false);
                }
            }
        } else {
            for (TextureRegion region : regionsMovimiento) {
                if (region.isFlipX()) {
                    region.flip(true, false);
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        frameActual = animacion.getKeyFrame(tiempo, true);
        batch.draw(frameActual, body.getPosition().x * PPM - 64, body.getPosition().y * PPM - 64, 128, 128);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            // Lógica para manejar la muerte del enemigo
        }
    }

    public Body getBody() {
        return body;
    }

    public int getHealth() {
        return health;
    }

    public void dispose() {
        imagen.dispose();
    }
}
