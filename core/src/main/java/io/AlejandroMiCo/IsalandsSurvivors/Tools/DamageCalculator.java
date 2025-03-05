package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import java.util.Random;

import io.AlejandroMiCo.IsalandsSurvivors.Combat.Bullet;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Player;

public class DamageCalculator {
    private static final Random random = new Random();

    public static int calculateDamage(Player knight, Bullet bullet) {
        // Determinar si el golpe es crítico
        boolean isCritical = (random.nextFloat()*100) < knight.getCritChance();
        int finalDamage = isCritical ? Math.round(knight.getAttackDamage() * 1.5f) : knight.getAttackDamage();
        return finalDamage;
    }
}
