package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.utils.Pool;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

public class EnemyPool extends Pool<Enemy> {
    private PlayScreen screen;
    private Knight knight;

    public EnemyPool(PlayScreen screen, Knight knight) {
        this.screen = screen;
        this.knight = knight;
    }

    // Método para obtener un enemigo del pool

    public Enemy obtain(float x, float y, int enemyType) {
        System.out.println("entro");
        Enemy enemy = super.obtain(); // Obtiene un enemigo de la pool

        if (enemy == null) {
            System.out.println("entro");
            // Si no hay enemigos disponibles en la pool, crea uno nuevo según el tipo
            switch (enemyType) {
                case 0:
                    enemy = new Coco(screen, x, y, knight);
                    break;
                case 1:
                    enemy = new TorchGobling(screen, x, y, knight);
                    break;
                case 2:
                    enemy = new TntGobling(screen, x, y, knight);
                    break;
                case 3:
                    enemy = new EnemyWarrior(screen, x, y, knight);
                    break;
                default:
                    enemy = new Coco(screen, x, y, knight);
                    break;
            }
        } else {
            // Reutilizar el enemigo de la pool y resetear sus valores
            enemy.reinitialize(x, y); // <- Necesitas crear este método en Enemy
        }

        return enemy;
    }

    @Override
    public void free(Enemy enemy) {
        enemy.getBody().setLinearVelocity(0, 0); // Detener movimiento
        enemy.setPosition(0, 0); // Reiniciar posición
    }

    @Override
    protected Enemy newObject() {
        return new Coco(screen, (float) ((Math.random() * 23) + 5), (float) ((Math.random() * 23) + 5), knight); // Enemigo base con coordenadas iniciales válidas
    }

}
