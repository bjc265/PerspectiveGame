package level;

import game.ObjectBody;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;

/**
 * The level class is our way of creating pre-built game levels. Because adding jBullet 
 * physics properties functionality to the XML parser would be both time consuming and beyond 
 * the scope of this project, we will simply have subclasses that store the data and are called 
 * in the main method via reflection.
 * The non-abstract methods may be overwritten, but their defaults should be "good enough" performance-wise.
 * @author Brett
 *
 */
public abstract class Level {
	
	/**
	 * Camera is assumed to be facing <1,0,0>, so to face another direction, rotate the RigidBody accordingly.
	 */
	public abstract RigidBody getCameraBody();

	public abstract RigidBody[] getObjectBodies();
	
	public BroadphaseInterface getBroadphase(){
		return new AxisSweep3(new Vector3f(-1000,-1000,-1000),new Vector3f(1000,1000,1000));
	}
	
	public CollisionConfiguration getCollisionConfiguration(){
		return new DefaultCollisionConfiguration();
	}
	
	public ConstraintSolver getConstraintSolver(){
		return new SequentialImpulseConstraintSolver();
	}
	
	public Dispatcher getDispatcher(){
		return new CollisionDispatcher(getCollisionConfiguration());
	}
	
	
}
