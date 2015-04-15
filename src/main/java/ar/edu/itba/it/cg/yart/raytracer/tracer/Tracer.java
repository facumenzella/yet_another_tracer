package ar.edu.itba.it.cg.yart.raytracer.tracer;

import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public interface Tracer {
		
	public Color traceRay(final Ray ray, List<GeometricObject> objects, final ShadeRec sr, final double tMax);
	public boolean hitObject();

}
