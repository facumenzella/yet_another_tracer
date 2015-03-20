package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Sphere extends GeometricObject {
	
	private Point3 center;
	private double radius;
	
	public Sphere(final Point3 center, final double radius) {
		this.center = center;
		this.radius = radius;
	}

	@Override
	public double hit(final Ray ray, final ShadeRec sr) {
		double t;
		Vector3d tmp = ray.origin.sub(center);
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
		
		t = (-b - e) / denom;
		
		if (t > EPSILON) {
			sr.normal = tmp.add(ray.direction.scale(t)).scale(1/radius);
			sr.localHitPoint = ray.origin.add(ray.direction.scale(t));
			return t;
		}
		
		t = (-b + e) / denom;
		
		if (t > EPSILON) {
			sr.normal = tmp.add(ray.direction.scale(t)).scale(1/radius);
			sr.localHitPoint = ray.origin.add(ray.direction.scale(t));
			return t;
		}
		
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public double shadowHit(final Ray ray) {
		double t;
		Vector3d tmp = ray.origin.sub(center);
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

}
