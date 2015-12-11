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
	
	private Transform sObjLastTr;
	
	
	
	private Matrix3f yRotCC;
	private Matrix3f yRotC;
	
	
	
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
		initializeLights();
		miscellaniousSetup();

		//to be moved to Level class for customizability
		dWorld.setGravity(new Vector3f(0,-9.8f,0));
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
		cameraBody.rigidBody.setFriction(100);
	}

	private void initializeLights() {
		levelData.addLights(rWorld);
	}

	/**
	 * Additional things that need to be set up, such as the clock, the rendering frame, the frame buffer, and the number of cores used 
	 */
	private void miscellaniousSetup() {
		
		cameraTr = new Transform();
		sObjLastTr = null;
		
		motionStates = new boolean[]{false,false,false,false};
		rotationStates = new boolean[]{false,false,false,false};
		rescaleStates = new boolean[]{false,false};
		clock = new Clock();
		frame=new JFrame("JPCTBullet Test");
		frame.setSize(1600, 1200);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		buffer = new FrameBuffer(1600,1200, FrameBuffer.SAMPLINGMODE_NORMAL);
		
		mouseMapper = new MouseMapper(rWorld,frame,buffer);
		keyMapper = new KeyMapper(frame);
		
		int cpu = Runtime.getRuntime().availableProcessors();
		if(cpu > 1){
			Config.useMultipleThreads = true;
			Config.maxNumberOfCores = cpu;
			Config.loadBalancingStrategy = 1;
		}
		
		yRotCC = new Matrix3f();
		yRotCC.rotY((float)Math.PI/100f);
		yRotC = new Matrix3f();
		yRotC.rotY((float)-Math.PI/100f);
	}

	private void run() {
		
		while(frame.isShowing()){
			//update physics
			float t = clock.getTimeMicroseconds()/1000000f;
			clock.reset();
			dWorld.stepSimulation(t);

			Camera camera = rWorld.getCamera(); 

			//update camera
			Object3D cameraObj = rWorld.getObjectByName("camera");

			//update ObjectBodies
			for(ObjectBody o : objects){
				o.update();
				Matrix3f ma = new Matrix3f();
				Vector3f an = new Vector3f();
				o.rigidBody.getAngularVelocity(an);
				o.rigidBody.getInvInertiaTensorWorld(ma);
				if(mouseMapper.selectedObject == o.renderObject){
					Transform tr = new Transform();
					
					if(mouseMapper.newlySelected){
						o.rigidBody.getWorldTransform(tr);
						sObjLastTr = new Transform();
						sObjLastTr.basis.set((Matrix3f) tr.basis.clone());
						
						sObjLastTr.origin.set(tr.origin);
						System.out.println(sObjLastTr.origin);
						mouseMapper.newlySelected = false;
					}
					
					o.rigidBody.setWorldTransform(sObjLastTr);
					
					Vector3f v = new Vector3f(tr.origin.x,tr.origin.y,tr.origin.z);
					SimpleVector cv = camera.getPosition();
					v.x -= cv.x;
					v.y -= cv.y;
					v.z -= cv.z;
					if(rescaleStates[0] != rescaleStates[1])
					{
						System.out.println("rescaling");
						o.persepctiveScale(camera,t,rescaleStates[0],sObjLastTr);
					
					}
				}
				
			}
			
			//keyboard input
			
			cameraBody.rigidBody.getWorldTransform(cameraTr);
			//System.out.println(camera.getDirection());
			SimpleVector f = camera.getDirection();
			f.y = 0;
			f.x = -f.x;
			SimpleVector s = camera.getSideVector();
			s.y = 0;
			s.x = -s.x;

			
			if(motionStates[0])
				cameraBody.rigidBody.applyCentralImpulse(new Vector3f(f.x*F_SPEED*t,f.y*F_SPEED*t,f.z*F_SPEED*t));
			if(motionStates[1])
				cameraBody.rigidBody.applyCentralImpulse(new Vector3f(-f.x*F_SPEED*t,-f.y*F_SPEED*t,-f.z*F_SPEED*t));
			if(motionStates[2])
				cameraBody.rigidBody.applyCentralImpulse(new Vector3f(-s.x*F_SPEED*t,-s.y*F_SPEED*t,-s.z*F_SPEED*t));
			if(motionStates[3])
				cameraBody.rigidBody.applyCentralImpulse(new Vector3f(s.x*F_SPEED*t,s.y*F_SPEED*t,s.z*F_SPEED*t));
			
			
			if(rotationStates[0] && cameraPitch < (float)Math.PI/2f){
				cameraPitch += (float)Math.PI*t;
			}
			if(rotationStates[1] && cameraPitch > (float)-Math.PI/2f){
				cameraPitch -= (float)Math.PI*t;
			}
			if(rotationStates[2]){
				Transform rot = new Transform();
				cameraBody.rigidBody.getWorldTransform(rot);
				rot.basis.mul(yRotCC);
				cameraBody.rigidBody.setWorldTransform(rot);
			}
			if(rotationStates[3]){
				Transform rot = new Transform();
				cameraBody.rigidBody.getWorldTransform(rot);
				rot.basis.mul(yRotC);
				cameraBody.rigidBody.setWorldTransform(rot);
			}
			
			
			
			camera.align(cameraObj);
			camera.setPositionToCenter(cameraObj);
			SimpleVector dirNew = camera.getDirection();
			dirNew.rotateX(cameraPitch);
			//dirNew.rotateY(cameraYaw);
			SimpleVector upNew = camera.getUpVector();
			upNew.rotateX(cameraPitch);
			//upNew.rotateY(cameraYaw);
			camera.setOrientation(dirNew, upNew);
			Transform tr = new Transform();
			dWorld.getCollisionObjectArray().get(1).getWorldTransform(tr);

			
			
			
			
			
			
			
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
					break;
				case(32):	//space
					if(currKey.getState()){
						Vector3f vel = new Vector3f();
						cameraBody.rigidBody.getLinearVelocity(vel);
						if(Math.abs(vel.y)<0.1)
							cameraBody.rigidBody.applyCentralImpulse(new Vector3f(0,10,0));
					}
					break;
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
