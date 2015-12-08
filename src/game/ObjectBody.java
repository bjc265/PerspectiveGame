package game;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;

public class ObjectBody {

	public final Object3D renderObject;
	public RigidBody rigidBody;
	
	public ObjectBody(RigidBody b){
		rigidBody = b;
		renderObject = createRenderObject(b);
	}
	
	private Object3D createRenderObject(RigidBody b) {
		// TODO
		return null;
	}

	public void update(){
		Transform transform = new Transform();
		rigidBody.getWorldTransform(transform);
		Vector3f t = transform.origin;
		Matrix3f r = transform.basis;
		
		
		Matrix translation = new Matrix();
		Matrix rotation = new Matrix();
		
		translation.setColumn(4, t.x, t.y, t.z, 1);
		rotation.setDump(new float[]{r.m00,r.m01,r.m02,0,r.m10,r.m11,r.m12,0,r.m20,r.m21,r.m22,0,0,0,0,1});
		
		renderObject.setTranslationMatrix(translation);
		renderObject.setRotationMatrix(rotation);
	}
	
}
