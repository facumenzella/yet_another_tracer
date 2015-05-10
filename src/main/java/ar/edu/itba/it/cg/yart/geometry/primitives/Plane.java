package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Plane extends GeometricObject {

	protected Point3d p;
	private Vector3d normal;

	public Plane(final Point3d p, final Vector3d normal) {
		this.p = p;
		this.normal = normal;
		updateBoundingBox();
	}

	@Override
	public double hit(final Ray ray, final ShadeRec sr) {		
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
	public double shadowHit(final Ray ray) {
		double t = (p.sub(ray.origin)).dot(normal) / ray.direction.dot(normal);
		if (t > EPSILON) {
			return t;
		} else {
			return Double.NEGATIVE_INFINITY;
		}
	}

	@Override
	public BoundingBox createBoundingBox() {
		// Infinite plane has no Bounding Box
		return null;
	}
	
	public double distanceFromRayOrigin(Ray ray) {
		return (p.sub(ray.origin)).dot(normal) / ray.direction.dot(normal);
	}
	
}
