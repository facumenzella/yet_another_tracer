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
	public double hit(final Ray ray, final ShadeRec sr) {
		Ray invRay = new Ray(ray.origin);
		invRay.direction = ray.direction;
		
		// apply the inverse set of transformations to the ray to produce an inverse transformed ray
		if (transformed) {
			invRay.origin = ray.origin.transformByMatrix(inverseMatrix);
			invRay.direction = ray.direction.transformByMatrix(inverseMatrix);
		}
		
		// intersect the inverse transformed ray with the untransformed object
//		if (!getBoundingBox().hit(invRay)) {
//			return Double.NEGATIVE_INFINITY;
//		}
		
		MutableVector3d tmp = invRay.origin.mutableSub(center);
		MutableVector3d rayTmp = new MutableVector3d(invRay.direction);
		
		double a = invRay.direction.dot(invRay.direction);
		double b = 2.0 * tmp.dot(invRay.direction);
		double c = tmp.dot(tmp)  - radius * radius;
		double disc = b * b - 4.0 * a * c;
		
		if (disc < 0.0) { // No hit
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
			sr.normal = tmp.inmutableCopy().normalizedVector();
			if (transformed) {
				sr.normal = sr.normal.transformByMatrix(transposedInvMatrix).normalizedVector();
			}
			sr.localHitPoint = invRay.origin.add(invRay.direction.scale(t));
			return t;
		}
		
		t = (-b + e) / denom;
		
		if (t > EPSILON) {
			rayTmp.scale(t);
			tmp.add(rayTmp);
			tmp.scale(1/radius);
			sr.normal = tmp.inmutableCopy().normalizedVector();
			if (transformed) {
				sr.normal = sr.normal.transformByMatrix(transposedInvMatrix).normalizedVector();
			}
			sr.localHitPoint = invRay.origin.add(invRay.direction.scale(t));
			return t;
		}
		
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public double shadowHit(final Ray ray) {
		Ray invRay = new Ray(ray.origin);
		invRay.direction = ray.direction;
		
		// apply the inverse set of transformations to the ray to produce an inverse transformed ray
		if (transformed) {
			invRay.origin = ray.origin.transformByMatrix(inverseMatrix);
			invRay.direction = ray.direction.transformByMatrix(inverseMatrix);
		}
		
		// intersect the inverse transformed ray with the untransformed object
//		if (!getBoundingBox().hit(invRay)) {
//			return Double.NEGATIVE_INFINITY;
//		}
		
		MutableVector3d tmp = invRay.origin.mutableSub(center);
		MutableVector3d rayTmp = new MutableVector3d(invRay.direction);
		
		double a = invRay.direction.dot(invRay.direction);
		double b = 2.0 * tmp.dot(invRay.direction);
		double c = tmp.dot(tmp)  - radius * radius;
		double disc = b * b - 4.0 * a * c;
		
		if (disc < 0.0) { // No hit
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
		}
		
		t = (-b + e) / denom;
		
		if (t > EPSILON) {
			rayTmp.scale(t);
			tmp.add(rayTmp);
			tmp.scale(1/radius);
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
