package io.AlejandroMiCo.IsalandsSurvivors.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Sprites.Knight;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Label countLabel;
    private Label timeLabel;

    private Texture healthBarBg;
    private Texture healthBarFill;
    private float maxHealth;
    private Knight knight;

    private float x, y, width, height;

    public Hud(SpriteBatch sb, Knight knight) {
        this.knight = knight;
        this.maxHealth = knight.getHealth();

        this.x = 20;
        this.y = Gdx.graphics.getHeight() - 40;
        this.width = 200;
        this.height = 20;

        worldTimer = 0;
        timeCount = 0;

        viewport = new FillViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        // Cargar texturas
        healthBarBg = new Texture("ui/health_bar_bg.png");
        healthBarFill = new Texture("ui/health_bar_fill.png");

        countLabel = new Label(String.format("%02d:%02d", worldTimer / 60, worldTimer % 60),
                new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.ROYAL));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.WHITE));

        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(countLabel).expandX();

        stage.addActor(table);
    }

    public void update(float dt) {
        timeCount += dt;
        if (timeCount >= 1) {
            worldTimer++;
            countLabel.setText(String.format("%02d:%02d", worldTimer / 60, worldTimer % 60));
            timeCount = 0;
        }

        float healthPercentage = Math.max(knight.getHealth() / maxHealth, 0);
        width = 200 * healthPercentage;
    }

    public void render(SpriteBatch batch) {
        System.out.println("width: ");
        batch.begin();

        // Dibujar el fondo de la barra de vida
        batch.draw(healthBarBg, x, y, 200, height); // Fondo siempre del mismo tamaño

        // Usamos glScissor para recortar correctamente
        batch.flush();
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        Gdx.gl.glScissor((int) x, (int) y, (int) width, (int) height); // Se recorta según la vida actual
        batch.draw(healthBarFill, x, y, width, height); // La imagen fill siempre tiene el mismo tamaño base
        batch.flush();

        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        batch.end();

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        healthBarBg.dispose();
        healthBarFill.dispose();
    }
}
