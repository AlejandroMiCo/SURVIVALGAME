package io.AlejandroMiCo.IsalandsSurvivors.Screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;

public class LevelUpScreen {
    private Stage stage;
    private boolean isVisible = false;
    private Knight knight;

    private String[] posiblesMejoras = { "vida", "velocidad", "daño", "velocidad_ataque" };
    private float[] valoresMejora = { 20f, 10f, 5f, 0.3f };

    public LevelUpScreen(Knight knight) {
        this.knight = knight;
        stage = new Stage(new ScreenViewport());

        generarOpcionesDeMejora();
    }

    private void generarOpcionesDeMejora() {
        stage.clear(); // Limpiar UI previa

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("Subida de Nivel - Elige una Mejora", new Skin(Gdx.files.internal("uiskin.json")));
        table.add(titleLabel).colspan(3).padBottom(20);
        table.row();

        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(posiblesMejoras.length);
            String mejora = posiblesMejoras[index];
            float cantidad = valoresMejora[index];

            TextButton button = new TextButton("+" + cantidad + " " + mejora,
                    new Skin(Gdx.files.internal("uiskin.json")));
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    knight.mejorarAtributo(mejora, cantidad);
                    hide();
                }
            });
            table.add(button).pad(10);
        }
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
        isVisible = true;
        generarOpcionesDeMejora();
    }

    public void hide() {
        Gdx.input.setInputProcessor(null);
        isVisible = false;
    }

    public void update(float dt) {
        if (isVisible) {
            stage.act(dt);
        }
    }

    public void render() {
        if (isVisible) {
            stage.draw();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }
}
