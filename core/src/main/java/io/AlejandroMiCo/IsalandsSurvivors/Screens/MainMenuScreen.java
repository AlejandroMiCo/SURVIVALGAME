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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

public class MainMenuScreen implements Screen {
    private Stage stage;
    private OrthographicCamera camera;
    private Texture fondo, titulo;
    private IslandsSurvivors game;
    float escala = Gdx.graphics.getWidth() / IslandsSurvivors.V_WIDTH;
    private Music music;

    public MainMenuScreen(final IslandsSurvivors game) {
        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);
        this.game = game;

        music = Gdx.audio.newMusic(Gdx.files.internal("music/menuSong.ogg"));
        music.setLooping(true);
        music.play();

        Table table = new Table();
        table.setSize(500, 450);
        table.setPosition((IslandsSurvivors.V_WIDTH / 2) - (table.getWidth() / 2), -60);

        Skin skin = new Skin();
        skin.add("default-font", IslandsSurvivors.font);
        skin.getFont("default-font").getData().setScale(escala/2.5f);

        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/boton_azul.png")));
        btnStyle.down = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/boton_azul_press.png")));
        btnStyle.font = skin.getFont("default-font");
        btnStyle.fontColor = Color.BLACK;

        TextButtonStyle btnExitStyle = new TextButtonStyle();
        btnExitStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/boton_rojo.png")));
        btnExitStyle.down = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/boton_rojo_press.png")));
        btnExitStyle.font = skin.getFont("default-font");
        btnExitStyle.fontColor = Color.BLACK;

        TextButton playButton = new TextButton("Jugar", btnStyle);
      //  TextButton storeButton = new TextButton("Store", btnStyle);
        TextButton optionButton = new TextButton("Opciones", btnStyle);
        TextButton exitButton = new TextButton("Salir", btnExitStyle);

        playButton.padBottom(10);
       // storeButton.padBottom(10);
        optionButton.padBottom(10);
        exitButton.padBottom(10);

        fondo = new Texture("img/map.png");
        titulo = new Texture("ui/title2.png");

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

        // storeButton.addListener(new ClickListener() {
        //     @Override
        //     public void clicked(InputEvent event, float x, float y) {
        //         // game.setScreen(new StoreScreen(game));
        //         dispose();
        //     }
        // });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.background(new TextureRegionDrawable(new Texture("ui/pergamino.png")));
        table.add(playButton).padBottom(5).size(150, 50).row();
    //    table.add(storeButton).padBottom(5).size(150, 50).row();
        table.add(optionButton).padBottom(5).size(150, 50).row();
        table.add(exitButton).padBottom(5).size(150, 50).row();

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

        game.batch.begin();
        game.batch.draw(titulo, 145, 312, titulo.getWidth(), titulo.getHeight());
        game.batch.end();

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
        music.dispose();
        stage.dispose();
    }
}
