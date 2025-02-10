package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import io.AlejandroMiCo.IsalandsSurvivors.Tools.VirtualJoystick;

public class Knight extends Sprite {

    // Posibles estados del personaje
    public enum State {
        IDDLE, MOVING, ATTACKING
    }

    public State currentState;
    public State previousState;

    // Animaciones
    private Animation<TextureRegion> movingAnimation;
    private Animation<TextureRegion> iddleAnimation;

    private TextureRegion[][] tmp;
    private TextureRegion[] regionsMovimiento;

    // Mundo y cuerpo del personaje
    public World world;
    public Body b2body;

    private float stateTimer;
    private boolean movingRight;

    // Joystick para mover el personaje
    private VirtualJoystick joystick;

    // Estadisticas del personaje
    public float timebetweenattacks;
    private int level;
    private int xp;
    private int xpToNextLevel;

    private HashMap<String, Float> atributos;

    public boolean hasLeveledUp = false;

    public Knight(PlayScreen screen, VirtualJoystick joy) {
        super(new Texture("creatures/Warrior_Blue.png"), 196, 196);
        this.joystick = joy;
        this.world = screen.getWorld();
        defineKnight();

        currentState = State.IDDLE;
        previousState = State.IDDLE;
        stateTimer = 0;
        movingRight = true;

        iddleAnimation = getAnimation(new Texture("creatures/Warrior_Blue.png"), 0);
        movingAnimation = getAnimation(new Texture("creatures/Warrior_Blue.png"), 1);

        setBounds(0, 0, 96 / IslandsSurvivors.PPM, 96 / IslandsSurvivors.PPM);
        this.level = 1;
        this.xp = 0;
        this.xpToNextLevel = 20; // Se necesita 100 XP para subir al nivel 2

        atributos = new HashMap<>();
        atributos.put("vida", 100f);
        atributos.put("velocidad", 100f);
        atributos.put("daño", 10f);
        atributos.put("velocidad_ataque", 1.5f);
        atributos.put("critico", 0f);

        timebetweenattacks = 0;

        this.xp = 0;
        this.level = 1;
    }

    public void gainXP(int amount) {
        xp += amount;
        if (xp >= xpToNextLevel) {
            hasLeveledUp = true;
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        xp -= xpToNextLevel; // Mantiene el exceso de XP
        xpToNextLevel *= 1.2; // La siguiente subida de nivel requiere más XP

    }

    // Se encarga de actualizar la camara y la animacion
    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        timebetweenattacks += dt;

        Vector2 direction = joystick.getDirection();
        b2body.setLinearVelocity(direction.scl(atributos.get("velocidad") * dt));
    }

    // Devuelve la animacion en funcion del estado actual del personaje
    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;

        switch (currentState) {
            case MOVING:
                region = movingAnimation.getKeyFrame(stateTimer, true);
                break;
            default:
                region = iddleAnimation.getKeyFrame(stateTimer, true);
                break;
        }

        // Gira la animacion en funcion de la direccion del personaje
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

    // Devuelve el estado actual del personaje
    public State getState() {
        if (b2body.getLinearVelocity().x != 0 || b2body.getLinearVelocity().y != 0) {
            return State.MOVING;
        } else {
            return State.IDDLE;
        }
    }

    // Define el cuerpo 2d del personaje
    public void defineKnight() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(300 / IslandsSurvivors.PPM, 300 / IslandsSurvivors.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        FixtureDef fedef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(12 / IslandsSurvivors.PPM, 12 / IslandsSurvivors.PPM);

        fedef.friction = 1;
        fedef.density = 200;

        fedef.filter.categoryBits = IslandsSurvivors.PLAYER_BIT;
        fedef.filter.maskBits = IslandsSurvivors.ENEMY_BIT | IslandsSurvivors.DEFAULT_BIT;

        fedef.shape = shape;
        b2body.createFixture(fedef);
    }

    // Se encarga de establecer las animaciones basicas del personaje
    public Animation<TextureRegion> getAnimation(Texture imagen, int fila) {
        tmp = TextureRegion.split(imagen, imagen.getWidth() / 6, imagen.getHeight() / 8);
        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[fila][i];
        }
        return new Animation<>(0.125f, regionsMovimiento);
    }

    public int getExperience() {
        return xp;
    }

    public int getNextLevelExperience() {
        return xpToNextLevel;
    }

    public int getLevel() {
        return level;
    }

    public int getAttackDamage() {
        return atributos.get("daño").intValue();
    }

    public float getCritChance() {
        return atributos.get("critico").floatValue(); // Probabilidad de crítico del personaje
    }

    public void mejorarAtributo(String atributo, float cantidad) {
        if (atributos.containsKey(atributo)) {
            atributos.put(atributo, atributos.get(atributo) + cantidad);
            System.out.println("Se mejoró " + atributo + " en " + cantidad);
        }
    }
}
