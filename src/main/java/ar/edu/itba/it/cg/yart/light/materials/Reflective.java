package ar.edu.itba.it.cg.yart.light.materials;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.brdf.PerfectSpecular;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class Reflective extends Phong implements Material{

	private final PerfectSpecular reflectiveBRDF = new PerfectSpecular();
	//private final Vector3d wi = new Vector3d(0,0,0);
	
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
	public Reflective setKr(final Color kr) {
		reflectiveBRDF.setKr(kr);
		return this;
	}
	public Reflective setKr(final Texture kr) {
		reflectiveBRDF.setKr(kr);
		return this;
	}

	@Override
	public Color shade(ShadeRec sr, final Stack stack) {
		Color colorL = super.shade(sr, stack);
		final double dx = -sr.ray.direction[0];
		final double dy = -sr.ray.direction[1];
		final double dz = -sr.ray.direction[2];

		final Vector3d wo = new Vector3d(dx, dy, dz);
		Vector3d wi = new Vector3d(0,0,0);
		Color fr = reflectiveBRDF.sample_f(sr, wo, wi);
		Ray reflectedRay = new Ray(sr.hitPoint, wi);
		reflectedRay.depth = sr.depth + 1;

		Color c = sr.world.getTree().traceRay(reflectedRay, new ShadeRec(sr.world), stack);
		
		final double factor = sr.normal.dot(wi);
		fr.r *= c.r * factor;
		fr.g *= c.g * factor;
		fr.b *= c.b * factor;

		colorL.r += fr.r;
		colorL.g += fr.g;
		colorL.b += fr.b;
		
		return colorL;
	}
}
