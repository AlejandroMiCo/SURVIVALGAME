package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Combat.Bullet;
import io.AlejandroMiCo.IsalandsSurvivors.Scenes.Hud;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;

public class LevelUpScreen {
    private Stage stage;
    private boolean isVisible = false;
    private Knight knight;
    private Texture pergaminoTexture;
    private Texture btnAzulTexture, btnAmarilloTexture, btnRojoTexture;

    Random random = new Random();

    // private String[] posiblesMejoras = { "vida", "velocidad", "daño",
    // "velocidad_ataque" };
    // private float[] valoresMejora = { 20f, 10f, 5f, 0.3f };

    private HashMap<String, Float> mejorasCaballero = new HashMap<>();
    private HashMap<String, Float> mejorasBala = new HashMap<>();

    private String[] posiblesMejoras = { "player_max_health", "player_speed", "player_damage", "player_critical_chance",
            "player_health_regenarition", "player_absorption_radius", "daño_bala",
            "velocidad_bala", "cooldown_bala", "critico_bala" };

    float escala = Gdx.graphics.getWidth() / IslandsSurvivors.V_WIDTH;

    public Hud hud;

    public LevelUpScreen(Knight knight, Hud hud) {
        this.knight = knight;
        this.hud = hud;
        stage = new Stage(new ScreenViewport());

        cargarMejoras();
        generarOpcionesDeMejora();
    }

    private void cargarMejoras() {
        mejorasCaballero.put("player_max_health", 20f);
        mejorasCaballero.put("player_speed", 10f);
        mejorasCaballero.put("player_damage", 5f);
        mejorasCaballero.put("player_critical_chance", 5f);
        mejorasCaballero.put("player_health_regenarition", 1f);
        mejorasCaballero.put("player_absorption_radius", 1f);

        mejorasBala.put("daño_bala", 5f);
        mejorasBala.put("velocidad_bala", 1f);
        mejorasBala.put("cooldown_bala", -0.5f);
        mejorasBala.put("critico_bala", 5f);
    }

    private void generarOpcionesDeMejora() {
        stage.clear(); // Limpiar UI previa

        Gdx.input.setInputProcessor(stage);

        // Cargar imágenes
        pergaminoTexture = new Texture(Gdx.files.internal("ui/pergamino.png"));
        btnAzulTexture = new Texture(Gdx.files.internal("ui/boton_azul.png"));
        btnAmarilloTexture = new Texture(Gdx.files.internal("ui/boton_amarillo.png"));
        btnRojoTexture = new Texture(Gdx.files.internal("ui/boton_rojo.png"));

        // Agregar fondo del pergamino
        Image pergamino = new Image(new TextureRegionDrawable(pergaminoTexture));
        pergamino.setSize(650 * escala, 550 * escala);
        pergamino.setPosition(Gdx.graphics.getWidth() / 2f - pergamino.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - pergamino.getHeight() / 2f);
        stage.addActor(pergamino);

        // Crear botones con imágenes como fondo
        Skin skin = new Skin();
        skin.add("default-font", IslandsSurvivors.font);
        // skin.getFont("default-font").getData().setScale(1f); // Restablecer la escala
        // a la original
        skin.getFont("default-font").getData().setScale(escala);

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
        btnAzul.setSize(300 * escala, 75 * escala);
        btnAmarillo.setSize(300 * escala, 75 * escala);
        btnRojo.setSize(300 * escala, 75 * escala);

        List<String> mejorasSeleccionadas = new ArrayList<>();
        Collections.addAll(mejorasSeleccionadas, posiblesMejoras);
        Collections.shuffle(mejorasSeleccionadas);

        for (int i = 0; i < buttons.length; i++) {

            String mejora = mejorasSeleccionadas.get(i);
            float cantidad = obtenerValorMejora(mejora);
            TextButton button = buttons[i];

            button.setText("+" + cantidad + " " + mejora);
            button.padBottom(15 * escala);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    aplicarMejora(mejora, cantidad);
                    hide();
                }
            });

            // Agregar botones a la escena
            stage.addActor(button);
        }
        // Posicionar botones dentro del pergamino
        float centerX = Gdx.graphics.getWidth() / 2f - btnAzul.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        btnAzul.setPosition(centerX, centerY + (25 * escala));
        btnAmarillo.setPosition(centerX, centerY - (50 * escala));
        btnRojo.setPosition(centerX, centerY - (125 * escala));
    }

    private float obtenerValorMejora(String mejora) {
        // Devuelve el valor de la mejora dependiendo de su tipo
        if (mejorasCaballero.containsKey(mejora)) {
            return mejorasCaballero.get(mejora);
        } else if (mejorasBala.containsKey(mejora)) {
            return mejorasBala.get(mejora);
        }
        return 0f; // Valor por defecto
    }

    private void aplicarMejora(String mejora, float cantidad) {
        if (mejorasCaballero.containsKey(mejora)) {
            knight.mejorarAtributo(mejora, cantidad);
        } else if (mejorasBala.containsKey(mejora)) {
            Bullet.mejorarAtributo(mejora, cantidad);
        }
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
        isVisible = true;
        generarOpcionesDeMejora();
    }

    public void hide() {
        if (hud != null) {
            Gdx.input.setInputProcessor(hud.stage);
        }
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
