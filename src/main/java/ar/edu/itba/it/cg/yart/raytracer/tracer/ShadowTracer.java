package ar.edu.itba.it.cg.yart.raytracer.tracer;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public interface ShadowTracer {
	public double traceShadowHit(final Ray ray, final List<GeometricObject> objects, final double tMax);
}
