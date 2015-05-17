package ar.edu.itba.it.cg.yart.raytracer.tracer;

import java.util.List;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public interface HitTracer {
	
	public double traceRayHit(final Ray ray, List<GeometricObject> objects, final ShadeRec sr, final double tMax);

}
