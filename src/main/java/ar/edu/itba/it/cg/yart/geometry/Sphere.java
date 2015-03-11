package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.raytracer.Ray;

public class Sphere extends GeometricObject {
	
	private Point3 center;
	private double radius;
	
	public Sphere(final Point3 center, final double radius) {
		this.center = center;
		this.radius = radius;
	}

	@Override
	public double hit(final Ray ray) {
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
