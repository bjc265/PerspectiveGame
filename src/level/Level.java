package level;

import java.util.LinkedList;
import java.util.List;

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
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.Light;

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
	
	
	public final ObjectBody cameraBody;
	public final List<ObjectBody> objectBodies;
	public final BroadphaseInterface broadphase;
	public final CollisionConfiguration collisionConfiguration;
	public final ConstraintSolver constraintSolver;
	public final Dispatcher dispatcher;

	
	public Level(){
		
		
		
		cameraBody = constructCameraBody();
		
		objectBodies = new LinkedList<ObjectBody>();
		broadphase = constructBroadphase();
		collisionConfiguration = constructCollisionConfiguration();
		constraintSolver = constructConstraintSolver();
		dispatcher = constructDispatcher(collisionConfiguration);
		
		addTextures(TextureManager.getInstance());
		addObjectBodies(objectBodies);
		cameraBody.rigidBody.setAngularFactor(0);
		cameraBody.renderObject.setVisibility(false);
		
	}
	
	
	/**
	 * Camera is assumed to be facing <1,0,0>, so to face another direction, rotate the RigidBody accordingly.
	 */
	protected abstract ObjectBody constructCameraBody();

	protected abstract void addObjectBodies(List<ObjectBody> bodies);
	
	protected abstract void addTextures(TextureManager manager);
	
	public abstract void addLights(World world);
	
	protected BroadphaseInterface constructBroadphase(){
		return new AxisSweep3(new Vector3f(-1000,-1000,-1000),new Vector3f(1000,1000,1000));
	}
	
	protected CollisionConfiguration constructCollisionConfiguration(){
		return new DefaultCollisionConfiguration();
	}
	
	protected ConstraintSolver constructConstraintSolver(){
		return new SequentialImpulseConstraintSolver();
	}
	
	protected Dispatcher constructDispatcher(CollisionConfiguration config){
		return new CollisionDispatcher(config);
	}
	
	
}
