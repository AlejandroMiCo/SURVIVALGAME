package io.AlejandroMiCo.SurvivalGame;

import javax.swing.text.Position;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;


@SuppressWarnings("rawtypes")

public class Panda extends Sprite{
    public double x, y;
    private Animation animacion;
    private float tiempo;
    private TextureRegion [] regionsMovimiento;
    private Texture imagen;
    private TextureRegion frameActual;
    private Vector2 v2;

    public Panda(int x, int y) {
        this.x = x;
        this.y = y;
        v2 = new Vector2();
        //cargar la imnagen
        imagen = new Texture("Panda.png");
        TextureRegion[][] tmp = TextureRegion.split(imagen, imagen.getWidth()/8, imagen.getHeight()/7);
    

        regionsMovimiento = new TextureRegion[8];

        //Movimiento hacia abajo
        for (int i = 0; i < 8; i++) {
            regionsMovimiento[i] = tmp[2][i];
        }

        animacion = new Animation(0.1f, regionsMovimiento);
        tiempo = 0f;

    }

    public void render(final SpriteBatch batch){
        tiempo += Gdx.graphics.getDeltaTime();  //Tiempo que pasa desde el ultimo render
        frameActual = (TextureRegion) animacion.getKeyFrame(tiempo, true);
        // movimiento();
        batch.draw(frameActual, (int)x, (int)y);
    }

    // public void movimiento(){
        
    //     if (Gdx.input.isKeyPressed(Keys.W)) {
    //         v2.set(v2.x, 1);
    //     }else if (Gdx.input.isKeyPressed(Keys.S)) {
    //         v2.set(v2.x, -1);
            
    //     }else if (Gdx.input.isKeyPressed(Keys.D)) {
    //         v2.set(1, v2.y);
            
    //     }else if (Gdx.input.isKeyPressed(Keys.A)) {
    //         v2.set(-1, v2.y);    
    //     }
    // }
}
