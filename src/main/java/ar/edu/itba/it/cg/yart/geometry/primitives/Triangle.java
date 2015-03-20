package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Triangle extends GeometricObject{

	// the vertexes are order counterclockwise (see page 362 of Ray Tracing from the ground up)
	private final Point3 pointA,  pointB, pointC;
	private final Vector3d normal;
	
	public Triangle() {
		this.pointA = new Point3(0, 0, 0);
		this.pointB = new Point3(0, 0, 1);
		this.pointC = new Point3(1,0,0);
		this.normal = this.normal(pointA, pointB, pointC);
	}
	
	public Triangle(final Point3 pointA, final Point3 pointB, final Point3 pointC) {
		this.pointA = pointA;
		this.pointB = pointB;
		this.pointC = pointC;
		this.normal = this.normal(pointA, pointB, pointC);
	}

	@Override
	public double hit(Ray ray, ShadeRec sr) {
		// This is too much
		// Check out Ray Tracing from the ground up (page 367)
		final double a = this.pointA.x - this.pointB.x, b = this.pointA.x - this.pointC.x;
		final double c = ray.direction.x,  d = this.pointA.x - ray.origin.x;
		final double e = this.pointA.y - this.pointB.y, f = this.pointA.y - this.pointC.y;
		final double g = ray.direction.y, h = this.pointA.y - ray.origin.y;
		final double i = this.pointA.z - this.pointB.z, j = this.pointA.z - this.pointC.z;
		final double k = ray.direction.z, l = this.pointA.z - ray.origin.z;
		
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
		sr.localHitPoint = ray.origin.add(ray.direction.scale(t));
		return t;
	}
	
	private Vector3d normal(final Point3 pointA, final Point3 pointB, final Point3 pointC) {
		final Vector3d e0 = pointB.sub(pointA);
		final Vector3d e1 = pointC.sub(pointA);
		final Vector3d n = e0.cross(e1);
		
		final double length = n.length;
		
		return new Vector3d(n.x / length, n.y / length, n.z / length);
	}

	@Override
	public double shadowHit(final Ray ray) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
