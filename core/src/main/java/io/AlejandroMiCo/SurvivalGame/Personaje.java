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

public class Personaje {
    private Animation<TextureRegion> animacion;
    private float tiempo;
    private TextureRegion[] regionsMovimiento;
    private Texture imagen;
    private TextureRegion frameActual;
    private Body body;
    private VirtualJoystick joystick;
    private static final float PPM = 100; // Pixeles por metro

    public Personaje(World world, VirtualJoystick joystick) {
        this.joystick = joystick;
        imagen = new Texture("img/pj.png");
        TextureRegion[][] tmp = TextureRegion.split(imagen, imagen.getWidth() / 8, imagen.getHeight() / 8);

        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[4][i];
        }

        animacion = new Animation<>(0.1f, regionsMovimiento);
        tiempo = 0f;

        // Crear el cuerpo del personaje en Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(100 / PPM, 100 / PPM);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(64 / 2 / PPM, 64 / 2 / PPM); // Ajusta el tamaño según sea necesario

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void update(float delta) {
        tiempo += delta;

        Vector2 direction = joystick.getDirection();
        body.setLinearVelocity(direction.scl(200 * delta)); // Ajusta la velocidad según sea necesario
    }

    public void render(SpriteBatch batch) {
        frameActual = animacion.getKeyFrame(tiempo, true);
        batch.draw(frameActual, body.getPosition().x * PPM - 64, body.getPosition().y * PPM - 64, 128, 128); // Ajusta el tamaño según sea necesario
    }

    public void dispose() {
        imagen.dispose();
    }
}