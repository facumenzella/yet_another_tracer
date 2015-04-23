package ar.edu.itba.it.cg.yart.raytracer.tracer;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;

public class SimpleHitTracer extends AbstractTracer implements HitTracer{

	@Override
	public double traceRayHit(final Ray ray, final List<GeometricObject> objects, final double tMax) {
		if (ray.depth >= MAX_DEPTH) {
			return Double.NEGATIVE_INFINITY;
		}
		double tMin = tMax;
		boolean hit = false;
		for (int i = 0; i < objects.size(); i++) {
			GeometricObject object = objects.get(i);
			double t = object.shadowHit(ray);
			if (t != Double.NEGATIVE_INFINITY && t < tMin) {
				tMin = t;
				hit = true;
			}
		}
		if (hit) {
			return tMin;
		}
		return Double.NEGATIVE_INFINITY;
	}

}
