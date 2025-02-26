package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;

public class DamageText {
    private float x, y;
    private int damage;
    private float timeAlive;
    private static final float LIFETIME = 2f; // Tiempo que dura el texto (en segundos)
    private BitmapFont font;
    private Color color;

    public DamageText(float x, float y, int damage) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.timeAlive = 0;
        this.font = new BitmapFont(); // Usa una fuente por defecto
        this.font.getData().setScale(0.05f); // 🔹 Reduce el tamaño de la fuente
        this.color = new Color(1, 0, 0, 1); // Rojo con opacidad completa
    }
    

    public boolean update(float dt) {
        timeAlive += dt;
        y += 1/IslandsSurvivors.PPM; // Hace que el número suba lentamente
        color.a = 1 - (timeAlive / LIFETIME); // Hace que se desvanezca

        return timeAlive >= LIFETIME; // Retorna `true` si ya debe desaparecer
    }

    public void render(SpriteBatch batch) {
        font.setColor(color);
        GlyphLayout layout = new GlyphLayout(font, String.valueOf(damage));
        font.draw(batch, layout, x - layout.width / 2, y);
    }
}
