package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Combat.Bullet;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Enemy;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;

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
