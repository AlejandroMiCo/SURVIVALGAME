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
        IDDLE, MOVING, DEAD
    }

    public State currentState;
    public State previousState;

    // Animaciones
    public Animation<TextureRegion> movingAnimation;
    public Animation<TextureRegion> iddleAnimation;
    public Animation<TextureRegion> deathAnimation;

    private TextureRegion[][] tmp;
    private TextureRegion[] regionsMovimiento;

    // Mundo y cuerpo del personaje
    public World world;
    public Body b2body;

    public float stateTimer;
    private boolean movingRight;

    // Joystick para mover el personaje
    private VirtualJoystick joystick;

    // Estadisticas del personaje
    public float timebetweenattacks;
    private int level;
    private float xp;
    private float xpToNextLevel;

    private HashMap<String, Float> atributos;

    public boolean hasLeveledUp = false;
    public float damageTimer;

    private float currentHealth;
    private float timeB4Heal;
    private int coinCount;
    private int enemiesDefeated;

    public Knight(PlayScreen screen, VirtualJoystick joy) {
        super(new Texture("creatures/Archer_Blue.png"), 196, 196);
        this.joystick = joy;
        this.world = screen.getWorld();
        defineKnight();

        currentState = State.IDDLE;
        previousState = State.IDDLE;
        stateTimer = 0;
        movingRight = true;
        coinCount = 0;
        timeB4Heal = 1.0f;

        iddleAnimation = getAnimation(new Texture("creatures/Archer_Blue.png"), 0);
        movingAnimation = getAnimation(new Texture("creatures/Archer_Blue.png"), 1);
        deathAnimation = getAnimation(new Texture("img/DeadPlayer.png"));

        setBounds(0, 0, 96 / IslandsSurvivors.PPM, 96 / IslandsSurvivors.PPM);
        this.level = 1;
        this.xp = 0;
        this.xpToNextLevel = 100; // Se necesita 100 XP para subir al nivel 2

        atributos = new HashMap<>();
        atributos.put("player_max_health", 100f);
        atributos.put("player_speed", 100f);
        atributos.put("player_damage", 10f);
        atributos.put("player_critical_chance", 0f);
        atributos.put("player_health_regenarition", 5f);
        atributos.put("player_absorption_radius", 0.75f);

        timebetweenattacks = 0;

        currentHealth = atributos.get("player_max_health");
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
        xpToNextLevel *= 1.1; // La siguiente subida de nivel requiere más XP

    }

    // Se encarga de actualizar la camara y la animacion
    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        timebetweenattacks += dt;

        Vector2 direction = joystick.getDirection();
        b2body.setLinearVelocity(direction.scl(atributos.get("player_speed") * dt));

        if (damageTimer > 0) {
            damageTimer -= dt;
            if (damageTimer <= 0) {
                setColor(1, 1, 1, 1); // Volver al color normal
            }
        }

        timeB4Heal -= dt;
        if (timeB4Heal <= 0 && currentState != State.DEAD && currentHealth < getMaxHealth()) {
            currentHealth += Math.min(atributos.get("player_health_regenarition"),
                    atributos.get("player_max_health") - currentHealth);
            timeB4Heal = 1.0f;
        }
    }

    // Devuelve la animacion en funcion del estado actual del personaje
    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;

        switch (currentState) {
            case DEAD -> region = deathAnimation.getKeyFrame(stateTimer, false);
            case MOVING -> region = movingAnimation.getKeyFrame(stateTimer, true);
            default -> region = iddleAnimation.getKeyFrame(stateTimer, true);
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
        if (currentHealth <= 0) {
            return State.DEAD;
        }
        if (b2body.getLinearVelocity().x != 0 || b2body.getLinearVelocity().y != 0) {
            return State.MOVING;
        } else {
            return State.IDDLE;
        }
    }

    // Define el cuerpo 2d del personaje
    public void defineKnight() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(500 / IslandsSurvivors.PPM, 500 / IslandsSurvivors.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        FixtureDef fedef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(12 / IslandsSurvivors.PPM, 12 / IslandsSurvivors.PPM);

        fedef.friction = 1;
        fedef.density = 200;

        fedef.filter.categoryBits = IslandsSurvivors.PLAYER_BIT;
        fedef.filter.maskBits = IslandsSurvivors.DEFAULT_BIT | IslandsSurvivors.ENEMY_BIT | IslandsSurvivors.ITEM_BIT;

        fedef.shape = shape;
        b2body.createFixture(fedef);
    }

    // Se encarga de establecer las animaciones basicas del personaje
    public Animation<TextureRegion> getAnimation(Texture imagen, int fila) {
        tmp = TextureRegion.split(imagen, imagen.getWidth() / 8, imagen.getHeight() / 7);
        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[fila][i];
        }
        return new Animation<>(0.125f, regionsMovimiento);
    }

    public float getCurrentExperience() {
        return xp;
    }

    public float getNextLevelExperience() {
        return xpToNextLevel;
    }

    public int getLevel() {
        return level;
    }

    public int getAttackDamage() {
        return atributos.get("player_damage").intValue();
    }

    public float getAbsorptionRadius() {
        return atributos.get("player_absorption_radius");
    }

    public float getCritChance() {
        return atributos.get("player_critical_chance").floatValue(); // Probabilidad de crítico del personaje
    }

    public void mejorarAtributo(String atributo, float cantidad) {
        if (atributos.containsKey(atributo)) {
            atributos.put(atributo, atributos.get(atributo) + cantidad);
        }
    }

    public void receiveDamage(float damage) {
        currentHealth -= damage;

        if (currentHealth <= 0) {
            currentHealth = 0;
            b2body.setLinearVelocity(0, 0);
            stateTimer = 0;
            // Aquí puedes agregar lógica de muerte, como una animación o reiniciar el juego
        }
        flashDamage();
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public float getMaxHealth() {
        return atributos.get("player_max_health");
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

    private void flashDamage() {
        setColor(1, 0, 0, 0.8f);
        damageTimer = 0.1f;
    }

    public void addCoin() {
        coinCount++;
    }

    public int getCoins() {
        return coinCount;
    }

    public void eat() {
        if (currentHealth < getMaxHealth()) {
            currentHealth += Math.min(10, getMaxHealth() - currentHealth);
        }
    }

    public int getEnemiesDefeated() {
        return enemiesDefeated;
    }

    public void addEnemyDefeated() {
        enemiesDefeated++;
    }

}
