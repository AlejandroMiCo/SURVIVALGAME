package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class VirtualJoystick {
    private Texture baseTexture;
    private Texture knobTexture;
    private Vector2 basePosition;
    private Vector2 knobPosition;
    private float baseRadius;
    private boolean touched;
    private int touchPointer;

    public VirtualJoystick(float baseRadius) {
        this.basePosition = new Vector2();
        this.knobPosition = new Vector2();
        this.baseRadius = baseRadius;
        this.touched = false;
    }

    public void update() {
        if (!touched) {
            for (int i = 0; i < 20; i++) { // Máximo de 20 toques soportados por LibGDX
                if (Gdx.input.isTouched(i)) {
                    touchPointer = i; // Asigna el dedo que controla el joystick
                    touched = true;
                    basePosition.set(Gdx.input.getX(touchPointer),
                            Gdx.graphics.getHeight() - Gdx.input.getY(touchPointer));
                    knobPosition.set(basePosition);
                    break;
                }
            }
        } else if (Gdx.input.isTouched(touchPointer)) {
            // Si el dedo que controla el joystick sigue tocando
            float touchX = Gdx.input.getX(touchPointer);
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(touchPointer);
            Vector2 touchPosition = new Vector2(touchX, touchY);

            if (touchPosition.dst(basePosition) <= baseRadius) {
                knobPosition.set(touchPosition);
            } else {
                Vector2 direction = touchPosition.sub(basePosition).nor();
                knobPosition.set(basePosition.x + direction.x * baseRadius,
                        basePosition.y + direction.y * baseRadius);
            }
        } else {
            touched = false; // Libera el joystick cuando se suelta el dedo
        }
    }

    public Vector2 getDirection() {
        if (touched) {
            return new Vector2(knobPosition.x - basePosition.x, knobPosition.y - basePosition.y).nor();
        }
        return new Vector2(0, 0);
    }

    public boolean isTouched() {
        return touched;
    }

    public boolean isActive() {
        return touched;
    }

    public void dispose() {
        baseTexture.dispose();
        knobTexture.dispose();
    }
}
