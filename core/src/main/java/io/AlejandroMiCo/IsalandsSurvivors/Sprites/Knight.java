package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Combat.MeleeWeapon;
import io.AlejandroMiCo.IsalandsSurvivors.Combat.Weapon;
import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.VirtualJoystick;

public class Knight extends Sprite {
    public enum State {
        IDDLE, MOVING
    }

    public State currentState;
    public State previousState;

    private Animation<TextureRegion> movingAnimation;
    private Animation<TextureRegion> iddleAnimation;

    private TextureRegion[][] tmp;
    private TextureRegion[] regionsMovimiento;

    public World world;
    public Body b2body;

    private float stateTimer;
    private boolean movingRight;

    private VirtualJoystick joystick;

    public int damage;
    private Weapon weapon;

    public Knight(PlayScreen screen, VirtualJoystick joystick) {
        super(new Texture("creatures/Warrior_Blue.png"), 196, 196);
        this.joystick = joystick;
        this.world = screen.getWorld();
        defineKnight();

        currentState = State.IDDLE;
        previousState = State.IDDLE;
        stateTimer = 0;
        movingRight = true;

        iddleAnimation = getAnimation(new Texture("creatures/Warrior_Blue.png"), 0);
        movingAnimation = getAnimation(new Texture("creatures/Warrior_Blue.png"), 1);

        setBounds(0, 0, 96 / IslandsSurvivors.PPM, 96 / IslandsSurvivors.PPM);
        weapon = new MeleeWeapon();
        damage = 10;
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;

        if (currentState == State.IDDLE) {
            region = iddleAnimation.getKeyFrame(stateTimer, true);
        } else {
            region = movingAnimation.getKeyFrame(stateTimer, true);
        }

        if ((b2body.getLinearVelocity().x < 0 || !movingRight) && !region.isFlipX()) {
            region.flip(true, false);
            movingRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || movingRight) && region.isFlipX()) {
            region.flip(true, false);
            movingRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if (b2body.getLinearVelocity().x != 0 || b2body.getLinearVelocity().y != 0) {
            return State.MOVING;
        } else {
            return State.IDDLE;
        }
    }

    public void defineKnight() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(300 / IslandsSurvivors.PPM, 300 / IslandsSurvivors.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        FixtureDef fedef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(12 / IslandsSurvivors.PPM, 12 / IslandsSurvivors.PPM);

        fedef.shape = shape;
        b2body.createFixture(fedef);
    }

    public Animation<TextureRegion> getAnimation(Texture imagen, int fila) {
        tmp = TextureRegion.split(imagen, imagen.getWidth() / 6, imagen.getHeight() / 8);
        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[fila][i];
        }
        return new Animation<>(0.125f, regionsMovimiento);
    }

    public Weapon getWeapon() {
        return weapon;
    }
}
