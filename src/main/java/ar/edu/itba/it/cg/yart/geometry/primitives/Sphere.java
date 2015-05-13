package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.geometry.MutableVector3d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class Sphere extends GeometricObject {
	
	private Point3d center;
	private double radius;
	
	public Sphere(final Point3d center, final double radius) {
		this.center = center;
		this.radius = radius;
		updateBoundingBox();
	}
	
	public Sphere() {
		this.center = new Point3d(0, 0, 0);
		this.radius = 1;
		updateBoundingBox();
	}

	@Override
	public double hit(final Ray ray, final ShadeRec sr) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}
		
		MutableVector3d tmp = ray.origin.mutableSub(center);
		MutableVector3d rayTmp = new MutableVector3d(ray.direction);
		
		double a = ray.direction.dot(ray.direction);
		double b = 2.0 * tmp.dot(ray.direction);
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
			sr.localHitPoint = ray.origin.add(ray.direction.scale(t));
			return t;
		}
		
		t = (-b + e) / denom;
		
		if (t > EPSILON) {
			rayTmp.scale(t);
			tmp.add(rayTmp);
			tmp.scale(1/radius);
			sr.normal = tmp.inmutableCopy().normalizedVector();
			sr.localHitPoint = ray.origin.add(ray.direction.scale(t));
			return t;
		}
		
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public double shadowHit(final Ray ray) {
		// intersect the inverse transformed ray with the untransformed object
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}
		
		MutableVector3d tmp = ray.origin.mutableSub(center);
		MutableVector3d rayTmp = new MutableVector3d(ray.direction);
		
		double a = ray.direction.dot(ray.direction);
		double b = 2.0 * tmp.dot(ray.direction);
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

	@Override
	public void applyTransformation(Matrix4d matrix) {
		// TODO Auto-generated method stub
		
	}

}
