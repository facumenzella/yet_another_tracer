package ar.edu.itba.it.cg.yart.acceleration_estructures;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.BoundingBox;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.raytracer.Ray;

public class XNode extends AbstractNode{
	
	public XNode(BoundingBox box, List<GeometricObject> objects, final double splitPoint) {
		super(box, objects, splitPoint);
		this.splittingPlane = new Plane(new Point3d(splitPoint, 0, 0), Vector3d.xAxis());
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public Node nearNode(Ray ray) {
		if (ray.origin.x <= this.splitPoint) {
			return this.left;
		}
		return this.right;
	}
	
}
