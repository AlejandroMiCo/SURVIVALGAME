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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.Assets;

public class HelpScreen implements Screen {
    private IslandsSurvivors game;
    private Stage stage;
    private OrthographicCamera camera;
    private Texture background;
    private Texture pergamino;

    public HelpScreen(final IslandsSurvivors game) {
        this.game = game;
        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        background = Assets.manager.get("img/map.png", Texture.class);
        pergamino = Assets.manager.get("ui/pergamino.png", Texture.class); // Fondo de pergamino

        // Estilo del botón
        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(Assets.manager.get("ui/boton_rojo.png", Texture.class));
        btnStyle.down = new TextureRegionDrawable(Assets.manager.get("ui/boton_rojo_press.png", Texture.class));
        btnStyle.font = new BitmapFont();
        btnStyle.fontColor = Color.BLACK;

        // Título
        Label titleLabel = new Label(Assets.getText("menu.help"), new Label.LabelStyle(new BitmapFont(), Color.BLACK));

        // Contenido de ayuda
        String helpText = Assets.getText("help.content"); // Texto cargado desde assets/lang
        Label helpLabel = new Label(helpText, new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        helpLabel.setWrap(true); // Permite que el texto se ajuste al ancho
        helpLabel.setAlignment(1); // Centra el texto

        // Tabla con contenido desplazable
        Table contentTable = new Table();
        contentTable.add(helpLabel).width(600).pad(10).row(); // Ajustamos el ancho del texto

        ScrollPane scrollPane = new ScrollPane(contentTable);
        scrollPane.setScrollingDisabled(true, false); // Solo desplazamiento vertical

        // Botón de regreso
        TextButton backButton = new TextButton(Assets.getText("menu.back"), btnStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new OptionsScreen(game)); // Vuelve al menú de opciones
                dispose();
            }
        });

        // Tabla principal con fondo de pergamino
        Table mainTable = new Table();
        mainTable.setSize(800, 700); // Ajustamos tamaño del pergamino
        mainTable.setPosition((IslandsSurvivors.V_WIDTH - mainTable.getWidth()) / 2,
                (IslandsSurvivors.V_HEIGHT - mainTable.getHeight()-40) / 2);
        mainTable.setBackground(new TextureRegionDrawable(pergamino));
        mainTable.top();
        mainTable.add(titleLabel).padTop(50).row();
        mainTable.add(scrollPane).width(600).height(300).pad(20).row();
        mainTable.add(backButton).size(200, 50).padBottom(20);

        stage.addActor(mainTable);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background, 0, 0, IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT);
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
