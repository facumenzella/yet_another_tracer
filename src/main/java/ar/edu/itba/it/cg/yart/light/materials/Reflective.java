package ar.edu.itba.it.cg.yart.light.materials;

import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.light.brdf.PerfectSpecular;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.Tracer;

public class Reflective extends Phong {

	private final PerfectSpecular reflectiveBRDF = new PerfectSpecular();

	public void setCr(final Color color) {
		reflectiveBRDF.setCr(color);
	}

	public void setKr(final double kr) {
		reflectiveBRDF.setKr(kr);
	}

	@Override
	public Color shade(ShadeRec sr) {
		
		Color colorL = super.shade(sr);
		Vector3d wo = sr.ray.direction.inverse();
		Vector3d wi = new Vector3d(0,0,0);
		Color fr = reflectiveBRDF.sample_f(sr, wo, wi);
		Ray reflectedRay = new Ray(sr.hitPoint, wi);
		reflectedRay.depth = sr.depth + 1;
		List<GeometricObject> objects = sr.world.getObjects();
		final Tracer tracer = sr.world.getActiveCamera().getTracer();
		colorL.addEquals(fr.multiply(tracer.traceRay(reflectedRay, objects, sr.world)).multiply(sr.normal.dot(wi)));
		
		return colorL;
	}
}
