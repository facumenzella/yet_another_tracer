package ar.edu.itba.it.cg.yart.raytracer.tracer;

import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class SimpleColorTracer extends AbstractTracer implements ColorTracer {
	
	@Override
	public Color traceRay(final Ray ray, List<GeometricObject> objects, final ShadeRec sr, final double tMax) {
		
		if (ray.depth >= MAX_DEPTH) {
			return Color.blackColor();
		}
		Vector3d normal = null;
		Point3 localHitPoint = null;
		double tMin = tMax;
		for (int i = 0; i < objects.size(); i++) {
			GeometricObject object = objects.get(i);
			double t = object.hit(ray, sr);
			if (t != Double.NEGATIVE_INFINITY && t < tMin) {
				sr.hitObject = true;
				sr.material = object.getMaterial();
				sr.hitPoint = ray.origin.add(ray.direction.scale(t));
				normal = sr.normal;
				localHitPoint = sr.localHitPoint;
				tMin = t;
			}
		}

		if (sr.hitObject) {
			sr.depth = ray.depth;
			sr.t = tMin;
			sr.normal = normal;
			sr.localHitPoint = localHitPoint;
			sr.ray = ray;
			return sr.material.shade(sr);
		}
		
		return sr.world.getBackgroundColor();
	}
	
}
