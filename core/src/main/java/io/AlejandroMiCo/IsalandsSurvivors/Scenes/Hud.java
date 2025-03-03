package io.AlejandroMiCo.IsalandsSurvivors.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    private Label hpLabel;
    private Label expLabel;
    private Label coins;
    private Label enemies;

    private ImageButton pauseButton;
    private boolean isPaused;
    private Knight knight;
    private float healthPercentage;
    private float experiencePercentage;

    // **Recursos compartidos para evitar fugas de memoria**
    private static BitmapFont font = new BitmapFont();
    private static Texture healthBgTexture, healthFillTexture, expBgTexture, expFillTexture, heartTexture, expTexture;
    private static Texture pauseTexture, pausePressedTexture;

    private static final Label.LabelStyle defaultLabelStyle = new Label.LabelStyle(font, Color.WHITE);

    private TextureRegion healthBarFillRegion;
    private TextureRegion experienceBarFillRegion;
    private final StringBuilder sb = new StringBuilder();

    static {
        font.setColor(Color.WHITE);

        healthBgTexture = new Texture("ui/health_bar_bg.png");
        healthFillTexture = new Texture("ui/health_bar_fill.png");
        expBgTexture = new Texture("ui/health_bar_bg.png");
        expFillTexture = new Texture("ui/exp_bar_fill.png");
        heartTexture = new Texture("ui/heart.png");
        expTexture = new Texture("ui/experience.png");

        pauseTexture = new Texture("ui/pause_button.png");
        pausePressedTexture = new Texture("ui/pause_button_pressed.png");
    }

    public Hud(SpriteBatch sb, Knight knight) {
        this.knight = knight;
        worldTimer = 0;
        timeCount = 0;
        // isPaused = false;

        viewport = new FillViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        // pauseButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(pauseTexture)),
        //         new TextureRegionDrawable(new TextureRegion(pausePressedTexture)));
        // pauseButton.addListener(event -> {
        //     togglePause();
        //     return true;
        // });

        healthBarFillRegion = new TextureRegion(healthFillTexture, 0, 0, 200, 20);
        experienceBarFillRegion = new TextureRegion(expFillTexture, 0, 0, 200, 20);

        healthBarFillImage = new Image(new TextureRegionDrawable(healthBarFillRegion));
        experienceBarFillImage = new Image(new TextureRegionDrawable(experienceBarFillRegion));

        healthBarBgImage = new Image(healthBgTexture);
        experienceBarBgImage = new Image(expBgTexture);
        experienceBarFillImage = new Image(expFillTexture);
        heartImage = new Image(heartTexture);
        expImage = new Image(expTexture);

        countLabel = new Label("00:00", new Label.LabelStyle(font, Color.ROYAL));
        hpLabel = new Label("100/100", defaultLabelStyle);
        expLabel = new Label("0/100", defaultLabelStyle);
        coins = new Label("Coins: 0000", defaultLabelStyle);
        enemies = new Label("Enemies: 0000", defaultLabelStyle);

        Stack healthStack = new Stack();
        healthStack.add(healthBarBgImage);
        healthStack.add(healthBarFillImage);
        healthStack.add(hpLabel);

        Stack experienceStack = new Stack();
        experienceStack.add(experienceBarBgImage);
        experienceStack.add(experienceBarFillImage);
        experienceStack.add(expLabel);

        Table barsTable = new Table();
        barsTable.add(heartImage).size(30);
        barsTable.add(healthStack).size(200, 20);
        barsTable.row();
        barsTable.add(expImage).size(30);
        barsTable.add(experienceStack).size(200, 20);


        Table countsTable = new Table();
        countsTable.add(coins).pad(5);
        countsTable.row();
        countsTable.add(enemies).pad(5);
        // table.add(pauseButton).padTop(10).padRight(10).expandX().right();
        

        table.add(barsTable).padTop(15).padLeft(5);
        table.add(countLabel).expandX();
        table.add(countsTable).padTop(15).padLeft(5);

        stage.addActor(table);
    }

    // public void togglePause() {
    //     isPaused = !isPaused;
    // }

    // public boolean isPaused() {
    //     return isPaused;
    // }

    public void update(float dt) {
        timeCount += dt;
        if (timeCount >= 1) {
            worldTimer++;
            countLabel.setText(String.format("%02d:%02d", worldTimer / 60, worldTimer % 60));
            timeCount = 0;
        }

        updateUI();

        healthPercentage = Math.max(knight.getCurrentHealth() / knight.getMaxHealth(), 0);
        experiencePercentage = Math.max(knight.getCurrentExperience() / knight.getNextLevelExperience(), 0);

        healthBarFillImage.setSize(200 * healthPercentage, healthBarFillImage.getHeight());
        experienceBarFillImage.setSize(200 * experiencePercentage, experienceBarFillImage.getHeight());
    }

    public void render() {
        stage.draw();
    }

    public void updateUI() {
        sb.setLength(0); // Resetear el StringBuilder
        sb.append("Coins: ").append(knight.getCoins());
        coins.setText(sb.toString());

        sb.setLength(0);
        sb.append("Enemies: ").append(knight.getEnemiesDefeated());
        enemies.setText(sb.toString());

        sb.setLength(0);
        sb.append((int) knight.getCurrentExperience())
                .append("/")
                .append((int) knight.getNextLevelExperience());
        expLabel.setText(sb.toString());

        sb.setLength(0);
        sb.append((int) knight.getCurrentHealth())
                .append("/")
                .append((int) knight.getMaxHealth());
        hpLabel.setText(sb.toString());
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();

        healthBgTexture.dispose();
        healthFillTexture.dispose();
        expBgTexture.dispose();
        expFillTexture.dispose();
        heartTexture.dispose();
        expTexture.dispose();
        pauseTexture.dispose();
        pausePressedTexture.dispose();
    }

    public float getWorldTimer() {
        return worldTimer;
    }
}
