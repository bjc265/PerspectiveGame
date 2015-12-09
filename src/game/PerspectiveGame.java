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

import level.Level;

public class PerspectiveGame {

	private World rWorld;
	private FrameBuffer buffer;
	private JFrame frame;
	private Clock clock;

	private DiscreteDynamicsWorld dWorld;

	private List<ObjectBody> objects = new LinkedList<ObjectBody>();

	private Level levelData;

	public PerspectiveGame(Level ld){
		levelData = ld;
		//construct physics and render worlds
		rWorld = new World();
		dWorld = new DiscreteDynamicsWorld(
				levelData.dispatcher,
				levelData.broadphase,
				levelData.constraintSolver,
				levelData.collisionConfiguration);

		initializeCamera();
		initializeBodies();
		miscellaniousSetup();

		//to be moved to Level class for customizability
		dWorld.setGravity(new Vector3f(-9.8f,-9.8f,0));
		rWorld.setAmbientLight(120, 120, 120);
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Level data = (Level)Class.forName("level." + args[0]).newInstance();
		PerspectiveGame game = new PerspectiveGame(data);
		game.run();
	}

	/**
	 * Adds bodies from level into physics and rendering worlds
	 */
	private void initializeBodies() {
		for(ObjectBody ob : levelData.objectBodies){
			dWorld.addRigidBody(ob.rigidBody);
			rWorld.addObject(ob.renderObject);
			objects.add(ob);
		}
	}

	/**
	 * Adds the body that the camera will be bound to (represents "the player"). The cannot be rotated, and is not rendered.
	 */
	private void initializeCamera() {
		//add the ObjectBody representing the camera to the physics world, and align the camera with it's render object
		ObjectBody cameraBody = levelData.cameraBody;
		dWorld.addRigidBody(cameraBody.rigidBody);
		rWorld.addObject(cameraBody.renderObject);
		objects.add(cameraBody);

		//make the body representing the camera invisible and always upright
		cameraBody.renderObject.setName("camera");
		cameraBody.renderObject.setVisibility(false);
		cameraBody.rigidBody.setAngularFactor(0);
	}

	/**
	 * Additional things that need to be set up, such as the clock, the rendering frame, the frame buffer, and the number of cores used 
	 */
	private void miscellaniousSetup() {
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

	private void run() {
		buffer = new FrameBuffer(800,600, FrameBuffer.SAMPLINGMODE_NORMAL);
		while(frame.isShowing()){
			//update physics
			float t = clock.getTimeMicroseconds();
			clock.reset();
			dWorld.stepSimulation(t/1000000f);


			//update ObjectBodies
			for(ObjectBody o : objects)
				o.update();


			//update camera
			rWorld.getCamera().align(rWorld.getObjectByName("camera"));
			rWorld.getCamera().setPositionToCenter(rWorld.getObjectByName("camera"));

			System.out.println(rWorld.getCamera().getDirection());

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
