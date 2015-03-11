package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.raytracer.Ray;

public class Plane extends GeometricObject {
	
	private Point3 p;
	private Vector3d normal;
	
	public Plane(final Point3 p, final Vector3d normal) {
		this.p = p;
		this.normal = normal;
	}
	
	@Override
	public double hit(final Ray ray) {
		double t = (p.sub(ray.origin)).dot(normal) / ray.direction.dot(normal);
		
		if (t > EPSILON) {
			return t;
		}
		else {
			return Double.NEGATIVE_INFINITY;
		}
	}

}
