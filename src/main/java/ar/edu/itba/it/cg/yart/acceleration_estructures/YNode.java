package ar.edu.itba.it.cg.yart.acceleration_estructures;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.raytracer.Ray;

public class YNode extends AbstractNode{

	public YNode(AABB box, List<GeometricObject> objects, final double splitPoint) {
		super(box, objects, splitPoint);
		this.splittingPlane = new Plane(new Point3d(0, splitPoint, 0), Vector3d.yAxis());
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public Node nearNode(Ray ray) {
		if (ray.origin.y <= this.splitPoint) {
			return this.left;
		}
		return this.right;
	}

}
