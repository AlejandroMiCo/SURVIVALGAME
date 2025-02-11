package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

public class GameOverScreen implements Screen {
    private IslandsSurvivors game;
    private Stage stage;
    private OrthographicCamera camera;
    private float deathTimer = 0;

    public GameOverScreen(final IslandsSurvivors game) {
        this.game = game;
        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(new Texture((Gdx.files.internal("ui/boton_azul.png"))));
        btnStyle.font = skin.getFont("default-font");
        btnStyle.fontColor = Color.BLACK;

        Label gameOverLabel = new Label("GAME OVER", skin);
        TextButton retryButton = new TextButton("Reintentar", btnStyle);
        TextButton mainMenuButton = new TextButton("Menu Principal", btnStyle);

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
                // game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        table.add(gameOverLabel).padBottom(20).row();
        table.add(retryButton).padBottom(10).row();
        table.add(mainMenuButton).padBottom(10).row();

        stage.addActor(table);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();

        deathTimer += delta;
        if (deathTimer >= 1) {
            game.setScreen(new GameOverScreen(game));
        }
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
