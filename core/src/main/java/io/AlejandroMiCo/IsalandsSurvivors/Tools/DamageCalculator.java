package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import java.util.Random;

import io.AlejandroMiCo.IsalandsSurvivors.Combat.Bullet;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Player;

/**
 * Clase que calcula el daño que un jugador inflige a un enemigo.
 */
public class DamageCalculator {
    private static final Random random = new Random();

    /**
     * Método auxiliar para calcular el daño que un jugador inflige a un enemigo.
     * 
     * @param player Instancia del personaje principal.
     * @param bullet Instancia del bala.
     * @return El daño que el jugador inflige a un enemigo.
     */
    public static int calculateDamage(Player player, Bullet bullet) {
        // Determinar si el golpe es crítico
        boolean isCritical = (random.nextFloat() * 100) < player.getCritChance();
        int finalDamage = isCritical ? Math.round(player.getAttackDamage() * 1.5f) : player.getAttackDamage();
        return finalDamage;
    }
}
