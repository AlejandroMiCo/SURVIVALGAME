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
        if (Gdx.input.isTouched()) {
            if (!touched) {
                for (int i = 0; i < 20; i++) { // 20 es el número máximo de toques simultáneos que libGDX soporta
                    if (Gdx.input.isTouched(i)) {
                        touchPointer = i;
                        touched = true;
                        basePosition.set(Gdx.input.getX(touchPointer),
                                Gdx.graphics.getHeight() - Gdx.input.getY(touchPointer));
                        knobPosition.set(basePosition);
                        break;
                    }
                }
            }
            if (touched) {

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
            }
        } else {
            touched = false;
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