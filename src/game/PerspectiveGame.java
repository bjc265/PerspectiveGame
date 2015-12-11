package game;

import input.MouseMapper;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.KeyMapper;
import com.threed.jpct.util.KeyState;

import level.Level;

public class PerspectiveGame {

	private World rWorld;
	private FrameBuffer buffer;
	private JFrame frame;
	private Clock clock;

	private DiscreteDynamicsWorld dWorld;

	private List<ObjectBody> objects = new LinkedList<ObjectBody>();

	private Level levelData;
	
	private KeyMapper keyMapper;
	private MouseMapper mouseMapper;
	private KeyState currKey;

	private ObjectBody cameraBody;
	private float cameraPitch;
	private Transform cameraTr;
	
	private boolean[] motionStates;
	private boolean[] rotationStates;
	private boolean[] rescaleStates;
	
	private Matrix3f yRotCC;
	private Matrix3f yRotC;
	
	@SuppressWarnings("unused")
	private MouseMapper input;
	
	public final float F_SPEED = 120;

	public PerspectiveGame(Level ld){
		levelData = ld;
		
		
		//construct physics and render worlds
		rWorld = new World();
		dWorld = new DiscreteDynamicsWorld(
				levelData.dispatcher,
				levelData.broadphase,
				levelData.constraintSolver,
				levelData.collisionConfiguration);

		cameraPitch = 0;

		initializeCamera();
		initializeBodies();
		miscellaniousSetup();

		//to be moved to Level class for customizability
		dWorld.setGravity(new Vector3f(0,-9.8f,0));
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
		cameraBody = levelData.cameraBody;
		dWorld.addRigidBody(cameraBody.rigidBody);
		rWorld.addObject(cameraBody.renderObject);
		objects.add(cameraBody);

		//make the body representing the camera invisible and always upright
		cameraBody.renderObject.setName("camera");
		cameraBody.renderObject.setVisibility(false);
		cameraBody.rigidBody.setAngularFactor(0);
		cameraBody.rigidBody.setFriction(1000);
	}

	/**
	 * Additional things that need to be set up, such as the clock, the rendering frame, the frame buffer, and the number of cores used 
	 */
	private void miscellaniousSetup() {
		
		cameraTr = new Transform();
		
		
		motionStates = new boolean[]{false,false,false,false};
		rotationStates = new boolean[]{false,false,false,false};
		rescaleStates = new boolean[]{false,false};
		clock = new Clock();
		frame=new JFrame("JPCTBullet Test");
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		buffer = new FrameBuffer(800,600, FrameBuffer.SAMPLINGMODE_NORMAL);
		
		input = new MouseMapper(rWorld,frame,buffer);
		keyMapper = new KeyMapper(frame);
		
		int cpu = Runtime.getRuntime().availableProcessors();
		if(cpu > 1){
			Config.useMultipleThreads = true;
			Config.maxNumberOfCores = cpu;
			Config.loadBalancingStrategy = 1;
		}
		
		yRotCC = new Matrix3f();
		yRotCC.rotY((float)Math.PI/2f);
		yRotC = new Matrix3f();
		yRotC.rotY((float)-Math.PI/2f);
	}

	private void run() {
		
		while(frame.isShowing()){
			//update physics
			float t = clock.getTimeMicroseconds()/1000000f;
			clock.reset();
			dWorld.stepSimulation(t);

			Camera camera = rWorld.getCamera(); 


			//update ObjectBodies
			for(ObjectBody o : objects){
				o.update();
				if(rescaleStates[0] != rescaleStates[1] && mouseMapper.selectedObject != null && o.renderObject == mouseMapper.selectedObject)
				{
					//o.persepctiveScale(camera,t,rescaleStates[0]);
				}
			}

			//update camera
			Object3D cameraObj = rWorld.getObjectByName("camera");
			
			camera.align(cameraObj);
			camera.rotateAxis(cameraObj.getXAxis(), cameraPitch);
			camera.setPositionToCenter(cameraObj);
			Transform tr = new Transform();
			dWorld.getCollisionObjectArray().get(1).getWorldTransform(tr);

			//keyboard input
			
			cameraBody.rigidBody.getWorldTransform(cameraTr);
			SimpleVector forward = camera.getDirection();
			Vector3f f = new Vector3f(forward.x,forward.y,forward.z);
			
			if(motionStates[0])
				cameraBody.rigidBody.applyCentralImpulse(new Vector3f(f.x*F_SPEED*t,f.y*F_SPEED*t,f.z*F_SPEED*t));
			if(motionStates[1])
				cameraBody.rigidBody.applyCentralImpulse(new Vector3f(-f.x*F_SPEED*t,-f.y*F_SPEED*t,-f.z*F_SPEED*t));
			
			
			if(rotationStates[2]){
				Vector3f l = new Vector3f(f.x*F_SPEED*t,f.y*F_SPEED*t,f.z*F_SPEED*t);
				yRotCC.transform(l);
				System.out.println(l);
				cameraBody.rigidBody.applyCentralImpulse(l);
			}
			if(rotationStates[3]){
				Vector3f l = new Vector3f(f.x*F_SPEED*t,f.y*F_SPEED*t,f.z*F_SPEED*t);
				yRotC.transform(l);
				cameraBody.rigidBody.applyCentralImpulse(l);
			}
			
			
			
			do{
				currKey = keyMapper.poll();
				switch(currKey.getKeyCode()){
				case(87):	//w
					if(currKey.getState()){
						motionStates[0] = true;
					} else{
						motionStates[0] = false;
					}
					break;
				case(83):	//s
					if(currKey.getState()){
						motionStates[1] = true;
					} else{
						motionStates[1] = false;
					}
					break;
				case(65):	//a
					if(currKey.getState()){
						motionStates[2] = true;
					} else{
						motionStates[2] = false;
					}
					break;
				case(68):	//d
					if(currKey.getState()){
						motionStates[3] = true;
					} else{
						motionStates[3] = false;
					}
					break;
				case(38):	//up arrow
					if(currKey.getState()){
						rotationStates[0] = true;
					} else{
						rotationStates[0] = false;
					}
					break;
				case(40):	//down arrow
					if(currKey.getState()){
						rotationStates[1] = true;
					} else{
						rotationStates[1] = false;
					}
					break;
				case(37):	//left arrow
					if(currKey.getState()){
						rotationStates[2] = true;
					} else{
						rotationStates[2] = false;
					}
					break;
				case(39):	//right arrow
					if(currKey.getState()){
						rotationStates[3] = true;
					} else{
						rotationStates[3] = false;
					}
					break;
				case(16):	//shift
					if(currKey.getState()){
						rescaleStates[0] = true;
					} else{
						rescaleStates[0] = false;
					}
					break;
				case(17):	//ctrl
					if(currKey.getState()){
						rescaleStates[1] = true;
					} else{
						rescaleStates[1] = false;
					}
				default:
					break;
				}
			} while(currKey != KeyState.NONE);
			
			
			
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
