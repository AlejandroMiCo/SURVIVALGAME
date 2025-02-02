package io.AlejandroMiCo.IsalandsSurvivors.Combat;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Enemy;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;

public class MeleeWeapon implements Weapon {
    private float cooldown = 1.0f; // Un ataque por segundo

    public MeleeWeapon() {
        super();
    }

    @Override
    public void atacks(Knight owner, Enemy target) {
        if (!target.setToDestroy
                && target.b2body.getPosition().dst(owner.b2body.getPosition()) < 50 / IslandsSurvivors.PPM) {
            target.health -= owner.damage;

            if (target.health <= 0) {
                target.setToDestroy = true;
            }
            System.out.println("Daño causado: " + owner.damage);
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

}
