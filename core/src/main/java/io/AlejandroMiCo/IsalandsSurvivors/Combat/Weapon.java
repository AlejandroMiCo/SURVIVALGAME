package io.AlejandroMiCo.IsalandsSurvivors.Combat;

import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Enemy;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;

public interface Weapon {
    void atacks(Knight owner, Enemy target);
    float getCooldown(); // Tiempo entre ataques

    
}
