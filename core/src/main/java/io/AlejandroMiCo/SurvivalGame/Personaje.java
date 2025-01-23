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
    public enum State {IDDLE, MOVING}
    public State currenState;
    public State previousState;

    private Animation<TextureRegion> MovingAnimation;
    private Animation<TextureRegion> IddleAnimation;

    private boolean movingRight;
    private float stateTimer;

    private float tiempo;
    private TextureRegion[] regionsMovimiento;
    private Texture imagen;
    private TextureRegion frameActual;
    private Body body;
    private VirtualJoystick joystick;
    private static final float PPM = 100; // Pixeles por metro
    private int health;
    private float damageCooldown;

    public Personaje(World world, VirtualJoystick joystick) {
        this.joystick = joystick;
        imagen = new Texture("img/Warrior_Blue.png");

        currenState = State.IDDLE;
        previousState = State.IDDLE;
        stateTimer = 0;
        movingRight = true;

        TextureRegion[][] tmp = TextureRegion.split(imagen, imagen.getWidth() / 6, imagen.getHeight() / 8);

        health = 100;

        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[1][i];
        }
        MovingAnimation = new Animation<>(0.125f, regionsMovimiento);

        for (int i = 0; i < regionsMovimiento.length; i++) {
            regionsMovimiento[i] = null;
        }

        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[0][i];
        }
        IddleAnimation = new Animation<>(0.125f, regionsMovimiento);

        damageCooldown = 0;
        tiempo = 0f;

        // Crear el cuerpo del personaje en Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(250 / PPM, 250 / PPM);

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
        getFrame(delta);



        if (damageCooldown >0) {
            damageCooldown -= delta;
        }
    }

    public TextureRegion getFrame(float delta){
        currenState = getState(delta);

        TextureRegion region;
        if (currenState == State.MOVING) {
            // Vector2 direction = joystick.getDirection();
            // body.setLinearVelocity(direction.scl(200 * delta)); // Ajusta la velocidad según sea necesario

            // if (direction.x < 0) {
            //     for (TextureRegion region : regionsMovimiento) {
            //         if (!region.isFlipX()) {
            //             region.flip(true, false);
            //         }
            //     }
            // } else if (direction.x > 0) {
            //     for (TextureRegion region : regionsMovimiento) {
            //         if (region.isFlipX()) {
            //             region.flip(true, false);
            //         }
            //     }
            // }

            region = MovingAnimation.getKeyFrame(stateTimer, true);
        }else{
            region = IddleAnimation.getKeyFrame(stateTimer , true);
        }
    }

    public State getState(float delta){
        if (body.getLinearVelocity().x > 0 || body.getLinearVelocity().y > 0) {
            //
            return State.MOVING;
        } else {
            body.setLinearVelocity(0, 0);
            return State.IDDLE;
            // Detener el movimiento
        } // Ajusta la velocidad según sea necesario
    }


    public void render(SpriteBatch batch) {
        frameActual = MovingAnimation.getKeyFrame(tiempo, true);
        batch.draw(frameActual, body.getPosition().x * PPM - 64, body.getPosition().y * PPM - 64, 128, 128);
    }

    public void takeDamage(int damage) {
        if (damageCooldown <= 0) {
            health -= damage;
            damageCooldown = 1; // Reiniciar el temporizador a 1 segundo
            if (health <= 0) {
               System.out.println("pj: Me mori");
            }
        }
    }

    public int getHealth() {
        return health;
    }

    public Body getBody() {
        return body;
    }

    public void dispose() {
        imagen.dispose();
    }
}
