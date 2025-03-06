package io.AlejandroMiCo.IsalandsSurvivors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import io.AlejandroMiCo.IsalandsSurvivors.Screens.MainMenuScreen;
import io.AlejandroMiCo.IsalandsSurvivors.Tools.Assets;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class IslandsSurvivors extends Game {
    public static final float V_WIDTH = 820; // Ancho de la pantalla
    public static final float V_HEIGHT = 400; // Alto de la pantalla
    public static final float PPM = 100; // PPM de la pantalla
    public static final short DEFAULT_BIT = 1; // Bit de default
    public static final short PLAYER_BIT = 2; // Bit de player
    public static final short ENEMY_BIT = 4; // Bit de enemy
    public static final short BULLET_BIT = 8; // Bit de bullet
    public static final short ITEM_BIT = 16; // Bit de item (Compartido con la experiencia, monedas y carne)
    public static BitmapFont font; // Fuente de la aplicación
    public SpriteBatch batch; // SpriteBatch para renderizar los elementos de la UI

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Cargar fuente de la aplicación
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/EagleLake-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        font = generator.generateFont(parameter);
        
        // Cargar recursos de la aplicación
        Assets.load();

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
