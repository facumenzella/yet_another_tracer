package ar.edu.itba.it.cg.yart.acceleration_estructures;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;

public interface Node {

	public AABB getBoundingBox();
	public List<GeometricObject> getObjects();
	public void setLeftNode(final Node node);
	public void setRightNode(final Node node);
	public boolean isLeaf();
	public Node nearNode(final Ray ray);
	public Node farNode(final Node nearNode);
	public double distanceToSplittingPlane(Ray ray);
	
}
