package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesManager {
    private static final Preferences prefs = Gdx.app.getPreferences("GamePreferences");

    // Claves de las preferencias
    private static final String MUSIC_VOLUME_KEY = "musicVolume";
    private static final String SOUND_VOLUME_KEY = "soundVolume";
    private static final String VIBRATION_KEY = "vibrationEnabled";
    private static final String LANGUAGE_KEY = "language";

    // Métodos para obtener las preferencias
    public static float getMusicVolume() {
        return prefs.getFloat(MUSIC_VOLUME_KEY, 0.5f); // Valor por defecto 0.5
    }

    public static float getSoundVolume() {
        return prefs.getFloat(SOUND_VOLUME_KEY, 0.5f); // Valor por defecto 0.5
    }

    public static boolean isVibrationEnabled() {
        return prefs.getBoolean(VIBRATION_KEY, true); // Valor por defecto true
    }

    public static String getLanguage() {
        return prefs.getString(LANGUAGE_KEY, "ES"); // Valor por defecto "ES"
    }

    // Métodos para guardar las preferencias
    public static void setMusicVolume(float volume) {
        prefs.putFloat(MUSIC_VOLUME_KEY, volume);
        prefs.flush(); // Guardar los cambios
    }

    public static void setSoundVolume(float volume) {
        prefs.putFloat(SOUND_VOLUME_KEY, volume);
        prefs.flush(); // Guardar los cambios
    }

    public static void setVibrationEnabled(boolean enabled) {
        prefs.putBoolean(VIBRATION_KEY, enabled);
        prefs.flush(); // Guardar los cambios
    }

    public static void setLanguage(String language) {
        prefs.putString(LANGUAGE_KEY, language);
        prefs.flush(); // Guardar los cambios
    }
}

