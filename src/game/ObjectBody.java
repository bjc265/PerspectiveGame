package game;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

public class ObjectBody {

	public final Object3D renderObject;
	public RigidBody rigidBody;
	
	public ObjectBody(RigidBody b){
		rigidBody = b;
		renderObject = createRenderObject(b);
	}
	
	private Object3D createRenderObject(RigidBody rb) {
		Object3D obj;
		switch(rb.getCollisionShape().getClass().getSimpleName()){
		case("TriangleMeshShape"):
			//TODO
			return null;
		case("BoxShape"):
			//TODO
			return null;
		case("SphereShape"):
			SphereShape ss = (SphereShape)rb.getCollisionShape();
			obj = Primitives.getSphere(ss.getRadius());
			obj.build();
			return obj;
		}
		return null;
	}

	public void update(){
		Transform transform = new Transform();
		rigidBody.getWorldTransform(transform);
		Vector3f t = transform.origin;
		Matrix3f r = transform.basis;
		
		Matrix translation = new Matrix();
		Matrix rotation = new Matrix();
		
		//translation.setColumn(3, t.x, t.y, t.z, 1);
		translation.set(3, 0, t.x);
		translation.set(3,1,t.y);
		translation.set(3,2,t.z);
		rotation.setDump(new float[]{r.m00,r.m01,r.m02,0,r.m10,r.m11,r.m12,0,r.m20,r.m21,r.m22,0,0,0,0,1});
		
		//renderObject.translate(new SimpleVector(t.x,t.y,t.z));
		renderObject.setTranslationMatrix(translation);
		renderObject.setRotationMatrix(rotation);
	}
	
}
