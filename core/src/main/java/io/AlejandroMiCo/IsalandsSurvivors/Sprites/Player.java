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

public class Player extends Sprite {

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

    public float stateTimer;
    private boolean movingRight;

    // Estadisticas del personaje
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

    private final Vector2 velocity;
    private final Vector2 direction;
    private final VirtualJoystick joystick;
    private final World world;
    private Body b2body;
    private TextureRegion region;

    public Body getB2body() {
        return b2body;
    }

    public void setB2body(Body b2body) {
        this.b2body = b2body;
    }

    /**
     * Constructor de la clase Player.
     * 
     * @param screen Pantalla de juego.
     * @param joy    Instancia del joystick.
     */
    public Player(PlayScreen screen, VirtualJoystick joy) {
        super(new Texture("creatures/Archer_Blue.png"), 196, 196);

        this.joystick = joy;
        this.world = screen.getWorld();
        this.velocity = new Vector2();
        this.direction = new Vector2();
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
        this.xpToNextLevel = 100; // Se necesita 100 XP para subir el primer nivel

        // Atributos del personaje
        atributos = new HashMap<>();
        atributos.put("player_max_health", 100f);
        atributos.put("player_speed", 80f);
        atributos.put("player_damage", 15f);
        atributos.put("player_critical_chance", 0f);
        atributos.put("player_health_regeneration", 0f);
        atributos.put("player_absorption_radius", 0.75f);

        currentHealth = atributos.get("player_max_health");
        this.xp = 0;
        this.level = 1;
    }

    /**
     * Añade experiencia al personaje.
     * 
     * @param amount Cantidad de experiencia a añadir.
     */
    public void gainXP(int amount) {
        xp += amount;
        if (xp >= xpToNextLevel) {
            hasLeveledUp = true;
            levelUp();
        }
    }

    /**
     * Sube al nivel del personaje.
     */
    private void levelUp() {
        level++;
        xp -= xpToNextLevel; // Mantiene el exceso de XP
        xpToNextLevel *= 1.1; // La siguiente subida de nivel requiere más XP

    }

    /**
     * Actualiza la posición, animación y velocidad del personaje.
     * 
     * @param dt Delta time (tiempo transcurrido desde el último frame).
     */
    public void update(float dt) {
        direction.set(joystick.getDirection());
        velocity.set(direction).scl(atributos.get("player_speed") * dt);
        b2body.setLinearVelocity(velocity);

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));

        if (damageTimer > 0) {
            damageTimer -= dt;
            if (damageTimer <= 0) {
                setColor(1, 1, 1, 1);
            }
        }

        // Actualizar la velocidad de curación del personaje
        timeB4Heal -= dt;
        if (timeB4Heal <= 0 && currentState != State.DEAD && currentHealth < getMaxHealth()) {
            // Actualizar la salud del personaje si no está muerto y no está al máximo
            currentHealth += Math.min(atributos.get("player_health_regeneration"),
                    atributos.get("player_max_health") - currentHealth);
            timeB4Heal = 1.0f;
        }
    }

    /**
     * Obtiene el frame actual de la animación del personaje.
     * 
     * @param dt Delta time (tiempo transcurrido desde el último frame).
     * @return El frame actual de la animación.
     */
    public TextureRegion getFrame(float dt) {
        currentState = getState();
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

    /**
     * Obtiene el estado actual del personaje.
     * 
     * @return El estado actual del personaje.
     */
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

    /**
     * Define el cuerpo 2d del personaje.
     */
    public void defineKnight() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(500 / IslandsSurvivors.PPM, 500 / IslandsSurvivors.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        FixtureDef fedef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(12 / IslandsSurvivors.PPM, 12 / IslandsSurvivors.PPM);

        fedef.filter.categoryBits = IslandsSurvivors.PLAYER_BIT;
        fedef.filter.maskBits = IslandsSurvivors.DEFAULT_BIT | IslandsSurvivors.ENEMY_BIT | IslandsSurvivors.ITEM_BIT;

        fedef.shape = shape;
        b2body.createFixture(fedef);
    }

    /**
     * Método auxiliar para establecer las animaciones basicas del personaje.
     * 
     * @param imagen Textura de la animación.
     * @param fila   Fila de la textura de la animación.
     * @return La animación del personaje.
     */
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

    public float getSpeed() {
        return atributos.get("player_speed");
    }

    public float getAbsorptionRadius() {
        return atributos.get("player_absorption_radius");
    }

    public float getCritChance() {
        return atributos.get("player_critical_chance").floatValue(); // Probabilidad de crítico del personaje
    }

    /**
     * Mejora un atributo del personaje.
     * 
     * @param atributo Nombre del atributo a mejorar.
     * @param cantidad Cantidad de mejora.
     */
    public void mejorarAtributo(String atributo, float cantidad) {
        if (atributos.containsKey(atributo)) {
            atributos.put(atributo, atributos.get(atributo) + cantidad);
        }
    }

    /**
     * Recibe daño al personaje.
     * 
     * @param damage Daño recibido.
     */
    public void receiveDamage(float damage) {
        currentHealth -= damage;

        if (currentHealth <= 0) {
            currentHealth = 0;
            b2body.setLinearVelocity(0, 0);
            stateTimer = 0;
        }
        flashDamage();
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public float getMaxHealth() {
        return atributos.get("player_max_health");
    }

    /**
     * Método auxiliar para obtener la animación del personaje.
     * 
     * @param imagen Textura de la animación.
     * @return La animación del personaje.
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

    /**
     * Método auxiliar para recolorear el personaje.
     * 
     */
    private void flashDamage() {
        setColor(1, 0, 0, 0.8f);
        damageTimer = 0.1f;
    }

    /**
     * Añade monedas al personaje.
     */
    public void addCoin() {
        coinCount++;
    }

    public int getCoins() {
        return coinCount;
    }

    /**
     * Añade salud al personaje. (Si está al máximo, no se añade más)
     */
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
