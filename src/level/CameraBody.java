package level;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.threed.jpct.Camera;

public class CameraBody {

	public RigidBody body;
	public Camera camera;
	
	private Vector3f loc = new Vector3f();
	
	public void update(){
		
		body.getCenterOfMassPosition(loc);
		camera.setPosition(loc.x,loc.y,loc.z);
	}
	
}
