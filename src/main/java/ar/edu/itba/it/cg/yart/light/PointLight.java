package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.HitTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleHitTracer;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class PointLight extends AbstractLight {

	private final double ls;
	private final Color color;
	private Vector3d location;
	private final Point3 point;
	private final HitTracer tracer;

	private Color L;
	
	public PointLight(final double ls, final Color color,
			final Vector3d location) {
		super();
		this.ls = ls;
		this.color = color;
		this.location = location;
		this.point = new Point3(location.x, location.y, location.z);
		this.tracer = new SimpleHitTracer();
		this.L = this.mL(null);
	}

	@Override
	public Vector3d getDirection(final ShadeRec sr) {
		return location.sub(sr.hitPoint).normalizedVector();
	}

	@Override
	public Color L(final ShadeRec sr) {
		return this.L;
	}
	
	public Color mL(final ShadeRec sr) {
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

	@Override
	public void applyTransformation(Matrix4d matrix) {
		this.location = this.location.transformByMatrix(matrix);
	}
}
