package io.AlejandroMiCo.IsalandsSurvivors.Combat;

import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Enemy;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;

public class CombatSystem {
    private float attackTimer = 0;

    public void update(float deltaTime, Knight player, Enemy enemies) {
        attackTimer += deltaTime;

        if (attackTimer >= player.getWeapon().getCooldown()) {
            player.getWeapon().atacks(player, enemies);
            attackTimer = 0;
        }
    }
}
