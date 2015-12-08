package game;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Clock;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
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
		rWorld = new World();
		rWorld.setAmbientLight(120, 120, 120);
		
		
		//add the ObjectBody representing the camera to the physics world, and align the camera with it's render object
		ObjectBody cameraBody = levelData.getCameraBody();
		dWorld.addRigidBody(cameraBody.rigidBody);
		rWorld.addObject(cameraBody.renderObject);
		rWorld.getCamera().align(cameraBody.renderObject);
		cameraBody.renderObject.setName("camera");
		cameraBody.renderObject.setVisibility(false);
		objects.add(cameraBody);
		
		
		//add each RigidBody to physics world, and add corresponding Object3D to render world
		for(RigidBody rb : levelData.getObjectBodies()){
			ObjectBody ob = new ObjectBody(rb);
			dWorld.addCollisionObject(ob.rigidBody);
			rWorld.addObject(ob.renderObject);
			objects.add(ob);
		}
		
		
		
		
		
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
		
		while(frame.isShowing()){
			
			//update physics
			float t = clock.getTimeMicroseconds();
			clock.reset();
			dWorld.stepSimulation(t/1000000f);
			
			//update ObjectBodies (includes camera)
			for(ObjectBody o : objects)
				o.update();
			
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
