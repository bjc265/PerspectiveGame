package test;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class TestWorld {

	
	
	public static void main(String[] args){
		
		BroadphaseInterface broadphase = new DbvtBroadphase();
		
		DefaultCollisionConfiguration configuration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(configuration);
		
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		DiscreteDynamicsWorld world = new DiscreteDynamicsWorld(dispatcher,broadphase,solver,configuration);
		
		world.setGravity(new Vector3f(0,-9.8f,0));
		
		CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0,1,0),1);
		Transform groundTransform = new Transform();
		groundTransform.setRotation(new Quat4f(0,0,0,1));
		groundTransform.transform(new Vector3f(0,-1,0));
		DefaultMotionState groundMotionState = new DefaultMotionState(groundTransform);
		RigidBody groundBody = new RigidBody(0, groundMotionState, groundShape,new Vector3f(0,0,0));
		
		CollisionShape sphereShape = new SphereShape(1);
		Transform sphereTransform = new Transform();
		sphereTransform.setRotation(new Quat4f(0,0,0,-1));
		sphereTransform.transform(new Vector3f(0,-1,0));
		DefaultMotionState sphereMotionState = new DefaultMotionState(sphereTransform);
		RigidBody sphereBody = new RigidBody(1,sphereMotionState,sphereShape,new Vector3f(0,0,0));
		
		world.addRigidBody(groundBody);
		world.addRigidBody(sphereBody);
		
		
		
	}
	
	
	
}
