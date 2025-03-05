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

/**
 * Pantalla de créditos del juego.
 * Muestra información sobre los desarrolladores y colaboradores.
 */
public class CreditsScreen implements Screen {
    private IslandsSurvivors game;
    private Stage stage;
    private OrthographicCamera camera;
    private Texture background;
    private Texture pergamino;

    /**
     * Constructor de la pantalla de créditos.
     * 
     * @param game Instancia principal del juego.
     */
    public CreditsScreen(final IslandsSurvivors game) {
        this.game = game;
        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        // Cargar texturas de fondo y pergamino
        background = Assets.manager.get("img/map.png", Texture.class);
        pergamino = Assets.manager.get("ui/pergamino.png", Texture.class); // Fondo de pergamino

        // Estilo del botón de regreso
        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(Assets.manager.get("ui/boton_rojo.png", Texture.class));
        btnStyle.down = new TextureRegionDrawable(Assets.manager.get("ui/boton_rojo_press.png", Texture.class));
        btnStyle.font = new BitmapFont();
        btnStyle.fontColor = Color.BLACK;

        // Título
        Label titleLabel = new Label(Assets.getText("menu.help"), new Label.LabelStyle(new BitmapFont(), Color.BLACK));

         // Obtener el contenido de los créditos desde los assets
        String helpText = Assets.getText("credits.content");
        String[] lines = helpText.split("\n"); // Dividir por saltos de línea

        Table contentTable = new Table();
        contentTable.top().pad(10);

        // Crear las etiquetas para cada línea del texto de ayuda
        for (String line : lines) {
            Label lineLabel = new Label(line, new Label.LabelStyle(new BitmapFont(), Color.BLACK));
            lineLabel.setWrap(true); // Hacer que el texto se ajuste automáticamente
            lineLabel.setAlignment(1); // Alinear el texto al centro
            contentTable.add(lineLabel).width(350).padBottom(10).row(); // Separamos líneas
        }

        // Colocar la tabla en un ScrollPane
        ScrollPane scrollPane = new ScrollPane(contentTable);
        scrollPane.setScrollingDisabled(false, false); // Permite desplazamiento vertical y horizontal
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true); // Forzar scroll solo vertical
        scrollPane.layout(); // Asegura que el ScrollPane se ajuste bien

        // Botón de regreso
        TextButton backButton = new TextButton(Assets.getText("menu.back"), btnStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new OptionsScreen(game)); // Vuelve al menú de opciones
                dispose();
            }
        });

        // Crear la tabla principal con fondo de pergamino
        Table mainTable = new Table();
        mainTable.setSize(800, 700); // Ajustar tamaño de la tabla
        mainTable.setPosition((IslandsSurvivors.V_WIDTH - mainTable.getWidth()) / 2,
                (IslandsSurvivors.V_HEIGHT - mainTable.getHeight() - 40) / 2);
        mainTable.setBackground(new TextureRegionDrawable(pergamino));
        mainTable.top();
        mainTable.add(titleLabel).padTop(50).row();
        mainTable.add(scrollPane).size(450, 300).padTop(80).row(); // Expande el ScrollPane para que ocupe el espacio
                                                                   // disponible
        mainTable.add(backButton).size(200, 50).padBottom(20);

        stage.addActor(mainTable);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Limpiar pantalla
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar fondo
        game.batch.begin();
        game.batch.draw(background, 0, 0, IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT);
        game.batch.end();

        // Actualizar y dibujar la UI
        stage.act(delta);
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

    /**
     * Libera los recursos utilizados por la pantalla de créditos.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
