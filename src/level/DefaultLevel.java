package level;

import game.ObjectBody;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;

public class DefaultLevel extends Level {

	@Override
	public ObjectBody getCameraBody() {
		//TODO
		return null; 
	}

	@Override
	public RigidBody[] getObjectBodies() {
		return new RigidBody[0];
	}

}
