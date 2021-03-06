package ar.edu.itba.it.cg.yart.geometry.primitives;

import java.util.concurrent.ThreadLocalRandom;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.samplers.Sample;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

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
	public double hit(final Ray ray, final ShadeRec sr, final double tMax, final Stack stack) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}
		double tMin;
		double t;
		double tx = ray.origin.x - center.x;
		double ty = ray.origin.y - center.y;
		double tz = ray.origin.z - center.z;

		double a = (ray.direction[0] * ray.direction[0])
				+ (ray.direction[1] * ray.direction[1])
				+ (ray.direction[2] * ray.direction[2]);
		double b = 2.0 * (tx * ray.direction[0] + ty * ray.direction[1] + tz
				* ray.direction[2]);
		double c = (tx * tx + ty * ty + tz * tz) - radius * radius;
		double disc = b * b - 4.0 * a * c;

		if (disc < 0.0)
			return Double.NEGATIVE_INFINITY;
		else {
			double e = Math.sqrt(disc);
			double denom = 2.0 * a;
			t = (-b - e) / denom; // smaller root

			if (t > EPSILON) {
				tMin = t;

				double nx = tx + (ray.direction[0] * t);
				double ny = ty + (ray.direction[1] * t);
				double nz = tz + (ray.direction[2] * t);
				nx = nx / radius;
				ny = ny / radius;
				nz = nz / radius;
				sr.normal = new Vector3d(nx, ny, nz);
				double x = ray.origin.x + (ray.direction[0] * t);
				double y = ray.origin.y + (ray.direction[1] * t);
				double z = ray.origin.z + (ray.direction[2] * t);

				sr.localHitPoint = new Point3d(x, y, z);
				return tMin;
			}

			t = (-b + e) / denom; // larger root

			if (t > EPSILON) {
				tMin = t;

				double nx = tx + (ray.direction[0] * t);
				double ny = ty + (ray.direction[1] * t);
				double nz = tz + (ray.direction[2] * t);
				nx = nx / radius;
				ny = ny / radius;
				nz = nz / radius;
				sr.normal = new Vector3d(nx, ny, nz);
				double x = ray.origin.x + (ray.direction[0] * t);
				double y = ray.origin.y + (ray.direction[1] * t);
				double z = ray.origin.z + (ray.direction[2] * t);

				sr.localHitPoint = new Point3d(x, y, z);
				return tMin;
			}
		}

		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public double shadowHit(final Ray ray, final double tMax,final Stack stack) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}
		double tMin;
		double t;
		double tx = ray.origin.x - center.x;
		double ty = ray.origin.y - center.y;
		double tz = ray.origin.z - center.z;

		double a = (ray.direction[0] * ray.direction[0])
				+ (ray.direction[1] * ray.direction[1])
				+ (ray.direction[2] * ray.direction[2]);
		double b = 2.0 * (tx * ray.direction[0] + ty * ray.direction[1] + tz
				* ray.direction[2]);
		double c = (tx * tx + ty * ty + tz * tz) - radius * radius;
		double disc = b * b - 4.0 * a * c;

		if (disc < 0.0)
			return Double.NEGATIVE_INFINITY;
		else {
			double e = Math.sqrt(disc);
			double denom = 2.0 * a;
			t = (-b - e) / denom; // smaller root

			if (t > EPSILON) {
				tMin = t;

				return tMin;
			}

			t = (-b + e) / denom; // larger root

			if (t > EPSILON) {
				tMin = t;
				return tMin;
			}
		}

		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public AABB createBoundingBox() {
		return new AABB(new Point3d(center.x - radius, center.y + radius,
				center.z - radius), new Point3d(center.x + radius, center.y
				- radius, center.z + radius));
	}
	
	@Override
	public Sample getSample() {
		final double theta = 2 * Math.PI * ThreadLocalRandom.current().nextDouble();
		final double phi = Math.acos(2 * ThreadLocalRandom.current().nextDouble() -1.0);
		
		final double x = Math.cos(theta) * Math.sin(phi);
		final double y = Math.sin(theta) * Math.sin(phi);
		final double z = Math.cos(phi);
		
		final Point3d point = new Point3d(x, y, z);
		
		return new Sample(point, new Vector3d(point.x, point.y, point.z, 1));
	}
	
	@Override
	public double pdf() {
		return 1 / (4 * Math.PI * radius * radius);
	}

}
