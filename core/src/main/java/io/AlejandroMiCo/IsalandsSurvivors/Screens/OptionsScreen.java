package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import io.AlejandroMiCo.IsalandsSurvivors.Tools.Assets;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.PreferencesManager;

public class OptionsScreen implements Screen {
    private Stage stage;
    private OrthographicCamera camera;
    private Texture fondo;
    private IslandsSurvivors game;

    private float musicVolume;
    private float soundVolume;
    private boolean vibrationEnabled;
    private String language;

    private Music musicMenu, musicGame;
    private Sound soundEffect;

    private Label titleLabel;
    private Label musicLabel;
    private Label soundLabel;
    private Label vibrationLabel;
    private Label languageLabel;

    private TextButton vibrationButton;
    private TextButton languageButton;
    private TextButton creditsButton;
    private TextButton helpButton;
    private TextButton backButton;

    private final Texture pergamino;

    /**
     * Constructor de la pantalla de opciones.
     * 
     * @param game
     */
    public OptionsScreen(final IslandsSurvivors game) {
        this.game = game;
        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        // Cargar recursos desde el AssetManager
        fondo = Assets.manager.get("img/map.png", Texture.class);
        musicMenu = Assets.manager.get("music/menuSong.ogg", Music.class);
        musicGame = Assets.manager.get("music/song.ogg", Music.class);
        soundEffect = Assets.manager.get("sounds/attack.ogg", Sound.class);

        // Cargar preferencias
        loadPreferences();

        // Cargar el idioma seleccionado
        Assets.loadLanguage(language);
        pergamino = Assets.manager.get("ui/pergamino.png");

        // Estilos de botones
        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(Assets.manager.get("ui/boton_azul.png", Texture.class));
        btnStyle.down = new TextureRegionDrawable(Assets.manager.get("ui/boton_azul_press.png", Texture.class));
        btnStyle.font = new BitmapFont();
        btnStyle.fontColor = Color.BLACK;

        TextButtonStyle btnBackStyle = new TextButtonStyle();
        btnBackStyle.up = new TextureRegionDrawable(Assets.manager.get("ui/boton_rojo.png", Texture.class));
        btnBackStyle.down = new TextureRegionDrawable(Assets.manager.get("ui/boton_rojo_press.png", Texture.class));
        btnBackStyle.font = new BitmapFont();
        btnBackStyle.fontColor = Color.BLACK;

        // Inicializar las etiquetas (Labels)
        titleLabel = new Label(Assets.getText("menu.options"), new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        musicLabel = new Label(Assets.getText("menu.music"), new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        soundLabel = new Label(Assets.getText("menu.sound"), new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        vibrationLabel = new Label(Assets.getText("menu.vibration"),
                new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        languageLabel = new Label(Assets.getText("menu.language") + ": " + language,
                new Label.LabelStyle(new BitmapFont(), Color.BLACK));

        // Inicializar los botones antes de usarlos
        vibrationButton = new TextButton(vibrationEnabled ? Assets.getText("menu.on") : Assets.getText("menu.off"),
                btnStyle);
        vibrationButton.padBottom(5);
        languageButton = new TextButton(Assets.getText("menu.change"), btnStyle);
        languageButton.padBottom(5);
        creditsButton = new TextButton(Assets.getText("menu.credits"), btnStyle);
        creditsButton.padBottom(5);
        helpButton = new TextButton(Assets.getText("menu.help"), btnStyle);
        helpButton.padBottom(5);
        backButton = new TextButton(Assets.getText("menu.back"), btnBackStyle);
        backButton.padBottom(5);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(Assets.manager.get("ui/slider.png", Texture.class));
        sliderStyle.knob = new TextureRegionDrawable(Assets.manager.get("ui/sliderKnob.png", Texture.class));
        sliderStyle.knob.setMinHeight(40);
        sliderStyle.knob.setMinWidth(40);

        // Sliders para volumen de música y sonido
        Slider musicSlider = new Slider(0, 1, 0.1f, false, sliderStyle);
        musicSlider.setValue(musicVolume);
        musicSlider.addListener(event -> {
            musicVolume = musicSlider.getValue();
            // Actualizar volumen de la música
            musicMenu.setVolume(musicVolume);
            musicGame.setVolume(musicVolume);
            savePreferences();
            return false;
        });

        Slider soundSlider = new Slider(0, 1, 0.1f, false, sliderStyle);
        soundSlider.setValue(soundVolume);
        soundSlider.addListener(event -> {
            soundVolume = soundSlider.getValue();
            // Actualizar volumen de los efectos de sonido
            soundEffect.setVolume(1, soundVolume); // Asumiendo que tienes un sonido que reproducir
            savePreferences();
            return false;
        });

        // Botón de vibración
        vibrationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                vibrationEnabled = !vibrationEnabled;
                vibrationButton.setText(vibrationEnabled ? Assets.getText("menu.on") : Assets.getText("menu.off"));
                savePreferences(); // Guardar las preferencias
            }
        });

        // Botón de cambio de idioma
        languageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (language.equals("ES")) {
                    language = "EN";
                } else {
                    language = "ES";
                }

                Assets.loadLanguage(language.toLowerCase()); // Cambiar idioma cargando el nuevo archivo
                savePreferences(); // Guardar las preferencias
                game.setScreen(new OptionsScreen(game));
                dispose();
            }
        });

        // Botón de créditos
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CreditsScreen(game));
                dispose();
            }
        });

        // Botón de ayuda
        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HelpScreen(game));
                dispose();
            }
        });

        // Botón para volver al menú principal
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // Crear la tabla principal
        Table table = new Table();
        table.setSize(700, 700);
        table.setPosition((IslandsSurvivors.V_WIDTH / 2) - (table.getWidth() / 2), -160);
        table.setBackground(new TextureRegionDrawable(pergamino));

        // Construcción de la tabla
        table.add(titleLabel).padBottom(10).colspan(2).row();
        table.add(musicLabel).padRight(10);
        table.add(musicSlider).width(150).row();
        table.add(soundLabel).padRight(10);
        table.add(soundSlider).width(150).row();
        table.add(vibrationLabel).padRight(10);
        table.add(vibrationButton).padBottom(5).size(150, 40).row();
        table.add(languageLabel).padRight(10);
        table.add(languageButton).padBottom(5).size(150, 40).row();
        table.add(creditsButton).padBottom(5).size(150, 40);
        table.add(helpButton).padBottom(5).size(150, 40).row();
        table.add(backButton).padBottom(5).size(200, 40).colspan(2).row();

        stage.addActor(table);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Limpiar la pantalla
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);

        // Dibujar elementos en la pantalla
        game.batch.begin();
        game.batch.draw(fondo, 0, 0, IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT);
        game.batch.end();

        // Dibujar el escenario con la UI
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

    // Cargar las preferencias guardadas
    private void loadPreferences() {
        musicVolume = PreferencesManager.getMusicVolume();
        soundVolume = PreferencesManager.getSoundVolume();
        vibrationEnabled = PreferencesManager.isVibrationEnabled();
        language = PreferencesManager.getLanguage();
    }

    // Guardar las preferencias
    private void savePreferences() {
        PreferencesManager.setMusicVolume(musicVolume);
        PreferencesManager.setSoundVolume(soundVolume);
        PreferencesManager.setVibrationEnabled(vibrationEnabled);
        PreferencesManager.setLanguage(language);
    }
}