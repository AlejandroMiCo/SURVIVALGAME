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

    public VictoryScreen(final IslandsSurvivors game, int playerLevel, int enemiesDefeated, int coins) {
        this.playerLevel = playerLevel;
        this.enemiesDefeated = enemiesDefeated;

        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        int score = this.playerLevel * Math.max(this.enemiesDefeated, 1) * Math.max(coins, 1);

        PreferencesManager.saveHighScore(score);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(Assets.manager.get("ui/boton_azul.png", Texture.class));
        btnStyle.font = new BitmapFont();
        btnStyle.fontColor = Color.BLACK;

        // Obtener textos traducidos
        String victoryText = Assets.getText("game.victory");
        String statsText = String.format(Assets.getText("game.stats"), playerLevel, enemiesDefeated, score);
        String continueText = Assets.getText("game.continue");
        String mainMenuText = Assets.getText("game.main_menu");

        // Crear labels con traducción
        Label victoryLabel = new Label(victoryText, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label statsLabel = new Label(statsText, new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // Botones con traducción
        TextButton continueButton = new TextButton(continueText, btnStyle);
        TextButton mainMenuButton = new TextButton(mainMenuText, btnStyle);

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
                dispose();
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        table.add(victoryLabel).padBottom(20).row();
        table.add(statsLabel).padBottom(20).row(); // Espacio para las estadísticas
        table.add(continueButton).padBottom(10).row();
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
