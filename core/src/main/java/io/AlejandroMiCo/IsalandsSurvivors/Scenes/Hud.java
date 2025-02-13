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
import com.badlogic.gdx.utils.viewport.FitViewport;
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
        private Texture experienceBarBg;
        private Texture experienceBarFill;
        private Texture heart;
        private float maxHealth;
        private float maxExperience;
        private Knight knight;

        private float x, y, widthHp, widthEx, height;

        public Hud(SpriteBatch sb, Knight knight) {
            this.knight = knight;
            this.maxHealth = knight.getHealth();
            this.maxExperience = knight.getNextLevelExperience();

            this.x = 30;
            this.y = Gdx.graphics.getHeight() - 40;
            this.widthHp = 200;
            this.widthEx = 200;
            this.height = 20;

            worldTimer = 0;
            timeCount = 0;

            viewport = new FitViewport(IslandsSurvivors.V_WIDTH, IslandsSurvivors.V_HEIGHT, new OrthographicCamera());
            stage = new Stage(viewport, sb);

            Table table = new Table();
            table.top();
            table.setFillParent(true);

            // Cargar texturas
            healthBarBg = new Texture("ui/health_bar_bg.png");
            healthBarFill = new Texture("ui/health_bar_fill.png");
            experienceBarBg = new Texture("ui/health_bar_bg.png");
            experienceBarFill = new Texture("ui/exp_bar_fill.png");
            heart = new Texture("ui/heart.png");

            countLabel = new Label(String.format("%02d:%02d", worldTimer / 60, worldTimer % 60),
                    new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.ROYAL));
            timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.WHITE));

            table.add(timeLabel).expandX().padTop(10);
            table.row();
            table.add(countLabel).expandX();

            stage.setDebugAll(true);
            stage.addActor(table);
        }

        public void update(float dt) {
            timeCount += dt;
            if (timeCount >= 1) {
                worldTimer++;
                countLabel.setText(String.format("%02d:%02d", worldTimer / 60, worldTimer % 60));
                timeCount = 0;
            }

            maxExperience = knight.getNextLevelExperience();

            float healthPercentage = Math.max(knight.getHealth() / maxHealth, 0);
            widthHp = 200 * healthPercentage;
            float experiencePercentage = Math.max(knight.getExperience() / maxExperience, 0);
            widthEx = 200 * experiencePercentage;
        }

        public void render(SpriteBatch batch) {
            System.out.println("width: ");
            batch.begin();

            // Dibujar el fondo de la barra de vida
            batch.draw(healthBarBg, x, y, 200, height); // Fondo siempre del mismo tamaño
            batch.draw(experienceBarBg, x, y - 30, 200, height); // Fondo siempre del mismo tamaño
            // Usamos glScissor para recortar correctamente
            batch.flush();
            Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

            Gdx.gl.glScissor((int) x+1, (int) y, (int) widthHp, (int) height); // Se recorta según la vida actual
            batch.draw(healthBarFill, x, y, Math.min(200, widthHp), height); // La imagen fill siempre tiene el mismo tamaño base
            batch.flush();

            Gdx.gl.glScissor((int) x+1, (int) y - 30, (int) widthEx, (int) height); // Se recorta según la vida actual
            batch.draw(experienceBarFill, x, y - 30, widthEx, height); // La imagen fill siempre tiene el mismo tamaño base
            batch.flush();

            Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

            batch.draw(heart, 15, y + height - 25, 30, 30);
            batch.end();

            stage.draw();
        }

        @Override
        public void dispose() {
            stage.dispose();
            healthBarBg.dispose();
            healthBarFill.dispose();
            experienceBarBg.dispose();
            experienceBarFill.dispose();
        }
    }
