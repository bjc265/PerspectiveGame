package level;

import java.awt.Color;
import java.util.List;

import game.ObjectBody;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.threed.jpct.TextureManager;

public class DefaultLevel extends Level {

	@Override
	public ObjectBody constructCameraBody() {
		//TODO
		RigidBody body = new RigidBody(0, new DefaultMotionState(), new SphereShape(1));
		return new ObjectBody(body); 
	}

	@Override
	public void addObjectBodies(List<ObjectBody> bodies) {
		RigidBody s = new RigidBody(1, new DefaultMotionState(), new SphereShape(1));
		s.translate(new Vector3f(0,0,10));
		ObjectBody so = new ObjectBody(s);
		so.renderObject.setAdditionalColor(new Color(255,0,0));
		bodies.add(so);
		
	}

	@Override
	protected void addTextures(TextureManager manager) {
		
		
	}

}
