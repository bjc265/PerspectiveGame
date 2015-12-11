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
		RigidBody body = new RigidBody(0, new DefaultMotionState(), new SphereShape(1));
		body.translate(new Vector3f(0,2,-8));
		return new ObjectBody(body); 
	}

	@Override
	public void addObjectBodies(List<ObjectBody> bodies) {
		RigidBody s = new RigidBody(100, new DefaultMotionState(), new SphereShape(1));
		s.translate(new Vector3f(0,0,3));
		s.setRestitution(0.5f);
		s.setFriction(.3f);
		ObjectBody so = new ObjectBody(s);
		so.renderObject.setAdditionalColor(new Color(255,0,0));
		so.renderObject.setSpecularLighting(true);
		bodies.add(so);
		
		
		
		for(int i=0;i<9;i++){
			RigidBody ss = new RigidBody(2f, new DefaultMotionState(), new SphereShape(0.67f));
			ss.translate(new Vector3f((float)Math.random()*3-1.5f,(float)Math.random()*10+2,(float)Math.random()*3-1.5f));
			ss.setFriction(0.2f);
			ss.setRestitution(0.6f);
			Vector3f inertia = new Vector3f();
			ss.getCollisionShape().calculateLocalInertia(.5f, inertia);
			ss.setMassProps(.5f, inertia);
			ObjectBody sso = new ObjectBody(ss);
			sso.renderObject.setAdditionalColor(new Color((int)(Math.random()*200+55),(int)(Math.random()*200+55),(int)(Math.random()*200+55)));
			so.renderObject.setSpecularLighting(true);
			bodies.add(sso);
		}
		
		
		RigidBody ground = new RigidBody(0, new DefaultMotionState(), new StaticPlaneShape(new Vector3f(0,0,1),1));
		Transform t = new Transform();
		ground.getWorldTransform(t);
		t.basis.rotX((float)-Math.PI/2);
		t.origin.set(0, -3, 0);
		ground.setWorldTransform(t);
		ground.setFriction(0.1f);
		ground.setRestitution(0.7f);
		ObjectBody go = new ObjectBody(ground);
		go.renderObject.setAdditionalColor(new Color(0,255,0));
		//bodies.add(go);
		
		RigidBody box = new RigidBody(0, new DefaultMotionState(), new BoxShape(new Vector3f(50,.1f,50)));
		box.translate(new Vector3f(0,-1,0));
		box.setFriction(0.01f);
		ObjectBody bo = new ObjectBody(box);
		bo.renderObject.setAdditionalColor(50,150,50);
		bodies.add(bo);
		
		RigidBody wall0 = new RigidBody(0, new DefaultMotionState(), new BoxShape(new Vector3f(.1f,20,50)));
		wall0.translate(new Vector3f(-50,10,0));
		wall0.setFriction(0);
		wall0.setRestitution(0.6f);
		ObjectBody wo0 = new ObjectBody(wall0);
		wo0.renderObject.setAdditionalColor(20,20,80);
		bodies.add(wo0);
		
		RigidBody wall1 = new RigidBody(0, new DefaultMotionState(), new BoxShape(new Vector3f(.1f,20,50)));
		wall1.translate(new Vector3f(50,10,0));
		wall1.setFriction(0);
		wall1.setRestitution(0.6f);
		ObjectBody wo1 = new ObjectBody(wall1);
		wo1.renderObject.setAdditionalColor(20,20,80);
		bodies.add(wo1);
		
		RigidBody wall2 = new RigidBody(0, new DefaultMotionState(), new BoxShape(new Vector3f(50,20,.1f)));
		wall2.translate(new Vector3f(0,10,-50));
		wall2.setFriction(0);
		wall2.setRestitution(0.6f);
		ObjectBody wo2 = new ObjectBody(wall2);
		wo2.renderObject.setAdditionalColor(20,20,80);
		bodies.add(wo2);
		
		RigidBody wall3 = new RigidBody(0, new DefaultMotionState(), new BoxShape(new Vector3f(50,20,.1f)));
		wall3.translate(new Vector3f(0,10,50));
		wall3.setFriction(0);
		wall3.setRestitution(0.6f);
		ObjectBody wo3 = new ObjectBody(wall3);
		wo3.renderObject.setAdditionalColor(20,20,80);
		bodies.add(wo3);
	}

	@Override
	protected void addTextures(TextureManager manager) {
		
		
	}

	@Override
	public void addLights(World world) {
		world.setAmbientLight(5, 5, 5);
		
		Light light = new Light(world);
		light.setPosition(new SimpleVector(3,0,0));
		light.setIntensity(0,0,0);
		//light.setAttenuation(-.5f);
		
		Light light2 = new Light(world);
		light2.setPosition(new SimpleVector(10,-10,10));
		light2.setIntensity(10, 10, 10);
		//light2.setAttenuation(1);
		
		Light bigl = new Light(world);
		bigl.setPosition(new SimpleVector(-10,-15,-10));
		bigl.setIntensity(20,20,20);
		//light2.setAttenuation(1);
		
	}

}
