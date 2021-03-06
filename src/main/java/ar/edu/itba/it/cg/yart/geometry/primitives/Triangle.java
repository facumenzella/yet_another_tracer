package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public class Triangle extends GeometricObject{

	// the vertexes are order counterclockwise (see page 362 of Ray Tracing from the ground up)
	private final Point3d pointA,  pointB, pointC;
	private final Vector3d normal;
	
	public Triangle() {
		this.pointA = new Point3d(0, 0, 0);
		this.pointB = new Point3d(0, 0, 1);
		this.pointC = new Point3d(1,0,0);
		this.normal = this.normal(pointA, pointB, pointC);
		updateBoundingBox();
	}
	
	public Triangle(final Point3d pointA, final Point3d pointB, final Point3d pointC) {
		this.pointA = pointA;
		this.pointB = pointB;
		this.pointC = pointC;
		this.normal = this.normal(pointA, pointB, pointC);
		updateBoundingBox();
	}

	@Override
	public double hit(Ray ray, ShadeRec sr, final double tMax, final Stack stack) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}
		
		// This is too much
		// Check out Ray Tracing from the ground up (page 367)
		final double a = this.pointA.x - this.pointB.x, b = this.pointA.x - this.pointC.x;
		final double c = ray.direction[0],  d = this.pointA.x - ray.origin.x;
		final double e = this.pointA.y - this.pointB.y, f = this.pointA.y - this.pointC.y;
		final double g = ray.direction[1], h = this.pointA.y - ray.origin.y;
		final double i = this.pointA.z - this.pointB.z, j = this.pointA.z - this.pointC.z;
		final double k = ray.direction[2], l = this.pointA.z - ray.origin.z;
		
		final double m = (f * k) - (g * j), n = (h * k) - (g * l), p = (f * l) - (h * j);
		final double q = (g * i) - (e * k), s = (e * j) - (f * i);
		
		final double inv_denom = 1.0 / ((a * m) + (b * q) + (c * s));
		
		final double e1 = (d * m) - (b * n) - (c * p);
		final double beta = e1 * inv_denom;
		
		if (beta < 0) {
			return Double.NEGATIVE_INFINITY;
		}
		
		double r = (e * l) - (h * i);
		final double e2 = (a * n) + (d * q) + (c * r);
		final double gamma = e2 * inv_denom;
		
		if (gamma < 0) {
			return Double.NEGATIVE_INFINITY;
		}
		
		if (beta + gamma > 1) {
			return Double.NEGATIVE_INFINITY;
		}
		
		final double e3 = (a * p) - (b * r) + (d * s);
		final double t = e3 * inv_denom;
		
		if (t < EPSILON) {
			return Double.NEGATIVE_INFINITY;
		}
		sr.normal = normal;
//		ray.origin.add(ray.direction.scale(t));
		double x = ray.origin.x + (ray.direction[0] * t);
		double y = ray.origin.y + (ray.direction[1] * t);
		double z = ray.origin.z + (ray.direction[2] * t);
		
		sr.localHitPoint = new Point3d(x, y, z);
		return t;
	}
	
	private Vector3d normal(final Point3d pointA, final Point3d pointB, final Point3d pointC) {
		final Vector3d e0 = pointB.sub(pointA);
		final Vector3d e1 = pointC.sub(pointA);
		final Vector3d n = e0.cross(e1);
		
		final double length = n.length;
		
		return new Vector3d(n.x / length, n.y / length, n.z / length);
	}

	@Override
	public double shadowHit(final Ray ray, final double tMax,final Stack stack) {
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public AABB createBoundingBox() {
		return (new AABB(new Point3d(Math.min(
				Math.min(pointA.x, pointB.x), pointC.x) - EPSILON, Math.min(Math.min(pointA.y, pointB.y), pointC.y)
				- EPSILON, Math.min(Math.min(pointA.z, pointB.z), pointC.z)
				- EPSILON), 
				new Point3d(Math.max(Math.max(pointA.x, pointB.x),
				pointC.x) + EPSILON, Math.max(Math.max(pointA.y, pointB.y),
				pointC.y) + EPSILON, Math.max(Math.max(pointA.z, pointB.z),
				pointC.z) + EPSILON)));
	}
	
}
