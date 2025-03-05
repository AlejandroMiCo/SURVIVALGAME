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
import io.AlejandroMiCo.IsalandsSurvivors.Tools.PreferencesManager;

/**
 * Pantalla principal del menú del juego.
 * Muestra opciones como jugar, ajustes, récords y salir del juego.
 */
public class MainMenuScreen implements Screen {
    private final IslandsSurvivors game;
    private final Stage stage;
    private final OrthographicCamera camera;
    private final Texture fondo, titulo, pergamino;
    private Music music; // La música se manejará aquí
    private final TextButtonStyle blueStyle, redStyle;

    /**
     * Constructor de la pantalla de menú principal.
     * 
     * @param game Referencia al juego principal.
     */
    public MainMenuScreen(final IslandsSurvivors game) {
        this.game = game;

        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        String language = PreferencesManager.getLanguage();
        Assets.loadLanguage(language);

        // Cargar música y aplicar el volumen desde preferencias o valor guardado
        music = Assets.manager.get("music/menuSong.ogg");
        music.setLooping(true);
        music.setVolume(PreferencesManager.getMusicVolume()); // Usamos PreferencesManager para obtener el volumen
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
        TextButton recordsButton = createButton("menu.records", blueStyle);
        TextButton exitButton = createButton("menu.exit", redStyle);

        // Listeners
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
                music.stop();
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

        recordsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new RecordsScreen(game));
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
        table.add(recordsButton).padBottom(5).size(150, 50).row();
        table.add(exitButton).padBottom(5).size(150, 50).row();

        stage.addActor(table);
    }

    /**
     * Método auxiliar para crear un estilo de botón.
     * 
     * @param textureUp   Imagen cuando el botón no está presionado.
     * @param textureDown Imagen cuando el botón está presionado.
     * @return Un estilo de botón configurado.
     */
    private TextButtonStyle createButtonStyle(String textureUp, String textureDown) {
        TextButtonStyle style = new TextButtonStyle();
        style.up = new TextureRegionDrawable(Assets.manager.get(textureUp, Texture.class));
        style.down = new TextureRegionDrawable(Assets.manager.get(textureDown, Texture.class));
        style.font = IslandsSurvivors.font;
        style.fontColor = Color.BLACK;
        return style;
    }

    /**
     * Método auxiliar para crear un botón con texto.
     * 
     * @param textKey Clave del texto en los recursos de localización.
     * @param style   Estilo del botón.
     * @return Un botón configurado.
     */
    private TextButton createButton(String textKey, TextButtonStyle style) {
        TextButton button = new TextButton(Assets.getText(textKey), style);
        button.padBottom(10);
        return button;
    }

    @Override
    public void render(float delta) {
        // Limpiar la pantalla
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);

        // Dibujar elementos en la pantalla
        game.batch.begin();
        game.batch.draw(fondo, 0, 0, IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT);
        game.batch.draw(titulo, 145, 312, titulo.getWidth(), titulo.getHeight());
        game.batch.end();

        // Dibujar el escenario con la UI
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
