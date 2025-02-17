package io.AlejandroMiCo.IsalandsSurvivors.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

    private Image healthBarBgImage;
    private Image healthBarFillImage;
    private Image experienceBarBgImage;
    private Image experienceBarFillImage;
    private Image heartImage;
    private Image expImage;
    private Knight knight;

    private Label hpLabel;
    private Label expLabel;
    private Label coins;
    private Label enemies;

    private Table table;

    private ImageButton pauseButton;
    private boolean isPaused;

    public Hud(SpriteBatch sb, Knight knight) {
        this.knight = knight;

        worldTimer = 0;
        timeCount = 0;

        isPaused = false;

        viewport = new FillViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        table = new Table();
        table.top();
        table.setFillParent(true);

        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(
                new TextureRegion(new Texture("ui/pause_button.png")));
        TextureRegionDrawable pausePressedDrawable = new TextureRegionDrawable(
                new TextureRegion(new Texture("ui/pause_button_pressed.png")));
        pauseButton = new ImageButton(pauseDrawable, pausePressedDrawable);
        pauseButton.setTouchable(Touchable.enabled);
        pauseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("patata");
                togglePause();
                return true; // Indica que el evento ha sido manejado
            }
        });

        // Cargar texturas y crear imágenes
        healthBarBgImage = new Image(new Texture("ui/health_bar_bg.png"));
        healthBarFillImage = new Image(new Texture("ui/health_bar_fill.png"));
        experienceBarBgImage = new Image(new Texture("ui/health_bar_bg.png"));
        experienceBarFillImage = new Image(new Texture("ui/exp_bar_fill.png"));
        heartImage = new Image(new Texture("ui/heart.png"));
        expImage = new Image(new Texture("ui/exp.png"));

        countLabel = new Label(String.format("%02d:%02d", worldTimer / 60, worldTimer % 60),
                new Label.LabelStyle(new BitmapFont(), Color.ROYAL));
        hpLabel = new Label(String.format("%3.0f/%3.0f", knight.getCurrentHealth(), knight.getMaxHealth()),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        expLabel = new Label(
                String.format("%3.0f/%3.0f", knight.getCurrentExperience(), knight.getNextLevelExperience()),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        coins = new Label(String.format("Coins: %4d", knight.getCoins()),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        enemies = new Label(String.format("Enemies: %6d", knight.getCoins()),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        hpLabel.setAlignment(1);
        expLabel.setAlignment(1);

        // Crear Stack para la barra de vida
        Stack healthStack = new Stack();
        Table barsTable = new Table();
        // Table healthTable = new Table();
        barsTable.add(heartImage).size(30).padRight(0);
        healthStack.add(healthBarBgImage);
        healthStack.add(healthBarFillImage);
        healthStack.add(hpLabel);
        barsTable.add(healthStack).size(200, 20);
        barsTable.row();

        // Crear Stack para la barra de experiencia
        Stack experienceStack = new Stack();
        Table expTable = new Table();

        barsTable.add(expImage).size(30).padLeft(0);
        experienceStack.add(experienceBarBgImage);
        experienceStack.add(experienceBarFillImage);
        experienceStack.add(expLabel);
        barsTable.add(experienceStack).size(200, 20).right();

        barsTable.add(expTable).expandX().padBottom(5).left();

        // Agregar a la tabla
        table.row();
        table.add(barsTable).padTop(15).padLeft(5);
        table.add(countLabel).expandX().padBottom(10).padLeft(10);
        table.add(coins).pad(5);
        table.add(enemies).pad(5);

        // Agregar el botón a la tabla
        table.add(pauseButton).padTop(10).padRight(10).expandX().right();

        pauseButton.toFront();

        table.debug();
        stage.addActor(table);
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void update(float dt) {
        timeCount += dt;
        if (timeCount >= 1) {
            worldTimer++;
            countLabel.setText(String.format("%02d:%02d", worldTimer / 60, worldTimer % 60));
            timeCount = 0;
        }

        coins.setText(String.format("Coins: %4d", knight.getCoins()));
        enemies.setText(String.format("Enemies: %6d", knight.getEnemiesDefeated()));

        expLabel.setText(String.format("%3.0f/%3.0f", knight.getCurrentExperience(), knight.getNextLevelExperience()));
        hpLabel.setText(String.format("%3.0f/%3.0f", knight.getCurrentHealth(), knight.getMaxHealth()));

        float healthPercentage = Math.max(knight.getCurrentHealth() / knight.getMaxHealth(), 0);
        float experiencePercentage = Math.max(knight.getCurrentExperience() / knight.getNextLevelExperience(), 0);

        // Actualizar el tamaño de las barras de relleno
        healthBarFillImage.setSize(200 * healthPercentage, 20);
        experienceBarFillImage.setSize(200 * experiencePercentage, 20);
    }

    public void render() {
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Integer getWorldTimer() {
        return worldTimer;
    }

    public void setWorldTimer(Integer worldTimer) {
        this.worldTimer = worldTimer;
    }
}
