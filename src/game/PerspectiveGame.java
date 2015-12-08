package game;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import level.CameraBody;
import level.Level;

public class PerspectiveGame {
	
	private World rWorld;
	private FrameBuffer buffer;
	private JFrame frame;
	private Clock clock;
	
	public DiscreteDynamicsWorld dWorld;
	
	List<ObjectBody> objects = new LinkedList<ObjectBody>();
	
	public PerspectiveGame(Level levelData){
		dWorld = new DiscreteDynamicsWorld(
				levelData.getDispatcher(),
				levelData.getBroadphase(),
				levelData.getConstraintSolver(),
				levelData.getCollisionConfiguration());
		dWorld.setGravity(new Vector3f(0,-9.8f,0));
		rWorld = new World();
		rWorld.setAmbientLight(120, 120, 120);
		
		
		//add the ObjectBody representing the camera to the physics world, and align the camera with it's render object
		ObjectBody cameraBody = new ObjectBody(levelData.getCameraBody());
		dWorld.addRigidBody(cameraBody.rigidBody);
		rWorld.addObject(cameraBody.renderObject);
		
		cameraBody.renderObject.setName("camera");
		cameraBody.renderObject.setVisibility(false);
		objects.add(cameraBody);
		
		
		//add each RigidBody to physics world, and add corresponding Object3D to render world
		RigidBody[] bodies = levelData.getObjectBodies();
		for(RigidBody rb : bodies){
			ObjectBody ob = new ObjectBody(rb);
			dWorld.addRigidBody(ob.rigidBody);
			rWorld.addObject(ob.renderObject);
			objects.add(ob);
		}
		
		
		
		clock = new Clock();
		
		frame=new JFrame("JPCTBullet Test");
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		int cpu = Runtime.getRuntime().availableProcessors();
	    if(cpu > 1){
	      Config.useMultipleThreads = true;
	      Config.maxNumberOfCores = cpu;
	      Config.loadBalancingStrategy = 1;
	    }
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Level data = (Level)Class.forName("level." + args[0]).newInstance();
		PerspectiveGame game = new PerspectiveGame(data);
		game.run();
	}

	private void run() {
		buffer = new FrameBuffer(800,600, FrameBuffer.SAMPLINGMODE_NORMAL);
		Transform test = new Transform();
		while(frame.isShowing()){
			
			//update physics
			float t = clock.getTimeMicroseconds();
			clock.reset();
			dWorld.stepSimulation(t/1000000f);
			
			
			//update ObjectBodies
			for(ObjectBody o : objects)
				o.update();
			
			//update camera
			rWorld.getCamera().setOrientation(new SimpleVector(0,0,1), new SimpleVector(0,1,0));
			//rWorld.getCamera().align(rWorld.getObjectByName("camera"));
			rWorld.getCamera().setPositionToCenter(rWorld.getObjectByName("camera"));
			
			
			
			System.out.println(rWorld.getObject(1).getTranslation());
			
			//render
			buffer.clear();
			rWorld.renderScene(buffer);
			rWorld.draw(buffer);
			buffer.update();
			buffer.display(frame.getGraphics());
		}
		
		buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
		buffer.dispose();
		frame.dispose();
	}
	

}
