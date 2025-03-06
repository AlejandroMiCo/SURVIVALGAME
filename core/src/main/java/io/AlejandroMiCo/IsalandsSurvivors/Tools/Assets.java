package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.I18NBundle;

/**
 * Clase que contiene los recursos de la aplicación.
 */
public class Assets {
    public static final AssetManager manager = new AssetManager();
    public static I18NBundle bundle; // Para los textos en distintos idiomas

    /**
     * Método auxiliar para cargar recursos.
     */
    public static void load() {

        // Cargar imágenes
        manager.load("img/map.png", Texture.class);
        manager.load("ui/title2.png", Texture.class);
        manager.load("ui/boton_azul.png", Texture.class);
        manager.load("ui/boton_azul_press.png", Texture.class);
        manager.load("ui/boton_rojo.png", Texture.class);
        manager.load("ui/boton_rojo_press.png", Texture.class);
        manager.load("ui/boton_amarillo.png", Texture.class);
        manager.load("ui/pergamino.png", Texture.class);
        manager.load("ui/slider.png", Texture.class);
        manager.load("ui/sliderKnob.png", Texture.class);
        manager.load("img/Dead_custom.png", Texture.class); // Carga la textura de muerte

        // Cargar sonidos y música
        manager.load("music/menuSong.ogg", Music.class);
        manager.load("music/song.ogg", Music.class);
        manager.load("sounds/attack.ogg", Sound.class);
        manager.load("sounds/pupa.ogg", Sound.class);

        // Cargar localización
        loadLanguage("en");
        loadLanguage("es");

        manager.finishLoading();
    }

    /**
     * Método auxiliar para obtener el texto de un recurso.
     * 
     * @param key Clave del recurso.
     * @return El texto del recurso.
     */
    public static String getText(String key) {
        return bundle != null ? bundle.get(key) : "???" + key + "???";
    }

    public static void dispose() {
        manager.dispose();
    }

    /**
     * Método auxiliar para cargar el idioma seleccionado.
     * 
     * @param langCode Código del idioma.
     */
    @SuppressWarnings("deprecation")
    public static void loadLanguage(String langCode) {
        FileHandle baseFileHandle = Gdx.files.internal("i18n/messages");
        Locale locale = new Locale(langCode);
        bundle = I18NBundle.createBundle(baseFileHandle, locale);
    }
}
