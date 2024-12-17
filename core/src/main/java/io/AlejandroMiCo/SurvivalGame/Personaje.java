package io.AlejandroMiCo.SurvivalGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

@SuppressWarnings("rawtypes")
public class Personaje {

    public double x, y;
    private Animation animacion;
    private float tiempo;
    private TextureRegion [] regionsMovimiento;
    private Texture imagen;
    private TextureRegion frameActual;

    public Personaje(int x, int y) {
        this.x = x;
        this.y = y;

        //cargar la imnagen
        imagen = new Texture("pj.png");
        TextureRegion[][] tmp = TextureRegion.split(imagen, imagen.getWidth()/8, imagen.getHeight()/8);

        regionsMovimiento = new TextureRegion[6];

        //Movimiento hacia abajo
        for (int i = 0; i < 6; i++) {
            regionsMovimiento[i] = tmp[4][i];
        }

        animacion = new Animation(0.175f, regionsMovimiento);
        tiempo = 0f;

    }

    public void render(final SpriteBatch batch){
        tiempo += Gdx.graphics.getDeltaTime();  //Tiempo que pasa desde el ultimo render
        frameActual = (TextureRegion) animacion.getKeyFrame(tiempo, true);
        batch.draw(frameActual, (int)x, (int)y);
        y -= 0.175;
    }
}
