package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.acceleration_estructures.Side;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Plane extends GeometricObject {

	protected Point3 p;
	private Vector3d normal;

	public Plane(final Point3 p, final Vector3d normal) {
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
	
	public Side sideOfPoint(final Point3 point) {
		// check if the point belongs to the plane
		final double belongs = (normal.x - point.x) + (normal.y - point.y) + (normal.z - point.z);
		if (belongs == 0) {
			return Side.INTERSECTION;
		}
		
		// We take the inner product and check the sign of it
		Vector3d unitNormal = this.normal.normalized;
		Vector3d vectorToPoint = this.p.sub(point);
		
		final double dot = unitNormal.dot(vectorToPoint);
		// We arbitraty define >= 0 as RIGHT and <0 as LEFT
		if (dot >= 0) {
			 return Side.RIGHT;
		} else {
			return Side.LEFT;
		}
		
	}

	@Override
	public Side sideOfPlane(Plane plane) {
		Vector3d aNormal = plane.normal.normalized;
		Vector3d myNormal = this.normal.normalized;
		
		
		
		
		
	}

}
