package ar.edu.itba.it.cg.yart.raytracer;

import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public class Tracer {

	public ShadeRec traceRay(final Ray ray, List<GeometricObject> objects, ShadeRec sr) {
		Vector3d normal = null;
		Point3 localHitPoint = null;
		double tMin = Double.POSITIVE_INFINITY;		
		
		for (int i = 0; i < objects.size(); i++) {
			GeometricObject object = objects.get(i);
			double t = object.hit(ray, sr);
			if (t != Double.NEGATIVE_INFINITY && t < tMin) {
				sr.hitObject = true;
				sr.material = objects.get(i).getMaterial();
				sr.hitPoint = ray.origin.add(ray.direction.scale(t));
				normal = sr.normal;
				localHitPoint = sr.localHitPoint;
				tMin = t;
			}
		}
		if (sr.hitObject) {
			sr.t = tMin;
			sr.normal = normal;
			sr.localHitPoint = localHitPoint;
		}
		
		return sr;
	}
	
}
