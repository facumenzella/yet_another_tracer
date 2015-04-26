package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.HitTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleHitTracer;

public class PointLight extends LightAbstract {

	private final double ls;
	private final Color color;
	private final Vector3d location;
	private final Point3 point;
	private final HitTracer tracer;

	public PointLight(final double ls, final Color color,
			final Vector3d location) {
		super();
		this.ls = ls;
		this.color = color;
		this.location = location;
		this.point = new Point3(location.x, location.y, location.z);
		this.tracer = new SimpleHitTracer();
	}

	@Override
	public Vector3d getDirection(final ShadeRec sr) {
		return location.sub(sr.hitPoint).normalizedVector();
	}

	@Override
	public Color L(final ShadeRec sr) {
		return color.multiply(ls);

	}

	@Override
	public boolean inShadow(final Ray ray, final ShadeRec sr) {
		double t;
		final double d = point.distance(ray.origin);

		t = sr.world.getTree().traceRayHit(ray, tracer);
		if (t != Double.NEGATIVE_INFINITY && t < d) {
			return true;
		}
		return false;
	}
}
