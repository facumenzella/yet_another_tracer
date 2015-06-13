package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Normal3d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Plane extends GeometricObject {

	public Point3d p;
	public Vector3d normal;

	public Plane(final Point3d p, final Vector3d normal) {
		this.p = p;
		this.normal = normal;
		updateBoundingBox();
	}

	public Plane() {
		this(new Point3d(0, 0, 0), new Normal3d(0, 0, 1));
	}

	@Override
	public double hit(final Ray ray, final ShadeRec sr, final double tMax, final Stack stack) {
		// p.sub(ray.origin)
		double dx = p.x - ray.origin.x;
		double dy = p.y - ray.origin.y;
		double dz = p.z - ray.origin.z;
		// (p.sub(ray.origin)).dot(normal)
		dx = dx * normal.x;
		dy = dy * normal.y;
		dz = dz * normal.z;

		// ray.direction.dot(normal);
		final double denominator = ray.direction[0] * normal.x + ray.direction[1]
				* normal.y + ray.direction[2] * normal.z;
		double t = (dx + dy + dz) / denominator;

		if (t > EPSILON) {
			sr.normal = normal;
//			ray.origin.add(ray.direction.scale(t));
			double x = ray.origin.x + (ray.direction[0] * t);
			double y = ray.origin.y + (ray.direction[1] * t);
			double z = ray.origin.z + (ray.direction[2] * t);
			
			sr.localHitPoint = new Point3d(x, y, z);
			return t;
		} else {
			return Double.NEGATIVE_INFINITY;
		}
	}

	@Override
	public double shadowHit(final Ray ray, final double tMax,final Stack stack) {
		// double t = (p.sub(ray.origin)).dot(normal) /
		// ray.direction.dot(normal);

		// p.sub(ray.origin)
		double dx = p.x - ray.origin.x;
		double dy = p.y - ray.origin.y;
		double dz = p.z - ray.origin.z;
		// (p.sub(ray.origin)).dot(normal)
		dx = dx * normal.x;
		dy = dy * normal.y;
		dz = dz * normal.z;

		// ray.direction.dot(normal);
		final double denominator = ray.direction[0] * normal.x + ray.direction[1]
				* normal.y + ray.direction[2] * normal.z;

		double t = (dx + dy + dz) / denominator;

		if (t > EPSILON) {
			return t;
		} else {
			return Double.NEGATIVE_INFINITY;
		}
	}

	@Override
	public AABB createBoundingBox() {
		final double v = Double.MAX_VALUE;
		return new AABB(new Point3d(-v, v, EPSILON),
				new Point3d(v, -v, EPSILON));
	}

	@Override
	public boolean isFinite() {
		return false;
	}

}
