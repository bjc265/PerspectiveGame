package level;

import java.awt.Color;
import java.util.List;

import game.ObjectBody;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.Light;

public class DefaultLevel extends Level {

	

	@Override
	public ObjectBody constructCameraBody() {
		//TODO
		RigidBody body = new RigidBody(1, new DefaultMotionState(), new SphereShape(1));
		body.translate(new Vector3f(0,0,-3));
		return new ObjectBody(body); 
	}

	@Override
	public void addObjectBodies(List<ObjectBody> bodies) {
		RigidBody s = new RigidBody(100, new DefaultMotionState(), new SphereShape(1));
		s.translate(new Vector3f(0,0,3));
		ObjectBody so = new ObjectBody(s);
		so.renderObject.setAdditionalColor(new Color(255,0,0));
		so.renderObject.setSpecularLighting(true);
		bodies.add(so);
		
		RigidBody ss = new RigidBody(0.5f, new DefaultMotionState(), new SphereShape(0.67f));
		ss.translate(new Vector3f(0.1f,3,3));
		ss.setFriction(1);
		ObjectBody sso = new ObjectBody(ss);
		sso.renderObject.setAdditionalColor(new Color(0,255,0));
		bodies.add(sso);
		
		
		RigidBody ground = new RigidBody(0, new DefaultMotionState(), new StaticPlaneShape(new Vector3f(0,0,1),1));
		Transform t = new Transform();
		ground.getWorldTransform(t);
		t.basis.rotX((float)-Math.PI/2);
		t.origin.set(0, -3, 0);
		ground.setWorldTransform(t);
		ground.setFriction(1);
		ObjectBody go = new ObjectBody(ground);
		go.renderObject.setAdditionalColor(new Color(0,255,0));
		bodies.add(go);
	}

	@Override
	protected void addTextures(TextureManager manager) {
		
		
	}

	@Override
	public void addLights(World world) {
		world.setAmbientLight(5, 5, 5);
		
		Light light = new Light(world);
		light.setPosition(new SimpleVector(3,0,0));
		light.setIntensity(10,10,10);
		
		Light light2 = new Light(world);
		light2.setPosition(new SimpleVector(-6,-1,1));
		light2.setIntensity(30, 30, 30);
	}

}
