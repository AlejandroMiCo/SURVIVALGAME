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
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Meat;

public class WorldContactListener implements ContactListener {

    private Knight knight;

    public WorldContactListener(Knight knight) {
        this.knight = knight;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // System.out.println("Colisión detectada: " + fixA.getFilterData().categoryBits
        // + " con "
        // + fixB.getFilterData().categoryBits);

        switch (cDef) {
            case IslandsSurvivors.BULLET_BIT | IslandsSurvivors.ENEMY_BIT:
                Bullet bullet;
                Enemy enemy; // Suponiendo que tenemos una referencia al jugador

                if (fixA.getFilterData().categoryBits == IslandsSurvivors.BULLET_BIT) {
                    bullet = (Bullet) fixA.getUserData();
                    enemy = (Enemy) fixB.getUserData();
                } else {
                    bullet = (Bullet) fixB.getUserData();
                    enemy = (Enemy) fixA.getUserData();
                }

                int damage = DamageCalculator.calculateDamage(knight, bullet);
                enemy.takeDamage(damage);
                bullet.markForRemoval();
                break;

            case IslandsSurvivors.PLAYER_BIT | IslandsSurvivors.ENEMY_BIT:
                System.out.println("¡Colisión con el jugador!");

                Enemy enemySource;

                if (fixA.getFilterData().categoryBits == IslandsSurvivors.ENEMY_BIT) {
                    enemySource = (Enemy) fixA.getUserData();
                } else {
                    enemySource = (Enemy) fixB.getUserData();
                }

                if (enemySource != null) {
                    knight.receiveDamage(enemySource.getDamage());
                }
                break;
            case IslandsSurvivors.ITEM_BIT | IslandsSurvivors.PLAYER_BIT:
                CollectedItem coin;
                if (fixA.getFilterData().categoryBits == IslandsSurvivors.ITEM_BIT) {
                    coin = (CollectedItem) fixA.getUserData();
                } else {
                    coin = (CollectedItem) fixB.getUserData();
                }

                coin.collect();
                if (coin instanceof Meat) {
                    knight.eat();
                }
                System.out.println("Ahora soy mas rico muahahahha");
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
