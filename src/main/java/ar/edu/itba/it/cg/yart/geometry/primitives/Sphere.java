package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.MutableVector3d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
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

	public Sphere() {
		this.center = new Point3d(0, 0, 0);
		this.radius = 1;
		updateBoundingBox();
	}

	@Override
	public double hit(final Ray ray, final ShadeRec sr, final Stack stack) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}

		// MutableVector3d tmp = ray.origin.mutableSub(center);
		// MutableVector3d rayTmp = new MutableVector3d(ray.direction);
		double tx = ray.origin.x - center.x;
		double ty = ray.origin.y - center.y;
		double tz = ray.origin.z - center.z;

		// ray.direction.dot(ray.direction);

		double a = ray.direction[0] * ray.direction[0] + ray.direction[1]
				* ray.direction[1] + ray.direction[2] * ray.direction[2];
		double b = 2.0 * tx * ray.direction[0] + ty * ray.direction[1] + tz
				* ray.direction[2];
		double c = tx * tx + ty * ty + tz * tz - radius * radius;
		double disc = b * b - 4.0 * a * c;

		if (disc < 0.0) { // No hit
			return Double.NEGATIVE_INFINITY;
		}

		double e = Math.sqrt(disc);
		double denom = 2.0 * a;
		double t;

		t = (-b - e) / denom;

		if (t > EPSILON) {
			double rx = ray.direction[0] * t;
			double ry = ray.direction[1] * t;
			double rz = ray.direction[2] * t;

			tx = (tx + rx) / radius;
			ty = (ty + ry) / radius;
			rz = (tz + rz) / radius;

			final double length = Math.sqrt(tx*tx + ty*ty + tz*tz);
			sr.normal = new Vector3d(tx / length, ty / length, tz / length);
			double x = ray.origin.x + (ray.direction[0] * t);
			double y = ray.origin.y + (ray.direction[1] * t);
			double z = ray.origin.z + (ray.direction[2] * t);

			sr.localHitPoint = new Point3d(x, y, z);
			return t;
		}

		t = (-b + e) / denom;

		if (t > EPSILON) {
			double rx = ray.direction[0] * t;
			double ry = ray.direction[1] * t;
			double rz = ray.direction[2] * t;

			tx = (tx + rx) / radius;
			ty = (ty + ry) / radius;
			rz = (tz + rz) / radius;
			final double length = Math.sqrt(tx*tx + ty*ty + tz*tz);
			sr.normal = new Vector3d(tx / length, ty / length, tz / length);
			// ray.origin.add(ray.direction.scale(t));
			double x = ray.origin.x + (ray.direction[0] * t);
			double y = ray.origin.y + (ray.direction[1] * t);
			double z = ray.origin.z + (ray.direction[2] * t);
			sr.localHitPoint = new Point3d(x, y, z);
			return t;
		}

		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public double shadowHit(final Ray ray, final Stack stack) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}

		// MutableVector3d tmp = ray.origin.mutableSub(center);
		// MutableVector3d rayTmp = new MutableVector3d(ray.direction);
		double tx = ray.origin.x - center.x;
		double ty = ray.origin.y - center.y;
		double tz = ray.origin.z - center.z;

		// ray.direction.dot(ray.direction);

		double a = ray.direction[0] * ray.direction[0] + ray.direction[1]
				* ray.direction[1] + ray.direction[2] * ray.direction[2];
		double b = 2.0 * tx * ray.direction[0] + ty * ray.direction[1] + tz
				* ray.direction[2];
		double c = tx * tx + ty * ty + tz * tz - radius * radius;
		double disc = b * b - 4.0 * a * c;

		if (disc < 0.0) { // No hit
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
	public AABB createBoundingBox() {
		return new AABB(new Point3d(center.x - radius, center.y + radius,
				center.z - radius), new Point3d(center.x + radius, center.y
				- radius, center.z + radius));
	}

}
