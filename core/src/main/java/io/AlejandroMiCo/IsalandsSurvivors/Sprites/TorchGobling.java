package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class TorchGobling extends Enemy {

    private float stateTime;
    private TextureRegion[][] tmp;
    private TextureRegion[] regionsMovimiento;
    private Knight knight;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    public boolean shouldFaceRight;

    public boolean deathAnimationFinished;

    public TorchGobling(PlayScreen screen, float x, float y, Knight knight) {
        super(screen, x, y);
        this.knight = knight;

        walkAnimation = getWalkAnimation(new Texture("creatures/torchGobling.png"));

        deathAnimation = getDeathAnimation(new Texture("img/Dead_custom.png"), 0);

        stateTime = 0;

        setBounds(getX(), getY(), 96 / IslandsSurvivors.PPM, 96 / IslandsSurvivors.PPM);

        health = 20;
        setToDestroy = false;
        destroyed = false;
        deathAnimationFinished = false;
    }

    public void update(float dt) {
        stateTime += dt;


        if (setToDestroy && !destroyed) {
            // Animacion de muerte
            world.destroyBody(b2body);
            b2body = null;
            destroyed = true;
            stateTime = 0;
        }
        if (destroyed) {
            setRegion(deathAnimation.getKeyFrame(stateTime, false));

            if (deathAnimation.isAnimationFinished(stateTime)) {
                deathAnimationFinished = true;
            }
        } else {
            // Movimiento del enemigo
            Vector2 direction = new Vector2(knight.b2body.getPosition()).sub(b2body.getPosition()).nor();
            b2body.setLinearVelocity(direction.scl(100 * dt));

            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));

            shouldFaceRight = knight.b2body.getPosition().x > b2body.getPosition().x;
            if (!shouldFaceRight) {
                flip(true, false);
            }

        }
    }

    @Override
    public void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(300 / IslandsSurvivors.PPM, 300 / IslandsSurvivors.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        FixtureDef fedef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(12 / IslandsSurvivors.PPM, 12 / IslandsSurvivors.PPM);

        fedef.shape = shape;

        fedef.friction = 0;
        fedef.density = 0;

        fedef.filter.categoryBits = IslandsSurvivors.ENEMY_BIT; // El Gobling pertenece a la categoría "enemigo"
        fedef.filter.maskBits = IslandsSurvivors.BULLET_BIT | IslandsSurvivors.PLAYER_BIT | IslandsSurvivors.DEFAULT_BIT
                | IslandsSurvivors.ENEMY_BIT;
        b2body.createFixture(fedef).setUserData(this);
    }

    public void draw(Batch batch) {
        if (!deathAnimationFinished) {
            super.draw(batch);
        }
    }

    public Animation<TextureRegion> getWalkAnimation(Texture imagen) {
        tmp = TextureRegion.split(imagen, imagen.getWidth() / 6, imagen.getHeight());
        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[0][i];
        }
        return new Animation<>(0.125f, regionsMovimiento);
    }

    public Animation<TextureRegion> getDeathAnimation(Texture imagen, int fila) {
        tmp = TextureRegion.split(imagen, imagen.getWidth() / 6, imagen.getHeight());
        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[fila][i];
        }
        return new Animation<>(0.125f, regionsMovimiento);
    }

    @Override
    public void takeDamage(int dmg) {
        health -= dmg;
        System.out.println("¡Gobling recibió " + dmg + " de daño! Vida restante: " + health);

        // Si la vida llega a 0, destruir el enemigo
        if (health <= 0) {
            setToDestroy = true;
        }
    }

}
