package ar.edu.itba.it.cg.yart.acceleration_estructures;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.primitives.BoundingBox;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;

public class LeafNode implements Node{

	private final List<GeometricObject> objects;
	
	public LeafNode(final List<GeometricObject> objects) {
		this.objects = objects;
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		return null;
	}

	@Override
	public List<GeometricObject> getObjects() {
		return this.objects;
	}

	@Override
	public void setLeftNode(Node node) {
		return;
	}
	
	@Override
	public void setRightNode(Node node) {
		return;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public Node nearNode(Ray ray) {
		return null;
	}

	@Override
	public Node farNode(Node nearNode) {
		return null;
	}

	@Override
	public double distanceToSplittingPlane(Ray ray) {
		return 0;
	}

}
