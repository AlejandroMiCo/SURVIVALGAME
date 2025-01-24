package io.AlejandroMiCo.IsalandsSurvivors.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.AlejandroMiCo.IsalandsSurvivors.IslandsSurvivors;

public class Knight extends Sprite {

    public World world;
    public Body b2body;

    public Knight(World world) {
        this.world = world;
        defineKnight();
    }

    public void defineKnight() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / IslandsSurvivors.PPM, 32 / IslandsSurvivors.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        FixtureDef fedef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / IslandsSurvivors.PPM);

        fedef.shape = shape;
        b2body.createFixture(fedef);
    }
}
