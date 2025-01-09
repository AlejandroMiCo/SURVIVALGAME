package io.AlejandroMiCo.SurvivalGame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;

public class Personaje {
    private Animation<TextureRegion> animacion;
    private float tiempo;
    private TextureRegion[] regionsMovimiento;
    private Texture imagen;
    private TextureRegion frameActual;
    private Vector2 posicion;
    private Vector2 velocidad;
    private VirtualJoystick joystick;

    public Personaje(VirtualJoystick joystick) {
        this.joystick = joystick;
        posicion = new Vector2(100, 100);
        velocidad = new Vector2(0, 0);
        imagen = new Texture("img/pj.png");
        TextureRegion[][] tmp = TextureRegion.split(imagen, imagen.getWidth() / 8, imagen.getHeight() / 8);

        regionsMovimiento = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[4][i];
        }

        animacion = new Animation<>(0.1f, regionsMovimiento);
        tiempo = 0f;
    }

    public void update(float delta) {
        tiempo += delta;

        Vector2 direction = joystick.getDirection();
        velocidad.set(direction.scl(200 * delta)); // Ajusta la velocidad según sea necesario

        posicion.add(velocidad);
    }

    public void render(SpriteBatch batch) {
        frameActual = animacion.getKeyFrame(tiempo, true);
        batch.draw(frameActual, posicion.x, posicion.y, Gdx.graphics.getWidth() * 0.175f, Gdx.graphics.getHeight() * 0.2f);
    }

    public void dispose() {
        imagen.dispose();
    }
}
