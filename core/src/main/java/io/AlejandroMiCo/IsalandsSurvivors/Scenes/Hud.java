package io.AlejandroMiCo.IsalandsSurvivors.Scenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    Label countLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label islandsSurvivorsLabel;

    public Hud(SpriteBatch sb) {
        worldTimer = 0;
        timeCount = 0;
        score = 0;

        viewport = new FillViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countLabel = new Label(String.format("%02d:%02d", worldTimer / 60, worldTimer % 60),
                new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.ROYAL));
        // scoreLabel = new Label(String.format("%06d", score),
        // new Label.LabelStyle(new BitmapFont(),
        // com.badlogic.gdx.graphics.Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.WHITE));
        // levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(),
        // com.badlogic.gdx.graphics.Color.WHITE));
        // worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(),
        // com.badlogic.gdx.graphics.Color.WHITE));
        // islandsSurvivorsLabel = new Label("KNIGHT",
        // new Label.LabelStyle(new BitmapFont(),
        // com.badlogic.gdx.graphics.Color.WHITE));

        // table.add(islandsSurvivorsLabel).expandX().padTop(10);
        // table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        // table.add(scoreLabel).expandX();
        // table.add(levelLabel).expandX();
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
    }

    public void addScore(int value) {
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (timeCount >= 1) {
            worldTimer++;
        }
    }
}
