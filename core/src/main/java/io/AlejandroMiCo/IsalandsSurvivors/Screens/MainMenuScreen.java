package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.Assets;

public class MainMenuScreen implements Screen {
    private final IslandsSurvivors game;
    private final Stage stage;
    private final OrthographicCamera camera;
    private final Texture fondo, titulo, pergamino;
    private final Music music;
    private final TextButtonStyle blueStyle, redStyle;

    public MainMenuScreen(final IslandsSurvivors game) {
        this.game = game;

        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        // Cargar música
        music = Assets.manager.get("music/menuSong.ogg");
        music.setLooping(true);
        music.play();

        // Cargar texturas
        fondo = Assets.manager.get("img/map.png");
        titulo = Assets.manager.get("ui/title2.png");
        pergamino = Assets.manager.get("ui/pergamino.png");

        // Crear estilos de botones (una sola vez)
        blueStyle = createButtonStyle("ui/boton_azul.png", "ui/boton_azul_press.png");
        redStyle = createButtonStyle("ui/boton_rojo.png", "ui/boton_rojo_press.png");

        // Crear botones
        Table table = new Table();
        table.setSize(500, 450);
        table.setPosition((IslandsSurvivors.V_WIDTH / 2f) - (table.getWidth() / 2f), -60);
        table.background(new TextureRegionDrawable(pergamino));

        TextButton playButton = createButton("menu.play", blueStyle);
        TextButton optionButton = createButton("menu.options", blueStyle);
        TextButton exitButton = createButton("menu.exit", redStyle);

        // Listeners
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
                dispose();
            }
        });

        optionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new OptionsScreen(game));
                dispose();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Agregar botones a la tabla
        table.add(playButton).padBottom(5).size(150, 50).row();
        table.add(optionButton).padBottom(5).size(150, 50).row();
        table.add(exitButton).padBottom(5).size(150, 50).row();

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

    private TextButton createButton(String textKey, TextButtonStyle style) {
        TextButton button = new TextButton(Assets.getText(textKey), style);
        button.padBottom(10);
        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(fondo, 0, 0, IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT);
        game.batch.draw(titulo, 145, 312, titulo.getWidth(), titulo.getHeight());
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
        Assets.manager.unload("music/menuSong.ogg");
    }

    // Métodos vacíos necesarios por la interfaz Screen
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
