package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;

public class LevelUpScreen {
    private Stage stage;
    private boolean isVisible = false;
    private Knight knight;
    private Texture pergaminoTexture;
    private Texture btnAzulTexture, btnAmarilloTexture, btnRojoTexture;

    Random random = new Random();

    private String[] posiblesMejoras = { "vida", "velocidad", "daño", "velocidad_ataque" };
    private float[] valoresMejora = { 20f, 10f, 5f, 0.3f };

    public LevelUpScreen(Knight knight) {
        this.knight = knight;
        stage = new Stage(new ScreenViewport());

        generarOpcionesDeMejora();
    }

    private void generarOpcionesDeMejora() {
        stage.clear(); // Limpiar UI previa

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cargar imágenes
        pergaminoTexture = new Texture(Gdx.files.internal("ui/pergamino.png"));
        btnAzulTexture = new Texture(Gdx.files.internal("ui/boton_azul.png"));
        btnAmarilloTexture = new Texture(Gdx.files.internal("ui/boton_amarillo.png"));
        btnRojoTexture = new Texture(Gdx.files.internal("ui/boton_rojo.png"));

        // Agregar fondo del pergamino
        Image pergamino = new Image(new TextureRegionDrawable(pergaminoTexture));
        pergamino.setSize(650, 600);
        pergamino.setPosition(Gdx.graphics.getWidth() / 2f - pergamino.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - pergamino.getHeight() / 2f);
        stage.addActor(pergamino);

        // Crear botones con imágenes como fondo
        Skin skin = new Skin();
        skin.add("default-font", new BitmapFont());

        TextButton.TextButtonStyle azulStyle = new TextButton.TextButtonStyle();
        azulStyle.up = new TextureRegionDrawable(btnAzulTexture);
        azulStyle.font = skin.getFont("default-font");
        azulStyle.fontColor = Color.BLACK;

        TextButton.TextButtonStyle amarilloStyle = new TextButton.TextButtonStyle();
        amarilloStyle.up = new TextureRegionDrawable(btnAmarilloTexture);
        amarilloStyle.font = skin.getFont("default-font");
        amarilloStyle.fontColor = Color.BLACK;

        TextButton.TextButtonStyle rojoStyle = new TextButton.TextButtonStyle();
        rojoStyle.up = new TextureRegionDrawable(btnRojoTexture);
        rojoStyle.font = skin.getFont("default-font");
        rojoStyle.fontColor = Color.BLACK;

        // Crear botones
        TextButton[] buttons = new TextButton[3];
        TextButton btnAzul = new TextButton("Aumentar Fuerza", azulStyle);
        TextButton btnAmarillo = new TextButton("Mejorar Defensa", amarilloStyle);
        TextButton btnRojo = new TextButton("Subir Vida", rojoStyle);

        buttons[0] = btnAzul;
        buttons[1] = btnAmarillo;
        buttons[2] = btnRojo;

        // Dar tamaño a los botones
        btnAzul.setSize(300, 75);
        btnAmarillo.setSize(300, 75);
        btnRojo.setSize(300, 75);

        // Posicionar botones dentro del pergamino
        float centerX = Gdx.graphics.getWidth() / 2f - btnAzul.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        btnAzul.setPosition(centerX, centerY + 25);
        btnAmarillo.setPosition(centerX, centerY - 50);
        btnRojo.setPosition(centerX, centerY - 125);

        for (TextButton button : buttons) {

            int index = random.nextInt(posiblesMejoras.length);
            String mejora = posiblesMejoras[index];
            float cantidad = valoresMejora[index];

            button.setText("+" + cantidad + " " + mejora);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    knight.mejorarAtributo(mejora, cantidad);
                    hide();
                }
            });

            // Agregar botones a la escena
            stage.addActor(btnAzul);
            stage.addActor(btnAmarillo);
            stage.addActor(btnRojo);
        }
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
        isVisible = true;
        generarOpcionesDeMejora();
    }

    public void hide() {
        Gdx.input.setInputProcessor(null);
        isVisible = false;
    }

    public void update(float dt) {
        if (isVisible) {
            stage.act(dt);
        }
    }

    public void render() {
        if (isVisible) {
            stage.draw();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }
}
