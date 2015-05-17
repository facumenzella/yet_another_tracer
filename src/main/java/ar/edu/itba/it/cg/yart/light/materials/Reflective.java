package ar.edu.itba.it.cg.yart.light.materials;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.brdf.PerfectSpecular;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.ColorTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleColorTracer;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class Reflective extends Phong {

	private final PerfectSpecular reflectiveBRDF = new PerfectSpecular();
	private final ColorTracer tracer = new SimpleColorTracer();
	
	public Reflective setKa(final double ka) {
		super.setKa(ka);
		return this;
	}

	public Reflective setKd(final double kd) {
		super.setKd(kd);
		return this;
	}

	public Reflective setKs(final double ks) {
		super.setKs(ks);
		return this;
	}

	public Reflective setCd(final Color cd) {
		super.setCd(cd);
		return this;
	}

	public Reflective setCd(final Texture cd) {
		super.setCd(cd);
		return this;
	}

	public Reflective setExp(final double exp) {
		super.setExp(exp);
		return this;
	}
	public Reflective setCr(final Texture cr) {
		reflectiveBRDF.setCr(cr);
		return this;
	}

	public Reflective setCr(final Color color) {
		final Texture texture = new ConstantColor(color);
		setCr(texture);
		return this;
	}

	public Reflective setKr(final double kr) {
		reflectiveBRDF.setKr(kr);
		return this;
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
