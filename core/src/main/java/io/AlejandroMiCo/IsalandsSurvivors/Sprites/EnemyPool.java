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
    @Override
    protected Enemy newObject() {
        // Este método debe devolver una nueva instancia de una clase que extienda Enemy
        // Aquí, puedes elegir crear diferentes tipos de enemigos (por ejemplo, Goblin,
        // Skeleton, etc.)
        // O puedes hacerlo de una forma más general, si quieres tener más flexibilidad.

        // Supongamos que creamos un Goblin:
        return new Coco(screen, 0, 0, knight); // Cambia el tipo según la clase que deseas
    }

    public Enemy obtain(float x, float y, int enemyType) {
        // Obtén un enemigo reciclado de la pool
        Enemy enemy = super.obtain();
        enemy.setPosition(x, y);

        // Basado en el tipo de enemigo, puedes asignar un tipo específico
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

        return enemy;
    }

    @Override
    public void free(Enemy enemy) {
        // Aquí puedes hacer alguna limpieza específica antes de liberar el enemigo
        super.free(enemy);
    }
}
