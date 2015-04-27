package ar.edu.itba.it.cg.yart.light.materials;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.brdf.PerfectSpecular;
import ar.edu.itba.it.cg.yart.light.btdf.PerfectTransmitter;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.ColorTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleColorTracer;

public class Transparent extends Phong {

	private final PerfectSpecular reflectiveBRDF = new PerfectSpecular();
	private final PerfectTransmitter specularBTDF = new PerfectTransmitter();
	private final ColorTracer tracer = new SimpleColorTracer();

	public void setCr(final Color color) {
		reflectiveBRDF.setCr(color);
	}

	public void setKr(final double kr) {
		reflectiveBRDF.setKr(kr);
	}

	public void setIor(final double ior) {
		specularBTDF.setIor(ior);
	}

	public void setKt(final double kt) {
		specularBTDF.setKt(kt);
	}

	@Override
	public Color shade(ShadeRec sr) {
		Color colorL = super.shade(sr);
		final Vector3d wo = sr.ray.direction.inverse();
		final Vector3d wi = new Vector3d(0, 0, 0);
//		final Color fr = reflectiveBRDF.sample_f(sr, wo, wi);
		final Ray reflectedRay = new Ray(sr.hitPoint, wi);
		reflectedRay.depth = sr.depth + 1;
		if (specularBTDF.tir(sr)) {
			Color c = sr.world.getTree().traceRay(reflectedRay, tracer, new ShadeRec(sr.world));
			colorL.addEquals(c);
		} else {
			final Vector3d wt = new Vector3d(0, 0, 0);
			final Color ft = specularBTDF.sample_f(sr, wo, wt);
			final Ray transmittedRay = new Ray(sr.hitPoint, wt);
			transmittedRay.depth = sr.depth + 1;

			Color c = sr.world.getTree().traceRay(transmittedRay, tracer, new ShadeRec(sr.world));
			ft.multiplyEquals(c);
			ft.multiplyEquals(Math.abs(sr.normal.dot(wt)));
			colorL.addEquals(ft);
		}
		
		return colorL;
	}

}
