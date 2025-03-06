package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.utils.Pool;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class EnemyPool extends Pool<Enemy> {
    private PlayScreen screen;
    private Player knight;
    private int enemyType = 0;

    /**
     * Constructor de la clase EnemyPool.
     * 
     * @param screen Pantalla de juego.
     * @param player Instancia del personaje principal.
     */
    public EnemyPool(PlayScreen screen, Player player) {
        this.screen = screen;
        this.knight = player;
    }

    /**
     * Método para obtener un enemigo del pool.
     * 
     * @param x Posición X.
     * @param y Posición Y.
     * @return Un enemigo del pool.
     */
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

    /**
     * Método auxiliar para cambiar el tipo de enemigo que se va a extraer de la
     * pool.
     * 
     * @param enemyType Tipo de enemigo.
     */
    public void setEnemyType(int enemyType) {
        this.enemyType = enemyType;
    }

    /**
     * Método auxiliar para liberar el enemigo de la pool.
     * 
     * @param enemy Enemigo a liberar.
     */
    @Override
    public void free(Enemy enemy) {
        enemy.getBody().setLinearVelocity(0, 0); // Detener movimiento
        enemy.setPosition(0, 0); // Reiniciar posición
        enemy.setActive(false); // Marcarlo como inactivo
    }

    /**
     * Método para crear un nuevo enemigo.
     * 
     * @return Un nuevo enemigo.
     */
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

    /**
     * Método auxiliar para obtener una posición X aleatoria.
     * 
     * @return Posición X aleatoria.
     */
    private float randomX() {
        return (float) ((Math.random() * 22) + 5);
    }

    /**
     * Método auxiliar para obtener una posición Y aleatoria.
     * 
     * @return Posición Y aleatoria.
     */
    private float randomY() {
        return (float) ((Math.random() * 23) + 5);
    }

}
