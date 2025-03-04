package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

public class RecordsScreen implements Screen {
    private final IslandsSurvivors game;
    private final Stage stage;
    private final OrthographicCamera camera;
    private final Texture fondo, pergamino;

    public RecordsScreen(final IslandsSurvivors game) {
        this.game = game;

        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        // Cargar imágenes
        fondo = Assets.manager.get("img/map.png");
        pergamino = Assets.manager.get("ui/pergamino.png");

        // Crear tabla para los récords
        Table table = new Table();
        table.setSize(700, 700);
        table.setPosition((IslandsSurvivors.V_WIDTH / 2) - (table.getWidth() / 2), -160);
        table.background(new TextureRegionDrawable(pergamino));

        // Título
        Label titleLabel = new Label(Assets.getText("records.title"),
                new Label.LabelStyle(IslandsSurvivors.font, Color.BLACK));
        table.add(titleLabel).padBottom(20).row();

        // Obtener los récords guardados y ordenarlos de mayor a menor
        List<Integer> highScores = PreferencesManager.getHighScores();
        Collections.sort(highScores, Collections.reverseOrder()); // Ordenar de mayor a menor

        // Mostrar los puntajes ordenados
        for (int i = 0; i < highScores.size(); i++) {
            Label scoreLabel = new Label((i + 1) + ".-> " + highScores.get(i),
                    new Label.LabelStyle(IslandsSurvivors.font, Color.BLACK));
            table.add(scoreLabel).padBottom(10).row();
        }

        // Botón de volver al menú
        TextButtonStyle buttonStyle = createButtonStyle("ui/boton_rojo.png", "ui/boton_rojo_press.png");
        TextButton backButton = new TextButton(Assets.getText("menu.back"), buttonStyle);
        backButton.padBottom(10);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        table.add(backButton).size(150, 50).row();
        stage.addActor(table);
    }

    private TextButtonStyle createButtonStyle(String textureUp, String textureDown) {
        TextButtonStyle style = new TextButtonStyle();
        style.up = new TextureRegionDrawable(Assets.manager.get(textureUp, Texture.class));
        style.down = new TextureRegionDrawable(Assets.manager.get(textureDown, Texture.class));
        style.font = IslandsSurvivors.font;
        style.fontColor = Color.BLACK;
        return style;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(fondo, 0, 0, IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT);
        game.batch.end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {
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
}
