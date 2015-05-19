package ar.edu.itba.it.cg.yart.acceleration_estructures;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.raytracer.Ray;

abstract class AbstractNode implements Node {

	protected Node left;
	protected Node right;
	private AABB box;
	private List<GeometricObject> objects;
	protected Plane splittingPlane;
	protected double splitPoint;

	public AbstractNode(final AABB box, final List<GeometricObject> objects, final double splitPoint) {
		this.box = box;
		this.objects = objects;
		this.splitPoint = splitPoint;
	}
	
	public AABB getBoundingBox() {
		return this.box;
	}

	
	public List<GeometricObject> getObjects() {
		return this.objects;
	}
	
	public void setLeftNode(final Node node) {
		this.left = node;
	}
	
	public void setRightNode(final Node node) {
		this.right = node;
	}
	
	// this methods checks in which half space the point is
	public abstract Node nearNode(final Ray ray);

	public Node farNode(final Node nearNode) {
		return (nearNode == right) ? left : right;
	}
	
	public double distanceToSplittingPlane(Ray ray) {
		return this.splittingPlane.distanceFromRayOrigin(ray);
	}
	
}
