package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public boolean setToDestroy;
    public boolean destroyed;
    public Knight knight;

    public static int INITIAL_HEALTH = 20;
    public static int INITIAL_DAMAGE = 5;
    public static float INITIAL_SPEED = 75;

    protected int health;
    protected int damage;
    protected float speed;

    public boolean deathAnimationFinished;

    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    private float stateTime;
    public boolean shouldFaceRight;

    public Enemy(PlayScreen screen, float x, float y, Knight knight, String walkFile) {
        this.health = INITIAL_HEALTH;
        this.damage = INITIAL_DAMAGE;
        this.speed = INITIAL_SPEED;
        this.world = screen.getWorld();
        this.screen = screen;
        this.knight = knight;

        walkAnimation = getAnimation(new Texture(walkFile));
        deathAnimation = getAnimation(new Texture("img/Dead_custom.png"));

        stateTime = 0;

        setToDestroy = false;
        destroyed = false;
        deathAnimationFinished = false;

        defineEnemy();
        setBounds(getX(), getY(), 96 / IslandsSurvivors.PPM, 96 / IslandsSurvivors.PPM);
        setPosition(x, y);
    }

    public void defineEnemy() {

        BodyDef bdef = new BodyDef();
        bdef.position.set((float) ((Math.random() * 20) + 3), (float) ((Math.random() * 20) + 3));

        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fedef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(12 / IslandsSurvivors.PPM, 12 / IslandsSurvivors.PPM);
        fedef.shape = shape;

        fedef.friction = 1;
        fedef.density = 100;

        fedef.filter.categoryBits = IslandsSurvivors.ENEMY_BIT; // El Gobling pertenece a la categoría "enemigo"
        fedef.filter.maskBits = IslandsSurvivors.BULLET_BIT | IslandsSurvivors.PLAYER_BIT | IslandsSurvivors.DEFAULT_BIT
                | IslandsSurvivors.ENEMY_BIT;
        b2body.createFixture(fedef).setUserData(this);
    }

    public abstract void takeDamage(int dmg);

    public Animation<TextureRegion> getAnimation(Texture imagen) {
        TextureRegion[][] tmp;
        TextureRegion[] regionsMovimiento;

        tmp = TextureRegion.split(imagen, imagen.getWidth() / 6, imagen.getHeight());
        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[0][i];
        }
        return new Animation<>(0.125f, regionsMovimiento);
    }

    public void draw(Batch batch) {
        if (!deathAnimationFinished) {
            super.draw(batch);
        }
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
            b2body.setLinearVelocity(direction.scl(speed * dt));

            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));

            shouldFaceRight = knight.b2body.getPosition().x > b2body.getPosition().x;
            if (!shouldFaceRight) {
                flip(true, false);
            }

        }
    }

    public int getDamage() {
        return damage;
    }

}
