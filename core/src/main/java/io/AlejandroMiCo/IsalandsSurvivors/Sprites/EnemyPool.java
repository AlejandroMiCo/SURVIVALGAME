package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.utils.Pool;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class EnemyPool extends Pool<Enemy> {
    private PlayScreen screen;
    private Knight knight;
    private int enemyType = 0;

    public EnemyPool(PlayScreen screen, Knight knight) {
        this.screen = screen;
        this.knight = knight;
    }

    // Método para obtener un enemigo del pool

    public Enemy obtain(float x, float y) {
        Enemy enemy = super.obtain(); // Obtiene un enemigo de la pool

        if (enemy == null) {
            // Solo crear si realmente no hay disponibles en la pool
            enemy = newObject();
        } else {
            // Reutilizar un enemigo de la pool, cambiando su tipo
            enemy.reinitialize(x, y);
        }

        return enemy;
    }

    public void setEnemyType(int enemyType) {
        this.enemyType = enemyType;
    }

    @Override
    public void free(Enemy enemy) {
        enemy.getBody().setLinearVelocity(0, 0); // Detener movimiento
        enemy.setPosition(0, 0); // Reiniciar posición
        enemy.setActive(false); // Marcarlo como inactivo
    }

    @Override
    protected Enemy newObject() {
        switch (enemyType) {
            case 0:
                return new Coco(screen, randomX(), randomY(), knight);
            case 1:
                return new TorchGobling(screen, randomX(), randomY(), knight);
            case 2:
                return new TntGobling(screen, randomX(), randomY(), knight);
            default:
                return new EnemyWarrior(screen, randomX(), randomY(), knight);
        }
    }

    private float randomX() {
        return (float) ((Math.random() * 22) + 5);
    }

    private float randomY() {
        return (float) ((Math.random() * 23) + 5);
    }

}
