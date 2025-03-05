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

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(Assets.manager.get("ui/boton_azul.png", Texture.class));
        btnStyle.down = new TextureRegionDrawable(Assets.manager.get("ui/boton_azul_press.png", Texture.class));
        btnStyle.font = new BitmapFont();
        btnStyle.fontColor = Color.BLACK;

        Label gameOverLabel = new Label(Assets.getText("game.over"),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));
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
                IslandsSurvivors.font.getData().setScale(1.0f);
                game.setScreen(new MainMenuScreen(game));
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
