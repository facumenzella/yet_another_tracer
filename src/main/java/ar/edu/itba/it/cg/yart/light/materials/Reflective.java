package ar.edu.itba.it.cg.yart.light.materials;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.brdf.PerfectSpecular;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.ColorTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleColorTracer;

public class Reflective extends Phong {

	private final PerfectSpecular reflectiveBRDF = new PerfectSpecular();
	private final ColorTracer tracer = new SimpleColorTracer();
	
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

		Color c = sr.world.getTree().traceRay(reflectedRay, tracer, new ShadeRec(sr.world));
		fr.multiplyEquals(c);
		fr.multiplyEquals(sr.normal.dot(wi));
		colorL.addEquals(fr);
		
		return colorL;
	}
}
