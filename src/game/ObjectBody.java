package game;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.util.ExtendedPrimitives;

public class ObjectBody {

	public final Object3D renderObject;
	public final RigidBody rigidBody;
	public final String textureName;
	
	public ObjectBody(RigidBody body){
		rigidBody = body;
		renderObject = createRenderObject(body);
		textureName = null;
	}
	
	public ObjectBody(RigidBody body, String texName){
		rigidBody = body;
		textureName = texName;
		renderObject = createRenderObject(body);
	}
	
	private Object3D createRenderObject(RigidBody rb) {
		Object3D obj = null;
		switch(rb.getCollisionShape().getClass().getSimpleName()){
		case("BvhTriangleMeshShape"):
			obj = Loader.loadOBJ(textureName, null, 1)[0];
			break;
		case("BoxShape"):
			//TODO
			break;
		case("SphereShape"):
			SphereShape ss = (SphereShape)rb.getCollisionShape();
			obj = Primitives.getSphere(ss.getRadius());
			break;
		case("StaticPlaneShape"):
			StaticPlaneShape sps = (StaticPlaneShape)rb.getCollisionShape();
			obj = Primitives.getPlane(2, 1e30f);
			obj = ExtendedPrimitives.createPlane(1,0);
			Vector3f n3f = new Vector3f();
			sps.getPlaneNormal(n3f);
			
			SimpleVector oldNormal = new SimpleVector(n3f.x,n3f.y,n3f.z);
			SimpleVector newNormal = new SimpleVector(0,0,1);
			SimpleVector rotAxis = newNormal.calcCross(oldNormal);
			float angle = newNormal.calcAngle(oldNormal);
			obj.rotateAxis(rotAxis, angle);
			
			break;
		}
		obj.build();
		return obj;
	}

	public void update(){
		Transform transform = new Transform();
		rigidBody.getWorldTransform(transform);
		Vector3f t = transform.origin;
		Matrix3f r = transform.basis;
		
		Matrix translation = new Matrix();
		Matrix rotation = new Matrix();
		
		translation.setRow(3, -t.x, -t.y, t.z, 1);
		
		rotation.setDump(new float[]{r.m00,r.m01,r.m02,0,r.m10,r.m11,r.m12,0,r.m20,r.m21,r.m22,0,0,0,0,1});
		
		//jpct's axes are -x,-y,and z, so we need to reorient
		rotation.setOrientation(new SimpleVector(0,0,1), new SimpleVector(0,1,0));
		
		
		renderObject.setTranslationMatrix(translation);
		renderObject.setRotationMatrix(rotation);
	}
	
}
