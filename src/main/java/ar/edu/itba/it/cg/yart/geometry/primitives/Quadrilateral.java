package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Normal3d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public class Quadrilateral extends GeometricObject {

	private Point3d corner; // corner of rectangle
	private Vector3d sideA; // this are the sides, they should not be paralel
							// with this implementation you can create any
							// quadrilateral
							// not just a rectangle.
	private Vector3d sideB;
	private double sideAlenghtSquared;
	private double sideBlenghtSquared;
	private Normal3d normal;

	// for area lights

	// private double area;
	// private double invArea;

	public Quadrilateral() {
		final Point3d corner = new Point3d(0, 0, 0);
		final Vector3d sideA = new Vector3d(1, 0, 0);
		final Vector3d sideB = new Vector3d(0, 1, 0);
		this.corner = corner;
		this.sideA = sideA;
		this.sideB = sideB;
		this.normal = sideA.cross(sideB).normalizedVector();
		sideAlenghtSquared = sideA.length * sideA.length;
		sideBlenghtSquared = sideB.length * sideB.length;
		updateBoundingBox();

	}

	public Quadrilateral(final Point3d corner, final Vector3d sideA,
			final Vector3d sideB) {
		this.corner = corner;
		this.sideA = sideA;
		this.sideB = sideB;
		this.normal = sideA.cross(sideB).normalizedVector();
		sideAlenghtSquared = sideA.length * sideA.length;
		sideBlenghtSquared = sideB.length * sideB.length;
		updateBoundingBox();
	}

	@Override
	public AABB createBoundingBox() {
		final double p1x = Math.min(corner.x, corner.x + sideA.x + sideB.x)
				- EPSILON;
		final double p2x = Math.max(corner.x, corner.x + sideA.x + sideB.x)
				+ EPSILON;
		final double p1y = Math.min(corner.y, corner.y + sideA.y + sideB.y)
				- EPSILON;
		final double p2y = Math.max(corner.y, corner.y + sideA.y + sideB.y)
				+ EPSILON;
		final double p1z = EPSILON;
		final double p2z = EPSILON;

		final Point3d p1 = new Point3d(p1x, p2y, p1z);
		final Point3d p2 = new Point3d(p2x, p1y, p2z);

		return new AABB(p1, p2);
	}

	@Override
	public double hit(Ray ray, ShadeRec sr, final double tMax, final Stack stack) {

		// p.sub(ray.origin)
		double dx = corner.x - ray.origin.x;
		double dy = corner.y - ray.origin.y;
		double dz = corner.z - ray.origin.z;
		// (p.sub(ray.origin)).dot(normal)
		dx = dx * normal.x;
		dy = dy * normal.y;
		dz = dz * normal.z;

		// ray.direction.dot(normal);
		final double denominator = ray.direction[0] * normal.x
				+ ray.direction[1] * normal.y + ray.direction[2] * normal.z;
		double t = (dx + dy + dz) / denominator;

		if (t <= EPSILON) {
			return Double.NEGATIVE_INFINITY;
		}
		// ray.origin.add(ray.direction.scale(t));
		double x = ray.origin.x + (ray.direction[0] * t);
		double y = ray.origin.y + (ray.direction[1] * t);
		double z = ray.origin.z + (ray.direction[2] * t);
		final Point3d p = new Point3d(x, y, z);

		final Vector3d d = p.sub(corner);

		double ddota = d.dot(sideA);

		if (ddota < 0.0 || ddota > sideAlenghtSquared)
			return Double.NEGATIVE_INFINITY;

		double ddotb = d.dot(sideB);

		if (ddotb < 0.0 || ddotb > sideBlenghtSquared) {
			return Double.NEGATIVE_INFINITY;
		}

		sr.normal = normal;
		sr.localHitPoint = p;
		return t;
	}

	@Override
	public double shadowHit(Ray ray, final double tMax,final Stack stack) {

		// p.sub(ray.origin)
		double dx = corner.x - ray.origin.x;
		double dy = corner.y - ray.origin.y;
		double dz = corner.z - ray.origin.z;
		// (p.sub(ray.origin)).dot(normal)
		dx = dx * normal.x;
		dy = dy * normal.y;
		dz = dz * normal.z;

		// ray.direction.dot(normal);
		final double denominator = ray.direction[0] * normal.x
				+ ray.direction[1] * normal.y + ray.direction[2] * normal.z;
		double t = (dx + dy + dz) / denominator;

		if (t <= EPSILON) {
			return Double.NEGATIVE_INFINITY;
		}

		// ray.origin.add(ray.direction.scale(t));
		double x = ray.origin.x + (ray.direction[0] * t);
		double y = ray.origin.y + (ray.direction[1] * t);
		double z = ray.origin.z + (ray.direction[2] * t);
		final Point3d p = new Point3d(x, y, z);
		final Vector3d d = p.sub(corner);

		double ddota = d.dot(sideA);

		if (ddota < 0.0 || ddota > sideAlenghtSquared)
			return Double.NEGATIVE_INFINITY;

		double ddotb = d.dot(sideB);

		if (ddotb < 0.0 || ddotb > sideBlenghtSquared) {
			return Double.NEGATIVE_INFINITY;
		}

		return t;
	}

}
