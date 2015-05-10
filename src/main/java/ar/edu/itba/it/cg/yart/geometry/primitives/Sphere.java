package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.geometry.MutableVector3d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Sphere extends GeometricObject {
	
	private Point3d center;
	private double radius;
	
	public Sphere(final Point3d center, final double radius) {
		this.center = center;
		this.radius = radius;
		updateBoundingBox();
	}

	@Override
	public double hit(final Ray aRay, final ShadeRec sr) {
		Ray ray = new Ray(aRay.origin);
		ray.direction = aRay.direction;
		if (transformed) {
			ray.origin = ray.origin.transformByMatrix(inverseMatrix);
			ray.direction = ray.direction.transformByMatrix(inverseMatrix);
		}
		
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}
		
		MutableVector3d tmp = ray.origin.mutableSub(center);
		MutableVector3d rayTmp = new MutableVector3d(ray.direction);
		
		double a = ray.direction.dot(ray.direction);
		double b = 2.0 * tmp.dot(ray.direction);
		double c = tmp.dot(tmp)  - radius * radius;
		double disc = b * b - 4.0 * a * c;
		
		if (disc < 0.0) {
			// No hit
			return Double.NEGATIVE_INFINITY;
		}
		
		double e = Math.sqrt(disc);
		double denom = 2.0 * a;
		double t;

		t = (-b - e) / denom;
		
		if (t > EPSILON) {
			rayTmp.scale(t);
			tmp.add(rayTmp);
			tmp.scale(1/radius);
			sr.normal = tmp.inmutableCopy();
			if (transformed) {
				sr.normal = sr.normal.transformByMatrix(transposedInvMatrix);
			}
			sr.localHitPoint = aRay.origin.add(aRay.direction.scale(t));
			return t;
		}
		
		t = (-b + e) / denom;
		
		if (t > EPSILON) {
			rayTmp.scale(t);
			tmp.add(rayTmp);
			tmp.scale(1/radius);
			sr.normal = tmp.inmutableCopy();
			if (transformed) {
				sr.normal = sr.normal.transformByMatrix(transposedInvMatrix);
			}
			sr.localHitPoint = aRay.origin.add(aRay.direction.scale(t));
			return t;
		}
		
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public double shadowHit(final Ray aRay) {
		Ray ray = new Ray(aRay.origin);
		ray.direction = aRay.direction;
		if (transformed) {
			ray.origin = ray.origin.transformByMatrix(inverseMatrix);
			ray.direction = ray.direction.transformByMatrix(inverseMatrix);
		}
		
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}
		
		MutableVector3d tmp = ray.origin.mutableSub(center);
		double a = ray.direction.dot(ray.direction);
		double b = 2.0 * tmp.dot(ray.direction);
		double c = tmp.dot(tmp)  - radius * radius;
		double disc = b * b - 4.0 * a * c;
		
		if (disc < 0.0) {
			// No hit
			return Double.NEGATIVE_INFINITY;
		}
		
		double e = Math.sqrt(disc);
		double denom = 2.0 * a;
		
		double t;
		t = (-b - e) / denom;
		
		if (t > EPSILON) {
			return t;
		}
		
		t = (-b + e) / denom;
		
		if (t > EPSILON) {
			return t;
		}
		
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public BoundingBox createBoundingBox() {
		return new BoundingBox(new Point3d(center.x - radius, center.y - radius, center.z - radius),
				new Point3d(center.x + radius, center.y + radius, center.z + radius));
	}

}
