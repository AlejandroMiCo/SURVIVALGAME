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
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Player;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.Assets;

/**
 * Pantalla de subida de nivel donde el jugador puede elegir una mejora.
 * Se presentan tres opciones de mejora al jugador, las cuales se eligen
 * aleatoriamente
 * de una lista predefinida. Al seleccionar una, se aplica al personaje y se
 * cierra la pantalla.
 */
public class LevelUpScreen {
    private Stage stage;
    private boolean isVisible = false;
    private Player knight;
    private Texture pergaminoTexture;
    private Texture btnAzulTexture, btnAmarilloTexture, btnRojoTexture;

    Random random = new Random();

    // Mapa con los valores de las mejoras disponibles
    private HashMap<String, Float> mejorasCaballero = new HashMap<>();

    // Lista de mejoras posibles para el jugador
    private String[] posiblesMejoras = { "player_max_health", "player_speed", "player_damage", "player_critical_chance",
            "player_health_regeneration", "player_absorption_radius",
            "bullet_speed", "bullet_cooldown" };

    float escala = Gdx.graphics.getWidth() / IslandsSurvivors.V_WIDTH;

    public Hud hud;

    /**
     * Constructor de la pantalla de subida de nivel.
     * 
     * @param player Referencia al personaje principal.
     * @param hud    Referencia al HUD para restaurar el control de entrada al
     *               cerrarse la pantalla.
     */
    public LevelUpScreen(Player player, Hud hud) {
        this.knight = player;
        this.hud = hud;
        stage = new Stage(new ScreenViewport());

        cargarMejoras();
        generarOpcionesDeMejora();
    }

    /**
     * Carga las mejoras con sus valores base.
     */
    private void cargarMejoras() {
        mejorasCaballero.put("player_max_health", 20f);
        mejorasCaballero.put("player_speed", 5f);
        mejorasCaballero.put("player_damage", 10f);
        mejorasCaballero.put("player_critical_chance", 5f);
        mejorasCaballero.put("player_health_regeneration", 1f);
        mejorasCaballero.put("player_absorption_radius", 0.25f);
        mejorasCaballero.put("bullet_speed", 0.5f);
        mejorasCaballero.put("bullet_cooldown", -0.125f);
    }

    /**
     * Genera tres opciones de mejora aleatorias y las muestra en la pantalla.
     */
    private void generarOpcionesDeMejora() {
        stage.clear(); // Limpiar UI previa

        Gdx.input.setInputProcessor(stage);

        // Cargar textutas
        pergaminoTexture = Assets.manager.get("ui/pergamino.png", Texture.class);
        btnAzulTexture = Assets.manager.get("ui/boton_azul.png", Texture.class);
        btnAmarilloTexture = Assets.manager.get("ui/boton_amarillo.png", Texture.class);
        btnRojoTexture = Assets.manager.get("ui/boton_rojo.png", Texture.class);

        // Agregar fondo del pergamino
        Image pergamino = new Image(new TextureRegionDrawable(pergaminoTexture));
        pergamino.setSize(650 * escala, 550 * escala);
        pergamino.setPosition(Gdx.graphics.getWidth() / 2f - pergamino.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - pergamino.getHeight() / 2f);
        stage.addActor(pergamino);

        // Crear botones con imágenes como fondo
        Skin skin = new Skin();
        skin.add("default-font", IslandsSurvivors.font);
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
        TextButton btnAzul = new TextButton("", azulStyle);
        TextButton btnAmarillo = new TextButton("", amarilloStyle);
        TextButton btnRojo = new TextButton("", rojoStyle);

        buttons[0] = btnAzul;
        buttons[1] = btnAmarillo;
        buttons[2] = btnRojo;

        /// Configurar tamaños de los botones
        for (TextButton button : buttons) {
            button.setSize(300 * escala, 75 * escala);
        }

        // Seleccionar tres mejoras aleatorias
        List<String> mejorasSeleccionadas = new ArrayList<>();
        Collections.addAll(mejorasSeleccionadas, posiblesMejoras);
        Collections.shuffle(mejorasSeleccionadas);

        for (int i = 0; i < buttons.length; i++) {
            String mejora = mejorasSeleccionadas.get(i);
            float cantidad = obtenerValorMejora(mejora);
            TextButton button = buttons[i];

            // Cambiar el texto del botón para usar el texto de los recursos
            String nombreMejora = Assets.getText("game." + mejora); // Obtiene el nombre localizado
            button.setText("+" + cantidad + " " + nombreMejora); // Accede a la mejora con el nombre localizable
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

    /**
     * Obtiene el valor de mejora correspondiente a una clave.
     */
    private float obtenerValorMejora(String mejora) {
        // Devuelve el valor de la mejora dependiendo de su tipo
        if (mejorasCaballero.containsKey(mejora)) {
            return mejorasCaballero.get(mejora);
        }
        return 0f; // Valor por defecto
    }

    /**
     * Aplica la mejora al personaje o a las balas según corresponda.
     */
    private void aplicarMejora(String mejora, float cantidad) {
        if (mejora.split("_")[0].equals("player")) {
            knight.mejorarAtributo(mejora, cantidad);
        } else {
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
