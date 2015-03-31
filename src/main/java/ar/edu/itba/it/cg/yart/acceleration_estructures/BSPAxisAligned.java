package ar.edu.itba.it.cg.yart.acceleration_estructures;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.primitives.BoundingBox;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public class BSPAxisAligned {

	private Node root;
	private final int depth;
	
	public BSPAxisAligned(final int depth, final List<GeometricObject> objects, 
			final BoundingBox initialBox, final Axis startAxis) {
		this.depth = depth;
		this.root = new Node(initialBox, objects);
		subdivideScene(this.root, 0, startAxis);
	}
	
	
	private void subdivideScene(final Node currentNode, final int currentDepth, final Axis toSplitAxis) {
		if (currentDepth == depth - 1) {
			// We reached the depth
			return;
		}
		
		final BoundingBox currentBox = currentNode.box;
		Axis nextAxis;
		final BoundingBox leftBox;
		final BoundingBox rightBox;
		if (toSplitAxis == Axis.X) {
			final double middleX = (currentBox.p0.x + currentBox.p1.x) / 2;
			leftBox = new BoundingBox(currentBox.p0, new Point3(middleX, currentBox.p1.y, currentBox.p1.z));
			rightBox = new BoundingBox(new Point3(middleX, currentBox.p0.y, currentBox.p0.z), currentBox.p1);
			nextAxis = Axis.Y;
		} else if (toSplitAxis == Axis.Y) {
			final double middleY = (currentBox.p0.y + currentBox.p1.y) / 2;
			leftBox = new BoundingBox(currentBox.p0, new Point3(currentBox.p1.x, middleY, currentBox.p1.z));
			rightBox = new BoundingBox(new Point3(currentBox.p0.x, middleY, currentBox.p0.z), currentBox.p1);
			nextAxis = Axis.Z;
		} else {
			final double middleZ = (currentBox.p0.y + currentBox.p1.y) / 2;
			leftBox = new BoundingBox(currentBox.p0, new Point3(currentBox.p1.x, currentBox.p1.y, middleZ));
			rightBox = new BoundingBox(new Point3(currentBox.p0.x, currentBox.p0.y, middleZ), currentBox.p1);
			nextAxis = Axis.X;
		}
		
		List<GeometricObject> leftObjects = new ArrayList<GeometricObject>();
		List<GeometricObject> rightObjects = new ArrayList<GeometricObject>();
		
		for (GeometricObject o : currentNode.objects) {
			final BoundingBox box = o.createBoundingBox();
			if (leftBox.boxIsInside(box)) {
				leftObjects.add(o);
			} else {
				rightObjects.add(o);
			}
		}
		currentNode.left = new Node(leftBox, leftObjects);
		currentNode.right = new Node(rightBox, rightObjects);
		
		if (currentNode.left.objects.size() > 1) {
			subdivideScene(currentNode.left, currentDepth + 1, nextAxis);
		}
		if (currentNode.right.objects.size() > 1) {
			subdivideScene(currentNode.right, currentDepth + 1, nextAxis);
		}	
	}

	
	/*
	 * Node implementation
	 */
	
	private class Node{
		@SuppressWarnings("unused")
		public Node left;
		@SuppressWarnings("unused")
		public Node right;
		public BoundingBox box;
		public List<GeometricObject> objects;

		public Node(final BoundingBox box, final List<GeometricObject> objects) {
			this.box = box;
			this.objects = objects;
			this.objects = new ArrayList<GeometricObject>();
		}
	}
	
}
