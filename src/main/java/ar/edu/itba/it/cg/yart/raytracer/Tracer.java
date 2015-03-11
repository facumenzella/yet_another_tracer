package ar.edu.itba.it.cg.yart.raytracer;

import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public class Tracer {

	public Color traceRay(final Ray ray, List<GeometricObject> objects) {
		Color ret = null;
		double tMin = Double.POSITIVE_INFINITY;		
		
		for (int i = 0; i < objects.size(); i++) {
			GeometricObject object = objects.get(i);
			double t = object.hit(ray);
			if (t != Double.NEGATIVE_INFINITY && t < tMin) {
				ret = objects.get(i).color;
				tMin = t;
			}
		}
		
		return ret;
	}
	
}
