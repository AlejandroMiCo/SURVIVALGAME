package io.AlejandroMiCo.IsalandsSurvivors.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;
import io.AlejandroMiCo.IsalandsSurvivors.Screens.PlayScreen;

/**
 * Clase que se encarga de crear los objetos del mundo físico de Box2D.
 */
public class B2WorldCreator {

    /**
     * Constructor de la clase B2WorldCreator.
     * 
     * @param screen Pantalla de juego.
     */
    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();

        // Crear los objetos del mundo físico
        TiledMap map = screen.getMap();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // Crear el cuerpo de los objetos de la capa de colisiones del mapa
        for (MapObject object : map.getLayers().get("CollisionLayer").getObjects()
                .getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / IslandsSurvivors.PPM,
                    (rect.getY() + rect.getHeight() / 2) / IslandsSurvivors.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / IslandsSurvivors.PPM, rect.getHeight() / 2 / IslandsSurvivors.PPM);
            fdef.filter.categoryBits = IslandsSurvivors.DEFAULT_BIT;
            fdef.filter.maskBits = IslandsSurvivors.BULLET_BIT | IslandsSurvivors.PLAYER_BIT
                    | IslandsSurvivors.ENEMY_BIT;
            fdef.shape = shape;
            body.createFixture(fdef);
        }
    }
}
