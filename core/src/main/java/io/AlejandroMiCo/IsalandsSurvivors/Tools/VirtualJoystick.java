package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Clase que implementa un joystick virtual.
 */
public class VirtualJoystick {
    private final Vector2 basePosition;
    private final Vector2 knobPosition;
    private final Vector2 touchPosition;
    private final Vector2 direction;
    private final float baseRadius;
    private boolean touched;
    private int touchPointer;

    /**
     * Constructor de la clase
     *
     * @param baseRadius Radio de la base del joystick.
     */
    public VirtualJoystick(float baseRadius) {
        this.basePosition = new Vector2();
        this.knobPosition = new Vector2();
        this.touchPosition = new Vector2();
        this.direction = new Vector2();
        this.baseRadius = baseRadius;
        this.touched = false;
    }

    /**
     * Método para actualizar la posición del joystick.
     */
    public void update() {
        if (!touched) {
            for (int i = 0; i < 20; i++) { // Máximo de 20 toques soportados por LibGDX
                if (Gdx.input.isTouched(i)) {
                    touchPointer = i; // Asigna el dedo que controla el joystick
                    touched = true;
                    basePosition.set(Gdx.input.getX(touchPointer),
                            Gdx.graphics.getHeight() - Gdx.input.getY(touchPointer));
                    knobPosition.set(basePosition);
                    return;
                }
            }
        } else if (Gdx.input.isTouched(touchPointer)) {
            // Si el dedo que controla el joystick sigue tocando
            touchPosition.set(Gdx.input.getX(touchPointer), Gdx.graphics.getHeight() - Gdx.input.getY(touchPointer));

            // Si el dedo está dentro del radio de la base del joystick
            if (touchPosition.dst(basePosition) <= baseRadius * baseRadius) {
                knobPosition.set(touchPosition);
            } else {
                direction.set(touchPosition).sub(basePosition).nor();
                knobPosition.set(basePosition).mulAdd(direction, baseRadius);
            }
        } else {
            touched = false; // Libera el joystick cuando se suelta el dedo
        }
    }

    /**
     * Obtiene la dirección del joystick.
     * 
     * @return La dirección del joystick.
     */
    public Vector2 getDirection() {
        return touched ? direction.set(knobPosition).sub(basePosition).nor() : direction.set(0, 0);
    }

    public boolean isTouched() {
        return touched;
    }

    public boolean isActive() {
        return touched;
    }
}
