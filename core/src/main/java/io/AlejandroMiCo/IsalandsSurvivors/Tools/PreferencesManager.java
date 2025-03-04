package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesManager {
    private static final Preferences prefs = Gdx.app.getPreferences("GamePreferences");

    // Claves de las preferencias
    private static final String MUSIC_VOLUME_KEY = "musicVolume";
    private static final String SOUND_VOLUME_KEY = "soundVolume";
    private static final String VIBRATION_KEY = "vibrationEnabled";
    private static final String LANGUAGE_KEY = "language";

    private static final String HIGH_SCORES = "high_scores";
    private static final int MAX_SCORES = 5;

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

    // Obtener la lista de puntajes más altos
    public static List<Integer> getHighScores() {
        String scoresString = prefs.getString(HIGH_SCORES, "");
        List<Integer> scores = new ArrayList<>();

        if (!scoresString.isEmpty()) {
            for (String s : scoresString.split(",")) {
                scores.add(Integer.parseInt(s));
            }
        }
        return scores;
    }

    // Guardar una nueva puntuación si está entre las mejores
    public static void saveHighScore(int newScore) {
        List<Integer> scores = getHighScores();
        scores.add(newScore);
        Collections.sort(scores, Collections.reverseOrder()); // Ordenar de mayor a menor

        // Mantener solo los mejores 5 o 10 puntajes
        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES);
        }

        // Convertir la lista a una cadena y guardarla
        StringBuilder scoresString = new StringBuilder();
        for (int score : scores) {
            scoresString.append(score).append(",");
        }
        if (scoresString.length() > 0) {
            scoresString.deleteCharAt(scoresString.length() - 1); // Eliminar la última coma
        }

        prefs.putString(HIGH_SCORES, scoresString.toString());
        prefs.flush();
    }
}
