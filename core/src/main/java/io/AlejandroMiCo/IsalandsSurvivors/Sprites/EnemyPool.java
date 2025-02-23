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

    @Override
    protected Enemy newObject() {
        return new TorchGobling(screen, max, max, knight);   ///Esto genera los cuadrados vacios
    }
}
