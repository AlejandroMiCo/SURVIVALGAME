package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import java.util.Random;

import io.AlejandroMiCo.IsalandsSurvivors.Combat.Bullet;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;

public class DamageCalculator {
    private static final Random random = new Random();

    public static int calculateDamage(Knight knight, Bullet bullet) {
        // Obtener daño base de ambas fuentes
        int bulletDamage = bullet.getDamage();
        int knightDamage = knight.getAttackDamage();
        int totalBaseDamage = bulletDamage + knightDamage;

        // Obtener probabilidad y multiplicador de crítico
        float critChance = knight.getCritChance() + bullet.getCritChance(); // Se usa la del Knight
        float critMultiplier = 1.5f; // Aumenta daño un 50% en crítico

        // Determinar si el golpe es crítico
        boolean isCritical = (random.nextFloat()*100) < critChance;
        if (isCritical) {
            System.out.println("CRÍTICO!");
        }
        int finalDamage = isCritical ? Math.round(totalBaseDamage * critMultiplier) : totalBaseDamage;
        System.out.println(finalDamage);
        return finalDamage;
    }
}
