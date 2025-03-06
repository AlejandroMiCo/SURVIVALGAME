package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Combat.Bullet;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Coin;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.CollectedItem;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Enemy;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Experience;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Player;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Meat;

public class WorldContactListener implements ContactListener {

    private Player player;

    /**
     * Constructor de la clase WorldContactListener.
     * 
     * @param player Instancia del personaje principal.
     */
    public WorldContactListener(Player player) {
        this.player = player;
    }

    /**
     * Método auxiliar para iniciar un contacto.
     * 
     * @param contact Contacto a iniciar.
     */
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            // Colision entre bala y enemigo
            case IslandsSurvivors.BULLET_BIT | IslandsSurvivors.ENEMY_BIT:
                Bullet bullet;
                Enemy enemy;

                // Obtener las entidades que colisionan
                if (fixA.getFilterData().categoryBits == IslandsSurvivors.BULLET_BIT) {
                    bullet = (Bullet) fixA.getUserData();
                    enemy = (Enemy) fixB.getUserData();
                } else {
                    bullet = (Bullet) fixB.getUserData();
                    enemy = (Enemy) fixA.getUserData();
                }

                // Calcular el daño y aplicarlo al enemigo
                int damage = DamageCalculator.calculateDamage(player, bullet);
                enemy.takeDamage(damage);
                bullet.markForRemoval();
                break;

            // Colision entre jugador y enemigo
            case IslandsSurvivors.PLAYER_BIT | IslandsSurvivors.ENEMY_BIT:
                Enemy enemySource;

                // Obtener las entidades que colisionan
                if (fixA.getFilterData().categoryBits == IslandsSurvivors.ENEMY_BIT) {
                    enemySource = (Enemy) fixA.getUserData();
                } else {
                    enemySource = (Enemy) fixB.getUserData();
                }

                // Aplicar daño al jugador
                if (enemySource != null) {
                    player.receiveDamage(enemySource.getDamage());
                }
                break;

            // Colision entre objeto y jugador
            case IslandsSurvivors.ITEM_BIT | IslandsSurvivors.PLAYER_BIT:
                CollectedItem item;

                // Obtener las entidades que colisionan
                if (fixA.getFilterData().categoryBits == IslandsSurvivors.ITEM_BIT) {
                    item = (CollectedItem) fixA.getUserData();
                } else {
                    item = (CollectedItem) fixB.getUserData();
                }

                // Recolectar el objeto
                item.collect();
                if (item instanceof Meat) {
                    player.eat();
                } else if (item instanceof Coin) {
                    player.addCoin();
                } else if (item instanceof Experience) {
                    player.gainXP(((Experience) item).getExpValue());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
