package input;

import game.ObjectBody;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JFrame;

import static com.threed.jpct.Interact2D.*;

import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Object3D;
import com.threed.jpct.World;

public class MouseMapper implements MouseListener{
	
	private World rWorld;
	private FrameBuffer buffer;
	
	public Object3D selectedObject;
	public Transform lastTransform;
	public boolean newlySelected;

	
	public MouseMapper(World world, JFrame frame, FrameBuffer buf){
		frame.addMouseListener(this);
		buffer = buf;
		rWorld = world;
		selectedObject = null;
		lastTransform = null;
		newlySelected = false;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int[] id = pickPolygon(rWorld.getVisibilityList(), reproject2D3D(rWorld.getCamera(),buffer,e.getX(),e.getY()));
		
		selectedObject = (id == null ? null : rWorld.getObject(getObjectID(id)));
		newlySelected = true;
		System.out.println(selectedObject);
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		selectedObject = null;
		lastTransform = null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		
	}


	
}
