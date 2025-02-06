package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Combat.Bullet;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.TorchGobling;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        System.out.println("Colisión detectada: " + fixA.getFilterData().categoryBits + " con "
                + fixB.getFilterData().categoryBits);

        switch (cDef) {
            case IslandsSurvivors.BULLET_BIT | IslandsSurvivors.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == IslandsSurvivors.BULLET_BIT) {
                    ((Bullet) fixA.getUserData()).markForRemoval();
                ((TorchGobling) fixB.getUserData()).takeDamage(10);  //TODO: Cambiar el daño al daño del personaje o lo que sea

                } else {
                    ((Bullet) fixB.getUserData()).markForRemoval();
                    ((TorchGobling) fixA.getUserData()).takeDamage(10);
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
