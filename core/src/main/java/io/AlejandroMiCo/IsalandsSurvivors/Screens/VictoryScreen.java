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
import io.AlejandroMiCo.IsalandsSurvivors.Tools.Assets;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.PreferencesManager;

public class VictoryScreen implements Screen {
    private Stage stage;
    private OrthographicCamera camera;

    private int playerLevel;
    private int enemiesDefeated;

    /**
     * Constructor de la pantalla de victoria.
     *
     * @param game Referencia al juego principal.
     * @param playerLevel Nivel del jugador.
     * @param enemiesDefeated Número de enemigos derrotados.
     * @param coins Número de monedas recogidas.
     */
    public VictoryScreen(final IslandsSurvivors game, int playerLevel, int enemiesDefeated, int coins) {
        this.playerLevel = playerLevel;
        this.enemiesDefeated = enemiesDefeated;

        // Inicializar cámara y escenario
        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        // Calcular el puntaje basado en el nivel del jugador, enemigos derrotados y monedas obtenidas
        int score = this.playerLevel * Math.max(this.enemiesDefeated, 1) * Math.max(coins, 1);

        // Guardar el puntaje en las preferencias
        PreferencesManager.saveHighScore(score);

        // Crear tabla para los elementos de la pantalla
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // Estilo de los botones
        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(Assets.manager.get("ui/boton_azul.png", Texture.class));
        btnStyle.font = new BitmapFont();
        btnStyle.fontColor = Color.BLACK;

        // Obtener textos traducidos
        String victoryText = Assets.getText("game.victory");
        String statsText = String.format(Assets.getText("game.stats"), playerLevel, enemiesDefeated, score);
        String mainMenuText = Assets.getText("game.main_menu");

        // Crear labels con traducción
        Label victoryLabel = new Label(victoryText, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label statsLabel = new Label(statsText, new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        TextButton mainMenuButton = new TextButton(mainMenuText, btnStyle);

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                IslandsSurvivors.font.getData().setScale(1.0f);
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // Agregar los elementos a la tabla y luego al escenario
        table.add(victoryLabel).padBottom(20).row();
        table.add(statsLabel).padBottom(20).row(); // Espacio para las estadísticas
        table.add(mainMenuButton).padBottom(10).row();

        stage.addActor(table);
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

    @Override
    public void dispose() {
        stage.dispose();
    }
}
