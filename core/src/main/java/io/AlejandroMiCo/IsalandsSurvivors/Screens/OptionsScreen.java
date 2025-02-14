package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

public class OptionsScreen implements Screen {
    private Stage stage;
    private OrthographicCamera camera;
    private Texture fondo;
    private IslandsSurvivors game;

    private float musicVolume = 0.5f;
    private float soundVolume = 0.5f;
    private boolean vibrationEnabled = true;
    private String language = "ES"; // Idioma por defecto (ES = Español)

    public OptionsScreen(final IslandsSurvivors game) {
        this.game = game;
        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setSize(500, 350);
        table.setPosition((IslandsSurvivors.V_WIDTH / 2) - (table.getWidth() / 2), 40);

        fondo = new Texture("img/map.png");

        // Estilos de botones
        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/boton_azul.png")));
        btnStyle.down = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/boton_azul_press.png")));
        btnStyle.font = new BitmapFont();
        btnStyle.fontColor = Color.BLACK;

        TextButtonStyle btnBackStyle = new TextButtonStyle();
        btnBackStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/boton_rojo.png")));
        btnBackStyle.down = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/boton_rojo_press.png")));
        btnBackStyle.font = new BitmapFont();
        btnBackStyle.fontColor = Color.BLACK;

        // Labels
        Label titleLabel = new Label("Opciones", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label musicLabel = new Label("Música", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label soundLabel = new Label("Sonido", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label vibrationLabel = new Label("Vibración", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label languageLabel = new Label("Idioma: " + language, new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/slider.png")));
        sliderStyle.knob = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/sliderKnob.png")));

        // Sliders para volumen de música y sonido
        Slider musicSlider = new Slider(0, 1, 0.1f, false, sliderStyle);
        musicSlider.setValue(musicVolume);
        musicSlider.addListener(event -> {
            musicVolume = musicSlider.getValue();
            // Aquí deberías actualizar el volumen de la música del juego
            return false;
        });

        Slider soundSlider = new Slider(0, 1, 0.1f, false, sliderStyle);
        soundSlider.setValue(soundVolume);
        soundSlider.addListener(event -> {
            soundVolume = soundSlider.getValue();
            // Aquí deberías actualizar el volumen de los efectos de sonido
            return false;
        });

        // Botón de vibración
        TextButton vibrationButton = new TextButton(vibrationEnabled ? "Activado" : "Desactivado", btnStyle);
        vibrationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                vibrationEnabled = !vibrationEnabled;
                vibrationButton.setText(vibrationEnabled ? "Activado" : "Desactivado");
            }
        });

        // Botón de cambio de idioma
        TextButton languageButton = new TextButton("Cambiar", btnStyle);
        languageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (language.equals("ES")) {
                    language = "EN";
                } else {
                    language = "ES";
                }
                languageLabel.setText("Idioma: " + language);
            }
        });

        // Botón de créditos
        TextButton creditsButton = new TextButton("Créditos", btnStyle);
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // game.setScreen(new CreditsScreen(game));
                dispose();
            }
        });

        // Botón de ayuda
        TextButton helpButton = new TextButton("Ayuda", btnStyle);
        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // game.setScreen(new HelpScreen(game));
                dispose();
            }
        });

        // Botón para volver al menú principal
        TextButton backButton = new TextButton("Volver", btnBackStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // Construcción de la tabla
        table.add(titleLabel).padBottom(10).colspan(2).row();
        table.add(musicLabel).padRight(10);
        table.add(musicSlider).width(200).row();
        table.add(soundLabel).padRight(10);
        table.add(soundSlider).width(200).row();
        table.add(vibrationLabel).padRight(10);
        table.add(vibrationButton).width(100).row();
        table.add(languageLabel).padRight(10);
        table.add(languageButton).width(100).row();
        table.add(creditsButton).size(150, 50);
        table.add(helpButton).size(150, 50).row();
        table.add(backButton).size(200, 60).colspan(2).row();

        stage.addActor(table);
    }

    @Override
    public void show() {
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
