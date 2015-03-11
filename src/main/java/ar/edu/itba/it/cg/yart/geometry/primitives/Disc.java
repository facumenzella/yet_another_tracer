package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;

public class Disc extends Plane{
	
	private final double r;
	
	public Disc(final Point3 center, final Vector3d normal, final double raidus) {
		super(center ,normal);
		this.r = raidus;
	}
	
	@Override
	public double hit(final Ray ray) {
		double t = super.hit(ray);
		if (t<= EPSILON) {
			return Double.NEGATIVE_INFINITY;
		}
		final double dx = ray.direction.x;
		final double dy = ray.direction.y;
		final double dz = ray.direction.z;
		final Point3 tmp = new Point3(t*dx, t*dy, t*dz);
		final Point3 intersection = ray.origin.add(tmp);
		
		final double aux = intersection.sub(p).dot(intersection.sub(p));
		
		if (aux <= r*r) {
			return t;
		}
		return Double.NEGATIVE_INFINITY;
	}
}
