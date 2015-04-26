package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.geometry.MutableVector3d;
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
		updateBoundingBox();
	}

	@Override
	public double hit(final Ray ray, final ShadeRec sr) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}
		
		double t;
		MutableVector3d tmp = new MutableVector3d(ray.origin.sub(center));
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
		
		t = (-b - e) / denom;
		
		if (t > EPSILON) {
			rayTmp.scale(t);
			tmp.add(rayTmp);
			tmp.scale(1/radius);
			sr.normal = tmp.inmutableCopy();
			sr.localHitPoint = ray.origin.add(rayTmp.inmutableCopy());
			return t;
		}
		
		t = (-b + e) / denom;
		
		if (t > EPSILON) {
			rayTmp.scale(t);
			tmp.add(rayTmp);
			tmp.scale(1/radius);
			sr.normal = tmp.inmutableCopy();
			sr.localHitPoint = ray.origin.add(rayTmp.inmutableCopy());
			return t;
		}
		
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public double shadowHit(final Ray ray) {
		double t;
		MutableVector3d tmp = new MutableVector3d(ray.origin.sub(center));
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

	@Override
	public BoundingBox createBoundingBox() {
		return new BoundingBox(new Point3(center.x - radius, center.y - radius, center.z - radius),
				new Point3(center.x + radius, center.y + radius, center.z + radius));
	}

}
