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

/**
 * Clase que representa un ítem que se colecciona en el juego.
 * Se encarga de manejar el movimiento del ítem y su posición en el mundo.
 */
public class CollectedItem extends Sprite {
    private World world;
    private Body body;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    private boolean collected = false;
    public Animation<TextureRegion> animation;
    private float stateTimer;
    private Player player;

    /**
     * Constructor de la clase CollectedItem.
     * 
     * @param world  Mundo físico.
     * @param x      Posición X.
     * @param y      Posición Y.
     * @param player Instancia del personaje principal.
     * @param file   Archivo de la textura del objeto.
     */
    public CollectedItem(World world, float x, float y, Player player, String file) {
        this.world = world;
        this.player = player;

        stateTimer = 0;

        // Cargar textura de la moneda
        animation = getAnimation(new Texture(file));

        setSize(64 / IslandsSurvivors.PPM, 64 / IslandsSurvivors.PPM); // Tamaño ajustado al PPM

        // Definición del cuerpo de la moneda
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(x, y));
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        body = world.createBody(bodyDef);

        // Definición de la colisión
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / 100f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true; // 🔹 Sensor para solo detectar colisiones
        fixtureDef.filter.categoryBits = IslandsSurvivors.ITEM_BIT;
        fixtureDef.filter.maskBits = IslandsSurvivors.PLAYER_BIT;
        body.createFixture(fixtureDef).setUserData(this); // `this` para identificar la moneda

        // Ajustar la posición del objeto
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        shape.dispose();
    }

    public void update(float dt) {
        setRegion(getFrame(dt));
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        // Posiciones del jugador
        float knightX = player.getB2body().getPosition().x;
        float knightY = player.getB2body().getPosition().y;

        // Distancia entre moneda y jugador
        float dx = body.getPosition().x - knightX;
        float dy = body.getPosition().y - knightY;
        float distance = Vector2.dst(body.getPosition().x, body.getPosition().y, knightX, knightY);

        // Movimiento hacia el jugador si está cerca en funcion del radio de atracción
        // del jugador
        if (distance < player.getAbsorptionRadius()) {
            float attractionSpeed = 2f;
            body.setLinearVelocity(
                    -attractionSpeed * (dx / distance),
                    -attractionSpeed * (dy / distance));
        } else {
            body.setLinearVelocity(0, 0); // Si está lejos o se aleja demasiado, el objeto se queda quieto
        }

        // Destrucción si se recoge
        if (collected && body != null) {
            world.destroyBody(body);
            body = null;
        }
    }

    /**
     * Método auxiliar para obtener la animación del objeto.
     *
     * 
     * @param dt Delta time (tiempo transcurrido desde el último frame).
     * @return La animación del objeto.
     */
    public TextureRegion getFrame(float dt) {

        TextureRegion region;
        region = animation.getKeyFrame(stateTimer, false);

        stateTimer += dt;
        return region;
    }

    /**
     * Método auxiliar para dibujar el objeto. Lo dibuja si el objeto no ha sido
     * recolectado.
     * 
     * @param batch SpriteBatch para renderizar el objeto.
     */
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

    /**
     * Método auxiliar para obtener la animación del objeto.
     * 
     * @param imagen Textura de la animación del objeto.
     * @return La animación del objeto.
     */
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
