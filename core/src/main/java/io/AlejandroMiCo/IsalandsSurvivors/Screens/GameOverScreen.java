package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Combat.Bullet;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Enemy;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.Assets;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.PreferencesManager;

/**
 * Pantalla de Game Over que se muestra cuando el jugador muere.
 * Permite al jugador reiniciar la partida o volver al menú principal.
 */
public class GameOverScreen implements Screen {
    private Stage stage; // Escenario donde se colocarán los elementos de la UI
    private OrthographicCamera camera; // Cámara para la vista del juego

    /**
     * Constructor de la pantalla de Game Over.
     * 
     * @param game Instancia principal del juego.
     */
    public GameOverScreen(final IslandsSurvivors game, int playerLevel, int enemiesDefeated, int coins) {
        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // Estilo de los botones
        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(Assets.manager.get("ui/boton_azul.png", Texture.class));
        btnStyle.down = new TextureRegionDrawable(Assets.manager.get("ui/boton_azul_press.png", Texture.class));
        btnStyle.font = new BitmapFont();
        btnStyle.fontColor = Color.BLACK;

        // Etiqueta de Game Over
        Label gameOverLabel = new Label(Assets.getText("game.over"),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // Botones de "Reintentar" y "Menú principal"
        TextButton retryButton = new TextButton(Assets.getText("game.retry"), btnStyle);
        TextButton mainMenuButton = new TextButton(Assets.getText("game.mainMenu"), btnStyle);

        Bullet.resetBullet();
        Enemy.resetEnemiesStats();

        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
                dispose();
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                IslandsSurvivors.font.getData().setScale(1.0f); // Restablece el tamaño de la fuente
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // Agrega los elementos a la tabla y luego al escenario
        table.add(gameOverLabel).padBottom(20).row();
        table.add(retryButton).padBottom(10).row();
        table.add(mainMenuButton).padBottom(10).row();

        stage.addActor(table);

        // Calcular el puntaje basado en el nivel del jugador, enemigos derrotados y
        // monedas obtenidas
        int score = playerLevel * Math.max(enemiesDefeated, 1) * Math.max(coins, 1);

        // Guardar el puntaje en las preferencias
        PreferencesManager.saveHighScore(score);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    /**
     * Libera los recursos de la pantalla.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
