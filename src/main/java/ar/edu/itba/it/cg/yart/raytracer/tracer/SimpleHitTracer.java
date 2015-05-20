package ar.edu.itba.it.cg.yart.raytracer.tracer;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class SimpleHitTracer extends AbstractTracer implements HitTracer {

	@Override
	public double traceRayHit(Ray ray, List<GeometricObject> objects,
			ShadeRec sr, double tMax) {
		
		if (objects == null || objects.size() == 0) {
			return Double.NEGATIVE_INFINITY;
		}
		
		double tMin = tMax;
		boolean hit = false;
		Vector3d normal = null;
		Point3d localHitPoint = null;
		GeometricObject object = null;
		for (int i = 0; i < objects.size(); i++) {
			object = objects.get(i);
			double t = object.hit(ray, sr);
			if (t != Double.NEGATIVE_INFINITY && t < tMin) {
				tMin = t;
				normal = sr.normal;
				localHitPoint = sr.localHitPoint;
				hit = true;
			}
		}
		if (hit) {
			sr.normal = normal;
			sr.localHitPoint = localHitPoint;
			return tMin;
		}
		return Double.NEGATIVE_INFINITY;
	}

}
