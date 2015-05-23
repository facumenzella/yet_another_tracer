package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Normal3d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Plane extends GeometricObject {

	public Point3d p;
	public Vector3d normal;

	public Plane(final Point3d p, final Vector3d normal) {
		this.p = p;
		this.normal = normal;
		updateBoundingBox();
	}
	
	public Plane() {
		this(new Point3d(0, 0, 0), new Normal3d(0, 0, 1));
	}

	@Override
	public double hit(final Ray ray, final ShadeRec sr, final Stack stack) {		
		double t = (p.sub(ray.origin)).dot(normal) / ray.direction.dot(normal);

		if (t > EPSILON) {
			sr.normal = normal;
			sr.localHitPoint = ray.origin.add(ray.direction.scale(t));
			return t;
		} else {
			return Double.NEGATIVE_INFINITY;
		}
	}

	@Override
	public double shadowHit(final Ray ray, final Stack stack) {
		double t = (p.sub(ray.origin)).dot(normal) / ray.direction.dot(normal);

		if (t > EPSILON) {
			return t;
		} else {
			return Double.NEGATIVE_INFINITY;
		}
	}

	@Override
	public AABB createBoundingBox() {
		final double v = Double.MAX_VALUE;
		return new AABB(new Point3d(-v, v, EPSILON), new Point3d(v, -v, EPSILON));
	}
	
	public double distanceFromRayOrigin(Ray ray) {
		return (p.sub(ray.origin)).dot(normal) / ray.direction.dot(normal);
	}
	
}
