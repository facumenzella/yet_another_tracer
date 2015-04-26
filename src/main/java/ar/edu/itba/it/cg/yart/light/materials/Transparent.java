package ar.edu.itba.it.cg.yart.light.materials;

import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
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
		final Color fr = reflectiveBRDF.sample_f(sr, wo, wi);
		final Ray reflectedRay = new Ray(sr.hitPoint, wi);
		final double tMax = Double.POSITIVE_INFINITY;
		reflectedRay.depth = sr.depth + 1;
		List<GeometricObject> objects = sr.world.getObjects();
		if (specularBTDF.tir(sr)) {
			colorL.addEquals(tracer.traceRay(reflectedRay, objects, new ShadeRec(
					sr.world), tMax));
		} else {
			final Vector3d wt = new Vector3d(0, 0, 0);
			final Color ft = specularBTDF.sample_f(sr, wo, wt);
			final Ray transmittedRay = new Ray(sr.hitPoint, wt);
			transmittedRay.depth = sr.depth + 1;

//			colorL.addEquals(fr.multiply(
//					tracer.traceRay(reflectedRay, objects, new ShadeRec(sr.world), tMax))
//					.multiply(Math.abs(sr.normal.dot(wi))));
			colorL.addEquals(ft.multiply(
					tracer.traceRay(transmittedRay, objects, new ShadeRec(sr.world), tMax))
					.multiply(Math.abs(sr.normal.dot(wt))));
		}
		
		return colorL;
	}

}
