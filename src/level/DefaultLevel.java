package level;

import game.ObjectBody;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;

public class DefaultLevel extends Level {

	@Override
	public RigidBody getCameraBody() {
		//TODO
		RigidBody body = new RigidBody(0, new DefaultMotionState(), new SphereShape(1));
		
		body.setAngularFactor(0);
		
		return body; 
	}

	@Override
	public RigidBody[] getObjectBodies() {
		RigidBody s = new RigidBody(1, new DefaultMotionState(), new SphereShape(1));
		s.translate(new Vector3f(0,0,10));
		return new RigidBody[]{s};
	}

}
