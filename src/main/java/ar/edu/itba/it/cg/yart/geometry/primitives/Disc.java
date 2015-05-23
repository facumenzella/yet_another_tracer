package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Disc extends Plane{
	
	private final double r;
	
	public Disc(final Point3d center, final Vector3d normal, final double raidus) {
		super(center ,normal);
		this.r = raidus;
	}
	
	@Override
	public double hit(final Ray ray, ShadeRec rs, final Stack stack) {
		double t = super.hit(ray, rs, stack);
		if (t<= EPSILON) {
			return Double.NEGATIVE_INFINITY;
		}
		final double dx = ray.direction.x;
		final double dy = ray.direction.y;
		final double dz = ray.direction.z;
		final Point3d tmp = new Point3d(t*dx, t*dy, t*dz);
		final Point3d intersection = ray.origin.add(tmp);
		
		final double aux = intersection.sub(p).dot(intersection.sub(p));
		
		if (aux <= r*r) {
			return t;
		}
		return Double.NEGATIVE_INFINITY;
	}
}
