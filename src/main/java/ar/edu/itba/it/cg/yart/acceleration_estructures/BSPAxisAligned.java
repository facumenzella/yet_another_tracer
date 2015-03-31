package ar.edu.itba.it.cg.yart.acceleration_estructures;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public class BSPAxisAligned {

	private Node root;
	private final int depth;
	
	public BSPAxisAligned(final int depth, final List<GeometricObject> objects, 
			final float startPoint, final float endPoint, final Axis boundingAxis) {
		this.depth = depth;
		this.root = new Node(boundingAxis, startPoint, endPoint);
		subdivideScene(this.root, 0);
	}
	
	
	private void subdivideScene(final Node currentNode, final int currentDepth) {
		if (currentDepth == depth - 1) {
			return;
		}
		
		Axis nextAxis;
		final float midPoint = currentNode.middleAxisPoint;
		if (currentNode.boundingAxis == Axis.X) {
			nextAxis = Axis.Y;
		} else if (currentNode.boundingAxis == Axis.Y) {
			nextAxis = Axis.Z;
		} else {
			nextAxis = Axis.X;
		}
		
		Node leftNode = new Node(nextAxis, currentNode.startAxisPoint, midPoint);
		Node rightNode = new Node(nextAxis, midPoint, currentNode.endAxisPoint);
		currentNode.left = leftNode;
		currentNode.right = rightNode;
		
		for (GeometricObject o : currentNode.objects) {
			// TODO check which side of the plane the objects are
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
		public Axis boundingAxis;
		public float startAxisPoint;
		public float middleAxisPoint;
		public float endAxisPoint;
		private List<GeometricObject> objects;

		public Node(final Axis boundingAxis, final float startAxisPoint, final float endAxisPoint) {
			this.boundingAxis = boundingAxis;
			this.startAxisPoint = startAxisPoint;
			this.middleAxisPoint = (endAxisPoint - startAxisPoint)/2;
			this.endAxisPoint = endAxisPoint;
			this.objects = new ArrayList<GeometricObject>();
		}
		
		public void addObject(final GeometricObject object) {
			this.objects.add(object);
		}
	}
	
}
